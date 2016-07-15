# Piano Jam Solo

This is an Android app that implements a simple piano that sends key press events
through a local pub-sub system. This pub-sub system implements a Publish interface
and Subscribe interface, and is intended to be replaced with a networked pub-sub
system that uses the Cogswell.io cloud messaging service.

# Piano Jam using Cogswell.io Tutorial
Before updating the Piano Jam Solo program to use Cogs you will need to set up an account, keys,
and campaign on the Cogswell.io website according to this [tutorial](CogswellTutorial.md).

## Program Subscriptions
First you will program the `Subscriptions` class.

### Make it implement methods from CogsSubscriptionHandler
The `CogsSubscriptionHandler` interface defines the methods that need to be implemented
in order to subscribe to a Cogs room over a WebSocket. Insert this code into the 'Subscriptions'
class at TODO 1:
```
@Override
public void message(CogsMessage message) {
  // Try to extract the key from the message.
  String key = message.getForwardedEvent().getAttributes().str("key");

  if (key != null){
    // If there is a key call Cogs.callback with it.
    Cogs.callback.call(key);
  } else {
    Logging.error("No key associated with message: " + message.getMessageId());
  }
}
```

## Program Cogs
Now you will implement the `Cogs` class.

### Configuration fields
Copy the following code to TODO 2 in the `Cogs` class:
```
private static String accessKey = "";
private static String clientSalt = "";
private static String clientSecret = "";
```
These lines are incomplete and need to have actual data added. These
values were created earlier when you created API and client keys on the Cogswell.io
website. Just copy them from the website or where you saved them.

### Make it implement methods from SubscribeInterface and PublishInterface
Now you will implement the required methods from `SubscribeInterface` and `PublishInterface`.

#### SubscribeInterface
The `SubscribeInterface` interface has two methods that must be implemented: `subscribe`
and `unsubscribe`. Copy the following to TODO 3 in the `Cogs` class:
```
      // Create a CogsSubscriptionRequest object containing the keys, namespace, and topic attributes.
      // These are necessary to route messages correctly at increasing levels of granularity.
      CogsSubscriptionRequest request;
      try {
        request = CogsSubscriptionRequest.builder()
                    .withAccessKey(accessKey)
                    .withClientSalt(clientSalt)
                    .withClientSecret(clientSecret)
                    .withNamespace(namespace)
                    .withTopicAttributes(attributes)
                    .build();
      } catch (Throwable t) {
        Logging.error("Error building subscription request.", t);
        throw new RuntimeException("Error building subscription request.", t);
      }

      try {
        // Call the GambitSDKService subscribe method.
        // This will subscribe the app with the Cogs topic and CogsSubscriptionHandler specified.
        cogsService.subscribe(request, subscriptions);
        bookkeepingCallback.call(room);
      } catch (Throwable t) {
        Logging.error("Error subscribing to push WebSocket.", t);
        bookkeepingCallback.call(null);
      }
```

Next you will copy the following to TODO 4 in the `Cogs` class:
```
      // Get the set of CogsSubscription objects from the GambitSDKService.
      Set<CogsSubscription> subscriptions = cogsService.getSubscriptions();

      for (final CogsSubscription subscription : subscriptions) {
        room = subscription.getTopicAttributesJson().str("room");
        // Call the GambitSDKService unsubscribe method on the CogsSubscription.
        // This will unsubscribe the app from the CogsSubscription specified.
        cogsService.unsubscribe(subscription, new io.cogswell.sdk.subscription.Callback<Boolean>() {
          public void call(Boolean stopped) {
            if (stopped) {
              String room = subscription.getTopicAttributesJson().str("room");
              Logging.info("Left room '" + room);
            }
          }
        });
      }

      bookkeepingCallback.call(room);
```

#### PublishInterface
The `PublishInterface` requires that one method be implemented, but you will also
create a helper method. Copy this into the `Cogs` class at TODO 5:
```
public static Future<GambitResponse> sendEvent(
  String namespace, JSONObject attributes, String eventName) {
    // Build a GambitRequestEvent.
    GambitRequestEvent.Builder builder = new GambitRequestEvent.Builder(
      accessKey, clientSalt, clientSecret);

    builder.setNamespace(namespace);
    builder.setAttributes(attributes);
    builder.setEventName(eventName);
    builder.setCampaignId(-1);
    builder.setTimestamp(TimeFormatter.isoNow());

    try {
      // Send the GambitRequestEvent to Cogs.
      return cogsService.sendGambitEvent(builder);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
}
```

Copy the following to TODO 6 in the `Cogs` class body:
```
      try {
        // Send the event using the sendEvent helper method and wait for the response.
        GambitResponse response = Cogs.sendEvent(namespace, attributes, eventName).get();
        int statusCode = response.getRawCode();

        if(response.isSuccess() && statusCode == 200) {
          bookkeepingCallback.call(room);
        } else {
          bookkeepingCallback.call(null);
        }
      } catch (Exception e){
        bookkeepingCallback.call(null);
      };
```

## Program PianoFragment
The final modifications you will make are to the `PianoFragment` class.

### Change publisher and subscriber to use Cogs
First, add the following code to TODO 7 in the body of the `PianoFragment` class:
```
private Cogs cogs = new Cogs();
private PublishInterface publisher = cogs;
private SubscribeInterface subscriber = cogs;
```
and remove the code:
```
private LocalPubSub pubsub = new LocalPubSub();
private PublishInterface publisher = pubsub;
private SubscribeInterface subscriber = pubsub;
```
Now the app will use Cogs instead of a local pub-sub component.

### Publish mode
You will need to change the following code at TODO 8
in the body of the `PianoFragment` class:
```
if(action == MotionEvent.ACTION_DOWN){
  Logging.info("Sending Key Event: " + key.getKeyName());
  sendKeyEvent(key);
}
```
to:
```
if(action == MotionEvent.ACTION_DOWN){
  if(isInPublishMode) {
    Logging.info("Sending Key Event: " + key.getKeyName());
    sendKeyEvent(key);
  } else {
    playSound(key.getKeyName());
    playColor(key.getKeyName());
  }
}
```

### Add controls
In order to allow the app to be more useful as a distributed application, you will
need to add in a couple of controls. You will need to delete several lines from the
"app/src/main/res/layout/activity_tab_layout.xml" file. Four components contain the
following code:
```
android:visibility="gone"
```
Remove this line where it appears in the file. This will change several UI components
from invisible to visible.

## Remove LocalPubSub
Now it is possible to remove the 'LocalPubSub' class, and the app should work fully
with Cogs as the pub-sub component.

