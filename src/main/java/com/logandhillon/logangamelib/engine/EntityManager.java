package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.api.Deferrable;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.physics.CollisionEntity;
import lombok.NonNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * Manages a group of entities and acts as a single source of truth for entity interaction. Entities should not be
 * managed without using this class, as this class handles safe/optimized entity management.
 * <p>
 * An entity manager can be closed to stop accepting new entities using the {@link #close()} method. This operation is
 * irreversible.
 *
 * @author Logan Dhillon
 */
final class EntityManager implements AutoCloseable {
    /**
     * Holds an instance of an entity, used to compare entities to their identity.
     */
    private record Entry(Entity entity) {}

    private final List<Entry>        entries       = new ArrayList<>();
    private final Map<Entity, Entry> active        = new IdentityHashMap<>();
    private final Queue<Entity>      pendingKills  = new ArrayDeque<>();
    private final Set<Entity>        queuedForKill = Collections.newSetFromMap(new IdentityHashMap<>());

    private final GameScene<?> owner;

    private int     passDepth;
    private Thread  passOwner;
    private boolean needsCompaction;
    private boolean open = true;

    EntityManager(@NonNull GameScene<?> owner) {
        this.owner = owner;
    }

    /**
     * Adds a new entity the manager. If {@link Entity#onAttach entity attachment} throws an exception, the entity will
     * not be added to this manager and the execption will be thrown to the stacktrace.
     *
     * @param entity entity to add
     *
     * @throws IllegalArgumentException attempted to add already attached entity
     * @throws RuntimeException         exception thrown during entity attachment.
     * @throws IllegalStateException    attempted to add entity while manager wasn't accepting additions; i.e., scene
     *                                  wasn't running
     */
    synchronized void add(@NonNull Entity entity) {
        if (!open)
            throw new IllegalStateException("Attempted to add entity to closed manager; is the scene running?");

        if (active.containsKey(entity))
            throw new IllegalArgumentException("Entity is already attached to this scene");

        Entry entry = new Entry(entity);
        active.put(entity, entry);
        entries.add(entry);

        try {
            owner.onEntityAdded(entity);
            entity.onAttach(owner);
        } catch (RuntimeException ex) {
            // remove entity if entity is active
            if (active.get(entity) == entry) {
                removeMembership(entity);
                try {
                    owner.onEntityRemoved(entity);
                } catch (RuntimeException cleanupFailure) {
                    ex.addSuppressed(cleanupFailure);
                }
            }
            if (passDepth == 0 && needsCompaction)
                compact();
            throw ex;
        }
    }

    /**
     * Marks an entity for removal; will be removed on next call to {@link flush} (i.e. next game tick).
     *
     * @param entity entity to kill/mark for removal
     */
    synchronized void kill(@NonNull Entity entity) {
        if (active.containsKey(entity) && queuedForKill.add(entity))
            pendingKills.add(entity);
    }

    /**
     * Marks all entities that match a given predicate for removal.
     *
     * @param filter returns true for each entity that should be removed
     *
     * @see kill
     */
    synchronized void killIf(@NonNull Predicate<Entity> filter) {
        try (var ignored = pass()) {
            for (Entry e: entries) {
                // skip retired entries
                if (active.get(e.entity) != e)
                    continue;

                // check if entry is still active after testing filter (i.e. side effects)
                if (filter.test(e.entity) && active.get(e.entity) == e)
                    kill(e.entity);
            }
        }
    }

    /**
     * Immediately removes all entries from the active list.
     *
     * @see remove
     */
    synchronized void clear() {
        try (var ignored = pass()) {
            for (Entry e: List.copyOf(entries)) remove(e);
        }
    }

    /**
     * Closes this entity manager: stops accepting new entities and clears the entity list. This operation is
     * irreversible.
     *
     * @see clear clearing the manager without closing
     */
    @Override
    public synchronized void close() {
        open = false;
        clear();
    }

    /**
     * Calls the {@link Entity#onUpdate(float)} method for each managed entity.
     *
     * @param dt delta time between game ticks
     */
    synchronized void update(float dt) {
        try (var ignored = pass()) {
            for (Entry entry: entries) {
                if (active.get(entry.entity) == entry)
                    entry.entity.onUpdate(dt);
            }
        }
    }

    /**
     * Calls the {@link Entity#render(Renderer)} method for each managed entity.
     *
     * @param renderer logangamelib render API
     */
    synchronized void render(Renderer renderer) {
        try (var ignored = pass()) {
            for (Entry entry: entries) {
                if (active.get(entry.entity) == entry)
                    entry.entity.render(renderer);
            }
        }
    }

    /**
     * Flushes the removal queue, removing all entities queued for removal from this manager.
     */
    synchronized void flush() {
        try (var ignored = pass()) {
            Entity entity;
            while ((entity = pendingKills.poll()) != null) {
                if (!queuedForKill.remove(entity))
                    continue;

                Entry entry = active.get(entity);
                if (entry != null)
                    remove(entry);
            }
        }
    }

    /**
     * Finds an active collision entity that matches a filter.
     *
     * @param filter only get collision entities matching this filter
     *
     * @return matching collision entities
     */
    synchronized CollisionEntity findCollision(@NonNull Predicate<CollisionEntity> filter) {
        try (var ignored = pass()) {
            for (Entry entry: entries) {
                if (active.get(entry.entity) != entry || !(entry.entity instanceof CollisionEntity collision)) continue;

                if (filter.test(collision) && active.get(entry.entity) == entry)
                    return collision;
            }
            return null;
        }
    }

    /**
     * Immediately removes an entry from the active list and its queue memberships
     *
     * @param entry entry to remove
     */
    private void remove(Entry entry) {
        Entity entity = entry.entity;

        if (active.get(entity) != entry)
            return;

        removeMembership(entity);
        try {
            owner.onEntityRemoved(entity);
        } catch (RuntimeException removalFailure) {
            try {
                entity.onDestroy();
            } catch (RuntimeException destroyFailure) {
                if (destroyFailure != removalFailure)
                    removalFailure.addSuppressed(destroyFailure);
            }
            throw removalFailure;
        }

        entity.onDestroy();
    }

    /**
     * Without removing an entry, removes an entity's membership to all queues and marks this entity manager for
     * compaction.
     *
     * @param entity entity to remove queue meberships of
     */
    private void removeMembership(Entity entity) {
        if (active.remove(entity) == null)
            return;

        queuedForKill.remove(entity);
        pendingKills.removeIf(queued -> queued == entity);
        needsCompaction = true;
    }

    /**
     * A pass is a safe way to interact with  {@link #entries}, ensuring the list is {@link #compact compacted} after a
     * pass finishes.
     * <p>
     * Passes are synchronous and must occur on one thread at a time. If another pass is requested from another thread,
     * it will have to wait for the pass on the first thread to finish.
     *
     * @return deferable method to compact entries if needed
     *
     * @throws IllegalStateException thrown if thread is interrupted while waiting for another thread to finish their
     *                               pass
     * @apiNote Should be run in a try-with-resources every time {@link #entries} is accessed.
     */
    public synchronized Deferrable pass() {
        Thread current = Thread.currentThread();
        while (passDepth > 0 && passOwner != current) {
            try {
                wait();
            } catch (InterruptedException ex) {
                current.interrupt();
                throw new IllegalStateException("Interrupted while waiting for entity manager pass", ex);
            }
        }

        if (passDepth == 0)
            passOwner = current;
        passDepth++;

        return new Deferrable() {
            private boolean closed;

            @Override
            public void close() {
                synchronized (EntityManager.this) {
                    if (closed) return;

                    if (passOwner != current)
                        throw new IllegalStateException("Entity manager pass must be closed by its owning thread");

                    closed = true;
                    if (--passDepth == 0) {
                        try {
                            if (needsCompaction) compact();
                        } finally {
                            passOwner = null;
                            EntityManager.this.notifyAll();
                        }
                    }
                }
            }
        };
    }

    /**
     * Compacts the entry list, removing stale and retired entries from the list in-place.
     */
    private void compact() {
        int write = 0;

        for (int read = 0; read < entries.size(); read++) {
            Entry entry = entries.get(read);
            if (active.get(entry.entity) == entry)
                entries.set(write++, entry);
        }

        entries.subList(write, entries.size()).clear();
        needsCompaction = false;
    }
}