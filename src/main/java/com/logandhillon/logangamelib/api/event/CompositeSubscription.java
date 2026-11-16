package com.logandhillon.logangamelib.api.event;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite subscription is an auto-closable list of subscriptions that unsubscribes from the all when closed.
 *
 * @author Logan Dhillon
 */
public class CompositeSubscription implements AutoCloseable {
    private final List<Subscription> subscriptions = new ArrayList<>();

    public void add(Subscription subscription) {
        if (subscription != null) subscriptions.add(subscription);
    }

    @Override
    public void close() {
        for (Subscription subscription: subscriptions) subscription.close();
        subscriptions.clear();
    }
}
