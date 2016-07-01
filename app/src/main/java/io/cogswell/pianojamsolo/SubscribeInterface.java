package io.cogswell.pianojamsolo;

/**
 * The interface that defines the methods a class must implement
 * to subscribe to or unsubscribe from a room.
 */
interface SubscribeInterface {
    /**
     * The method to unsubscribe from the current room in the pub-sub system.
     *
     * @param bookkeepingCallback A user supplied Callback that takes the unsubscribed room name.
     */
    void unsubscribe(final Callback<String> bookkeepingCallback);

    /**
     * The method to subscribe to a room in the pub-sub system.
     *
     * @param room                  The name of a room that indicates which publish
     *                              calls will send a message to this subscriber.
     * @param actionCallback        A user supplied Callback that takes a key name that arrives
     *                              via a published message.
     *
     * @param bookkeepingCallback    A callback to be invoked with the name of
     *                              the room to which there is a successful
     *                              subscription.
     */
    void subscribe(
            final String room,
            final Callback<String> actionCallback,
            final Callback<String> bookkeepingCallback
    );
}
