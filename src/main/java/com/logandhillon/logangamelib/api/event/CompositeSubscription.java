package com.logandhillon.logangamelib.api.event;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite subscription is an auto-closable list of subscriptions that unsubscribes from the all when closed.
 *
 * @author Logan Dhillon
 * @see Subscription
 */
public class CompositeSubscription implements AutoCloseable {
    private final List<Subscription> subscriptions = new ArrayList<>();

    public void add(Subscription subscription) {
        if (subscription != null) subscriptions.add(subscription);
    }

    /**
     * Attempts to close all subscriptions in the composite and unsubscribes from them all.
     *
     * @throws RuntimeException propagated subscription failure(s)
     */
    @Override
    public void close() {
        RuntimeException failure = null;
        try {
            for (Subscription subscription: subscriptions) {
                try {
                    subscription.close();
                } catch (RuntimeException e) {
                    if (failure == null) failure = e;
                    else failure.addSuppressed(e);
                }
            }
        } finally {
            subscriptions.clear();
        }

        if (failure != null) throw failure;
    }
}
