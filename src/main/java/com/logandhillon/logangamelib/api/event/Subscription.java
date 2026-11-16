package com.logandhillon.logangamelib.api.event;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Subscription implements AutoCloseable {
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Runnable      unsubscribe;

    public Subscription(Runnable unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) unsubscribe.run();
    }
}