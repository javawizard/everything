This document describes how to get BZNetwork up and running.

First things first. Here's what you'll need:

    A Linux box (untested on cygwin but it might work, and if anyone 
        wants to port this to windows I'd be glad to accept the 
        changes into the repository)
    Java 6 or later (untested on OpenJDK)
    Apache Tomcat 6 or later (should work on Tomcat 5 but I haven't tested it)
    BZFS (the BZFlag server, 2.0.10 or later, with shared library support enabled)
    
These are optional:
    
    MySQL (BZNetwork can use an embedded database if you don't have MySQL)
    
I'm going to assume you know how to get Java, Tomcat, and BZFS set up. I'm also going to assume that
you have Tomcat set up at /apache/tomcat, and that you haven't changed the default configuration (so 
the webapp root would be at /apache/tomcat/webapps and tomcat would be listening on port 8080).

Anyway, let's get started. Copy the bznetwork folder in the archive you downloaded (which should be
the folder that contains this README.txt file) into /apache/tomcat/webapps. Make sure tomcat is not
running when you do this, as it has a tendency to try to activate the webapp if it's running, which
would result in the webapp being made live when you haven't copied the whole thing over yet. Once
you've copied the folder to there, start tomcat.

Open a browser and go to http://localhost:8080/bznetwork. You should be presented with an installation
screen. Unless you want to configure BZNetwork to use MySQL, just click Install. This will, as the
install page describes, use an embedded database. You won't want to use the embedded database for
production installs as deploying a new version of BZNetwork would overwrite the embedded database's
data.

That's it! You now have BZNetwork up and running. Refer to the Getting Started guide at
http://code.google.com/p/bznetwork/wiki/GettingStarted if you need to know what to do now.



If you have any problems getting BZNetwork up and running, or if you have any questions, join
#bztraining on irc.freenode.net and ask jcp.