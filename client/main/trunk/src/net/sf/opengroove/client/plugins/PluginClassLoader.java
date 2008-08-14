package net.sf.opengroove.client.plugins;

/**
 * This class is used to load plugin classes. A PluginClassLoader is created for
 * each plugin (except for internal ones), and this class loader is used to load
 * all of the plugin's classes.<br/><br/>
 * 
 * When the plugin manager loads, it extracts the contents of the plugins to a
 * temporary folder (after deleting any files that may still be in that folder
 * from the last time OpenGroove was run), and performs general tasks such as
 * validating plugin signatures, etc. Some time after, it creates an instance of
 * this class for each plugin that is not an internal plugin. For internal
 * plugins, it uses the class loader used to load the plugin manager, which
 * should be the bootstrap class loader and therefore the class loader used to
 * load the OpenGroove classes themselves. Anyway, the plugin manager creates an
 * instance of this class, passing into it the location of the folder that it
 * extracted the plugin to. The plugin manager then creates class loaders for
 * all the other plugins. It then calls addDependancy() on each of these class
 * loaders to register the other class loaders upon which this class loader is
 * dependant. After this is done, the class loader is ready for use. When a
 * class is to be resolved, it first looks in the bootstrap class loader for an
 * appropriate class. If it finds one, it returns it. If it does not, it then
 * looks in it's cache of loaded classes (stored as a hashtable). If it finds
 * the class there, it returns it. If not, it checks to see if the class
 * specified corresponds to a class file within it's plugin's folder. If there
 * is such a class file, it loads it as a class, sticks it into the hashtable,
 * and returns it. If there isn't, then it calls each of it's dependency class
 * loaders to see if they have the class, specifying that they are to only
 * return their classes, and not themselves check their dependancies. If a class
 * is found there, it is returned. If no classes could be found in the
 * dependancies, then a ClassNotFoundException is thrown.
 * 
 * @author Alexander Boyd
 * 
 */
public class PluginClassLoader extends ClassLoader
{
}
