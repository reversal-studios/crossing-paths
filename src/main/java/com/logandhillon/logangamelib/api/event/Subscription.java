package com.logandhillon.logangamelib.api.event;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An atomic, auto-closable subscription that unsubscribes from what it was created for when closed.
 * <p>
 * Used to create memory-safe subscriptions that can close themselves when done.
 *
 * @see Subscription#close()
 */
public final class Subscription {
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Runnable      unsubscribe;

    /**
     * Creates a subscription.
     *
     * @param unsubscribe what to run when closed/unsubscribed
     */
    public Subscription(Runnable unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    public void close() {
        if (closed.compareAndSet(false, true)) unsubscribe.run();
    }
}