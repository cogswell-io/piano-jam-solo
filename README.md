# Piano Jam Solo

This is an Android app that implements a simple piano that sends key press events
through a local pub-sub system. This pub-sub system implements a Publish interface
and Subscribe interface, and is intended to be replaced with a networked pub-sub
system that uses the Cogswell.io cloud messaging service. The tutorial for
including Cogswell.io can be found at:
