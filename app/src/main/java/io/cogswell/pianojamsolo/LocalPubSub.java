package io.cogswell.pianojamsolo;

/**
 * LocalPubSub is the dummy pub-sub implementation that uses method calls
 * to imitate the Cogswell.io pub-sub system. Instead of sending events to
 * Cogswell.io and calling callbacks on key string extracted from received
 * messages, it just calls the callbacks directly on the keys extracted
 * from the publish call that would otherwise create and send an event.
 */
public class LocalPubSub implements PublishInterface, SubscribeInterface {
    private static Callback<String> callback = null;
    private static String currentRoom = null;

    @Override
    public void publish(String room, String key, Callback<String> bookkeepingCallback){
        LocalPubSub.callback.call(key);
        bookkeepingCallback.call(room);
    }

    @Override
    public void unsubscribe(final Callback<String> bookkeepingCallback){
        LocalPubSub.callback = null;
        bookkeepingCallback.call(currentRoom);
        currentRoom = null;
    }

    @Override
    public void subscribe(
            final String room,
            final Callback<String> actionCallback,
            final Callback<String> bookkeepingCallback){
        LocalPubSub.callback = actionCallback;
        currentRoom = room;
        bookkeepingCallback.call(room);
    }
}