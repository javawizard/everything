========================================================================
    DYNAMIC LINK LIBRARY : testplugin Project Overview
========================================================================

This plugin allows plugins to be written in the Java programming
language. It requires a pre-installed Java VM.

Plugins are written as java classes that have two static methods, 
load(String) and unload(). These methods can call methods in 
org.bzflag.jzapi.BzfsAPI to interact with the BZFS API.

If a plugin uses threads, it should make sure that all of it's threads
are dead upon unload() returning. In particular, marking a thread as a
daemon is not sufficient, since the same Java VM is used to run multiple
java plugins. If threads persist beyond the time that unload() is 
called, then they will stay around until bzfs shuts down, and may 
prevent bzfs from shutting down at all if they are not daemon threads.

Throughout the rest of this file, the native plugin that allows java 
plugins to run will be referred to as the java native plugin, and
plugins written in java will be referred to as java plugins.

The java native plugin should be loaded before any java plugins, so that
it can register a custom plugin handler to load java plugins. The java
native plugin takes one parameter, the classpath to use. This can 
contain as many classpath elements as are needed for the particular
environment in which the plugin is run. On windows, classpath elements
are delimited by semicolons; on linux, they are delimited by colons. For
example, the following would load the java native plugin, with a 
classpath that expects classes to be located in the classes folder:

bzfs -loadplugin testplugin.dll,classes

The java native plugin does not include it's own classes (such as
org.bzflag.jzapi.BzfsAPI) on the classpath, so those will need to be
provided. Typically, these classes are included in javaplugin.jar, so
adding that entry to the classpath would suffice. For example:

bzfs -loadplugin testplugin.dll,classes;javaplugin.jar

To load java plugins, the fully-qualified class name should be passed as
the argument to the -loadplugin switch, with period characters replaced
by forward slashes (even on windows), and with .class at the end. For 
example, the following loads the PlayerJoinListenPlugin, which is 
included with the java native plugin (hence why javaplugin.jar is the 
only entry on the classpath):

bzfs -loadplugin testplugin.dll,javaplugin.jar -loadplugin org/bzflag/jzapi/examples/PlayerJoinListenPlugin.class

This works even if the plugin's class file is not stored on disk; .class
simply serves to tell bzfs that this is a java plugin.














