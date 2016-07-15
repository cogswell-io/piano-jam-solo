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
tutorial, "Piano Jam" will be the name of the campaign.
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
