package io.cogswell.pianojamsolo;

/**
 * The interface that defines the methods a class must implement
 * to publish events.
 */
interface PublishInterface {
    /**
     * The method to publish events to the pub-sub system resulting from pressing a piano key.
     *
     * @param room                  The name of a room that indicates which subscribers
     *                              will receive a message initiated from the publish
     *                              call.
     * @param key                   The name of key that was pressed which needs to be
     *                              included in any initiated messages.
     * @param bookkeepingCallback   A callback to be invoked with the name of
     *                              the room to which there is a successful
     *                              publish.
     */
    void publish(String room, String key, Callback<String> bookkeepingCallback);
}