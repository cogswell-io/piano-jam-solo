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
    // TODO 2: Insert Cogs tutorial code here
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

        cogsService.execute(new Runnable() {
            @Override
            public void run() {
                // Create a JSON object to specify the topic.
                JSONObject attributes = new JSONObject();

                try {
                    // Add the attributes that specify the topic.
                    attributes.put("room", room);
                } catch (JSONException e) {
                    Logging.error("Error assembling topic attributes.", e);
                    throw new RuntimeException("Error assembling topic attributes.", e);
                }
            }

            // TODO 3: Insert Cogs tutorial code here
        });
    }

    @Override
    public void unsubscribe(final Callback<String> bookkeepingCallback) {
        cogsService.execute(new Runnable() {
            @Override
            public void run() {
                String room = null;

                // TODO 4: Insert Cogs tutorial code here
            }
        });
    }

    // TODO 5: Insert Cogs tutorial code here

    @Override
    public void publish(final String room, String key, final Callback<String> bookkeepingCallback) {
        final JSONObject attributes = new JSONObject();

        try {
            attributes.put("room", room);
            attributes.put("key", key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        cogsService.execute(new Runnable() {
            @Override
            public void run() {
                // TODO 6: Insert Cogs tutorial code here
            }
        });
    }
}