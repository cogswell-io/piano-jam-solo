package io.cogswell.pianojamsolo;

import io.cogswell.sdk.subscription.CogsMessage;
import io.cogswell.sdk.subscription.CogsSubscriptionHandler;

public class Subscriptions implements CogsSubscriptionHandler {
    private static Subscriptions instance;

    private Subscriptions(){};

    public static Subscriptions getInstance() {
        if (instance == null) {
            instance = new Subscriptions();
        }

        return instance;
    }

    @Override
    public void error(Throwable t) {
        Logging.error("WebSocket Error: ", t);
    }

    @Override
    public void connected() {
        Logging.info("WebSocket established.");
    }

    @Override
    public void message(CogsMessage message) {

    }

    @Override
    public void closed(Throwable error) {
        if (error != null) {
            Logging.error("WebSocket closed due to error.", error);
        } else {
            Logging.info("WebSocket closed without error.");
        }
    }

    @Override
    public void replaced() {
    }
}
