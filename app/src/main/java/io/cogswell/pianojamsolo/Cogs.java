package io.cogswell.pianojamsolo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.concurrent.Future;

import io.cogswell.sdk.GambitResponse;
import io.cogswell.sdk.GambitSDKService;
import io.cogswell.sdk.request.GambitRequestEvent;
import io.cogswell.sdk.subscription.CogsSubscription;
import io.cogswell.sdk.subscription.CogsSubscriptionHandler;
import io.cogswell.sdk.subscription.CogsSubscriptionRequest;

public class Cogs implements PublishInterface, SubscribeInterface {

    private static String accessKey = "";
    private static String clientSalt = "";
    private static String clientSecret = "";
    private static String namespace = "pianojam";
    private static String eventName = "Key Pressed";
    private static Subscriptions subscriptions = Subscriptions.getInstance();
    private static GambitSDKService cogsService = GambitSDKService.getInstance();
    public static Callback<String> callback = null;

    @Override
    public void subscribe(final String room,
                          final Callback<String> actionCallback,
                          final Callback<String> bookkeepingCallback) {
        callback = actionCallback;
    }

    @Override
    public void unsubscribe(final Callback<String> callback) {}

    @Override
    public void publish(final String room, String key, final Callback<String> bookkeepingCallback) {
        final JSONObject attributes = new JSONObject();

        try {
            attributes.put("room", room);
            attributes.put("key", key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}