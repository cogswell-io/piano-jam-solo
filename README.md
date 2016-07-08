# Piano Jam Solo

This is an Android app that implements a simple piano that sends key press events
through a local pub-sub system. This pub-sub system implements a Publish interface
and Subscribe interface, and is intended to be replaced with a networked pub-sub
system that uses the Cogswell.io cloud messaging service.

# Piano Jam using Cogswell.io Tutorial

## Create Campaign
Before editing the Piano Jam Solo app to produce a Cogs enabled app, you will need to
go to https://cogswell.io to set up an account and create a campaign.

### Account
+ Click on the "Sign Up" button on the header of the page to navigate to the sign up page.
+ Sign up and verify your e-mail address. 
+ Click on the login link to log into your account. If you already have an account you can skip
the previous steps and simply log in. 

Once logged in you can go through the QuickStart guide or use the Setup link
on the left to navigate to the pages needed.

### API Keys
+ Click on the "API Keys" link under the "Setup" menu, or if you are using the QuickStart guide
you will start at the "API Keys" tab. 

The API keys are how an application authenticates
that it has permission to use your account on Cogswell.io. Do not publicly expose your keys; 
they should be kept secret. 
+ Choose a name for your key pair and click the "Create Keys" button. 

The keys will be available from the "My Key Pairs" section of the "API Keys" page. 
Just click on the entry and a pop-up will display them.

### Namespace
+ Click on the "Namespaces" link or tab to create a namespace for the application. In the
tutorial this will be named "pianojam". 
+ Enter the name in the "Namespace Name" field and click next. 

Next you will create two namespace attributes. Events that are sent to Cogs 
for your account will be evaluated based on the values that they have for these attributes. 
+ Create an attribute with name "room" and select the data type "text" from the drop down. 
+ Click the "Add Attribute" button to see the new attribute listed. 
+ Click the "Make part of primary key?" check box so that it is checked. 
+ Create another attribute with the name "key" and the data type "text".
+ Click the "Add Attribute" button. Do not make this attribute part of the
primary key.

### Campaign
+ In the control panel to the left choose "pianojam" from the top dropdown underneath
the "Select Namespace" text. 
+ Click on the "New Campaign" link on the left. 

You will now create the campaign that will send messages to the Piano Jam application in response to 
incoming events. 
+ Choose a campaign name and enter it in the "Campaign Name" field. In the 
tutorial, "Piano Jam" will be the name of the campaign, but it can be named anything you like. 
+ Click next and go to the "Content" tab. You will need to check "No URL" and enter "Key Pressed" 
in the "Notification Message" field. 
+ Click the "Send Triggering Event" slider so that
it turns green. Sending the triggering event will allow the value of the key pressed to
be extracted from the message. 
+ Go to the "Audience" tab and select "Show anytime a
qualifying trigger is detected" for the message frequency. Then select one minute for
the message expiration value. 
+ Verify that the "Show to" box for "Whomever triggers this
campaign" is checked so that any device subscribing to the same topic as the triggering client
will be sent a message. The topic is defined by the primary key namespace attributes
chosen earlier. In this case the "room" attribute.

+ Continue on to the "Rules" page. This is where you will specify when to send messages
by triggering the campaign. 
+ Enter a description in the "Rule Description" field. This
can be anything you like. 
+ In the filters section choose "Newest/Last" for the "Reduction"
value for an event filter, and 1 for the value. Choose "Events" as the unit, and click
"Add Event Filter" to save the filter. 
+ In the conditions section choose attribute "key",
reduction "Newest", and operator "has any value", and click the "Add Condition" button.
+ Click the "Add Rule" button, and then finally click the "Finish" button to create the 
campaign.

### Client Keys
The final setup step is to generate client keys. In an app that is distributed to
customers the developers API keys should not be exposed. Therefore Cogs can create derived
keys for distribution to clients. 
+ Click on the "Client Keys" link under the setup menu.
+ Select the API key pair you created earlier from the "Create Client Keys from" selector.
+ Click "Create Keys" to create a new client key pair. 

Client keys are not saved, so make sure to
copy and paste them somewhere for the time being or keep this page open. You will need the client
secret and salt pair for later in the tutorial.

## Include Cogs Android Client SDK
In order to access the Cogswell.io service, you will need to include the Cogs Android
Client SDK in the app. In the "build.gradle" file under the "app/" directory place the line:
```
compile 'io.cogswell:cogs-android-client-sdk:1.0.29'
```
into the `dependencies` section.
This will cause Gradle to download the Cogs Android Client SDK and include it in
the app. There is another "build.gradle" file under the "gradle" directory, but it
helpfully advises against placing dependencies in it. After you have modified your build.gradle
file be sure to build the project to have access to the SDK's classes.

## Create Classes
Now you begin altering the code of the Piano Jam Solo app. First you will add two new
classes.

### Subscriptions
The first new class - `Subscriptions` - implements `CogsSubscriptionHandler` and is
used as an interface to the Cogs SDK message handling. Create a new file named "Subscriptions.java"
in the "app/src/main/java/io/cogswell/pianojamsolo" directory. Copy the following code into it:
```
package io.cogswell.pianojamsolo;

import io.cogswell.sdk.subscription.CogsMessage;
import io.cogswell.sdk.subscription.CogsSubscriptionHandler;

public class Subscriptions implements CogsSubscriptionHandler {

}
```

### Cogs
The second new class - `Cogs` - implements the `PublishInterface` and `SubscribeInterface`
interfaces. Create a new file named "Cogs.java" in the "app/src/main/java/io/cogswell/pianojamsolo"
directory. Copy the following code into it:
```
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

}
```

## Program Subscriptions
Now you will program the `Subscriptions` class.

### Make it a Singleton
First you need to make the `Subscriptions` class into a singleton. Add the following code inside
the `Subscriptions` definition:
```
private static Subscriptions instance;

  private Subscriptions(){};

  public static Subscriptions getInstance() {
    if (instance == null) {
      instance = new Subscriptions();
    }

    return instance;
  }
```
This code ensures that there is at most one instance of the `Subscriptions` class, and
that it is always accessible by using the `getInstance` method.

### Make it implement methods from CogsSubscriptionHandler
The `CogsSubscriptionHandler` interface defines the methods that need to be implemented
in order to subscribe to a Cogs room over a WebSocket. Add the following code inside
the `Subscriptions` definition:
```
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
  String key = message.getForwardedEvent().getAttributes().str("key");

  if (key != null){
    Cogs.callback.call(key);
  } else {
    Logging.warn("No key associated with message: " + message.getMessageId());
  }
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
```
Looking at this implementation you can see that most of the methods either do nothing
or log messages. The only method that does anything of significance is the `message`
method. This method is called when a message arrives over the WebSocket. It
attempts to extract the key from the message, and if successful call the `call`
method of the `Cogs.callback` object with the key as a parameter. This is not
yet implemented, but it will play the sound and alter the color of the key.

## Program Cogs
Now you will implement the `Cogs` class.

### Add configuration fields
Add the following code to the body of the `Cogs` class:
```
private static String accessKey = "";
private static String clientSalt = "";
private static String clientSecret = "";
private static String namespace = "pianojam";
private static String eventName = "Key Pressed";
private static Subscriptions subscriptions = Subscriptions.getInstance();
private static GambitSDKService cogsService = GambitSDKService.getInstance();
public static Callback<String> callback = null;
```
The first three of these are incomplete and need to have actual data added. These
values were created earlier when you created API and client keys on the Cogswell.io
website. Just copy them from the website. The 'namespace' and 'eventName' variables
are the "Namespace Name" and "Notification Method" fields from your earlier set
up on the Cogswell.io website. If you chose different values there you will need
to set these variables to those values. After that you get instances of the
`Subscriptions` class you just created, and the `GambitSDKService` from the Cogs
Android Client SDK. This class contains methods for interacting with Cogs.
Finally the `callback` variable is created to hold a callback to call when messages
are received from Cogs.

### Make it implement methods from SubscribeInterface and PublishInterface
Now you will implement the required methods from `SubscribeInterface` and `PublishInterface`.

#### SubscribeInterface
The `SubscribeInterface` interface has two methods that must be implemented: `subscribe`
and `unsubscribe`. Copy the following into the body of the `Cogs` class:
```
@Override
public void subscribe(final String room,
                      final Callback<String> actionCallback,
                      final Callback<String> bookkeepingCallback){
  callback = actionCallback;
  cogsService.execute(new Runnable() {
    @Override public void run() {
      JSONObject attributes = new JSONObject();
      try {
        attributes.put("room", room);
      } catch (JSONException e) {
        Logging.error("Error assembling topic attributes.", e);
        throw new RuntimeException("Error assembling topic attributes.", e);
      }

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
        cogsService.subscribe(request, subscriptions);
      } catch (Throwable t) {
        Logging.error("Error subscribing to push WebSocket.", t);
      } finally {
        bookkeepingCallback.call(room);
      }
    }
  });
}
```
The static `callback` field is assigned the `actionCallback` parameter. This allows
the class to handle what method to call when a message is received. The `subscribe`
method then creates an `attributes` JSON object that specifies the topic
that will be subscribed to. This lets a single namespace route messages for multiple
users, devices, applications, etc. Next it creates a `CogsSubscriptionRequest` that
contains the necessary keys, namespace, and topic attributes. These are needed to route
messages correctly at increasing levels of granularity. Then the `subscribe` method
from the `GambitSDKService` is called taking the request you just built and the
`Subscriptions` object that you previously implemented. Finally the `bookkeepingCallback`
is called with the room subscribed to.

Next you will copy this code to the body of the `Cogs` class:
```
@Override
public void unsubscribe(final Callback<String> callback){
  cogsService.execute(new Runnable() {
    @Override public void run() {
      String room = null;
      Set<CogsSubscription> subscriptions = cogsService.getSubscriptions();

      for (final CogsSubscription subscription : subscriptions) {
        room = subscription.getTopicAttributesJson().str("room");
        cogsService.unsubscribe(subscription, new io.cogswell.sdk.subscription.Callback<Boolean>() {
          public void call(Boolean stopped) {
            if (stopped) {
              String room = subscription.getTopicAttributesJson().str("room");

              Logging.info("Left room '" + room);
            }
          }
        });
      }

      callback.call(room);
    }
  });
}
```
The `unsubscribe` method calls the `unsubscribe` method of the `GambitSDKService`, but it
does it in a circuitous manner. Because you need a `CogsSubscription` object and the
`GambitSDKService` returns a set of them, it is necessary to loop through them and call
`unsubscribe`. Because the app only has one subscription at a time this is a bit of
extra code but no real extra work. A callback is invoked after subscription to log
information about leaving the room.

#### PublishInterface
The `PublishInterface` requires that one method be implemented, but you will also
create a helper method. Copy this into the `Cogs` class body:
```
public static Future<GambitResponse> sendEvent(
  String namespace, JSONObject attributes, String eventName) {
    GambitRequestEvent.Builder builder = new GambitRequestEvent.Builder(
      accessKey, clientSalt, clientSecret);

    builder.setNamespace(namespace);
    builder.setAttributes(attributes);
    builder.setEventName(eventName);
    builder.setCampaignId(-1);
    builder.setTimestamp(TimeFormatter.isoNow());

    try {
      return cogsService.sendGambitEvent(builder);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
}
```
This method is the helper method uses the `GambitSDKService` to send an event to
Cogs. Because `sendGambitEvent` returns a Future, this will be implemented as a helper method.
Now copy this into the `Cogs` class body:
```
@Override
public void publish(final String room, String key, final Callback<String> bookkeepingCallback){
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
      try {
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
    }
  });
}
```
This method implements the `publish` method from the `PublishInterface`. First it
creates an `attributes` JSON object, then it calls the helper method you just created
on a new thread passing in the namespace, attributes, and event name; and waits for the
response from the Future.

## Program PianoFragment
The final modifications you will make are to the `PianoFragment` class.

### Add necessary imports
```
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
```

### Change publisher and subscriber to use Cogs
First, add the following code to the body of the `PianoFragment` class:
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

### Add controls
In order to allow the app to be more useful as a distributed application, you will
need to add in a couple of controls and their backing logic. The first thing to do
is to delete a few lines fron the "app/src/main/res/layout/activity_tab_layout.xml" 
file. Several components contain the following code:
```
android:visibility="gone"
```
Remove this line wherever it appears in the file. This will change several UI components
from invisible to visible.

#### Publish mode
You will need to add the following code to the `PianoFragment` class body:

```
private boolean isInPublishMode = false;
```
and the following into the `onCreateView` method:
```
final Switch pubSubSwitch = (Switch) getActivity().findViewById(R.id.pubSubSwitch);

pubSubSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    isInPublishMode = isChecked;
  }
});
```
Next you will need to change the following code inside the View's `TouchListener`:
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
This makes the key presses publish to Cogs when in publish mode, but just directly
call the play methods when not in publish mode.

#### Subscriptions
To allow subscriptions to rooms that are not the default add the following to the PianoFragment class inside
of the onCreateView method:
```
final Button roomSubscribeButton = (Button) getActivity().findViewById(R.id.subscribeButton);
final EditText userSelectedRoomName = (EditText) getActivity().findViewById(R.id.userSelectedRoomName);
final String roomButtonTextSubscribe = "Subscribe";
final String roomButtonTextDisplay = "Room";

roomSubscribeButton.setOnClickListener(new View.OnClickListener() {
  public void onClick(View v) {
    final String newRoomName = userSelectedRoomName.getText().toString();
    Log.d("PianoFragment", "Subscribing to " + newRoomName);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    sharedPreferences.edit().putString("room", newRoomName).apply();
    roomSubscribeButton.setText(roomButtonTextDisplay);
    sub(newRoomName);
  }
});

SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
String oldRoomName = sharedPreferences.getString("room", null);

if (oldRoomName==null) {
  oldRoomName = defaultRoomName;
  sharedPreferences.edit().putString("room", defaultRoomName).apply();
}

sub(oldRoomName);

userSelectedRoomName.setText(oldRoomName);
userSelectedRoomName.addTextChangedListener(new TextWatcher() {
  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String oldRoomName = sharedPreferences.getString("room", null);
    String newRoomName = s.toString();
    if (!newRoomName.equals(oldRoomName)) {
      roomSubscribeButton.setText(roomButtonTextSubscribe);
      sharedPreferences.edit().putString("room", newRoomName).apply();
    }
  }

  @Override
  public void afterTextChanged(Editable s) {}
});
```
while deleting:
```
sub(defaultRoomName);
```
Most of this code deals with Android UI components. The only thing to note is that
the `sub` method gets called in the `roomSubscribeButton` click listener.

## Remove LocalPubSub
Now it is possible to remove the 'LocalPubSub' class, and the app should work fully
with Cogs as the pub-sub component.

