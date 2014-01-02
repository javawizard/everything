package net.sf.opengroove.client.oldplugins;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.storage.Storage;

/**
 * This class is used to load plugin classes. A PluginClassLoader is created for
 * each plugin (except for internal ones), and this class loader is used to load
 * all of the plugin's classes.<br/><br/>
 * 
 * When the plugin manager loads, it performs general tasks such as validating
 * plugin signatures, etc. Some time after, it creates an instance of this class
 * for each plugin that is not an internal plugin. For internal plugins, it uses
 * the class loader used to load the plugin manager, which should be the
 * bootstrap class loader and therefore the class loader used to load the
 * OpenGroove classes themselves. Anyway, the plugin manager creates an instance
 * of this class, passing into it the location of the jar file that contains the
 * plugin. The plugin manager then creates class loaders for all the other
 * plugins. It then calls addDependancy() on each of these class loaders to
 * register the other class loaders upon which this class loader is dependant.
 * After this is done, the class loader is ready for use. When a class is to be
 * resolved, it first looks in the bootstrap class loader for an appropriate
 * class. If it finds one, it returns it. If it does not, it then looks in it's
 * cache of loaded classes (stored as a hashtable). If it finds the class there,
 * it returns it. If not, it checks to see if the class specified is in the
 * plugin's jar file. If it's there, it loads it, sticks it into the hashtable,
 * and returns it. If it isn't, then it calls each of it's dependency class
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
    private Plugin[] allPlugins;
    private Plugin plugin;
    private Map<String, Class> internalPool = new Hashtable<String, Class>();
    
    public PluginClassLoader(Plugin[] plugins, Plugin plugin)
    {
        super(PluginClassLoader.class.getClassLoader());
        this.allPlugins = plugins;
        this.plugin = plugin;
    }
    
    private synchronized Class resolveInternal(
        String className)
    {
        if (internalPool.get(className) != null)
            return internalPool.get(className);
        JarFile file = plugin.getModel().getJarFile();
        JarEntry entry = file.getJarEntry(className
            .replace(".", "/")
            + ".class");
        if (entry == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            Storage.copy(file.getInputStream(entry), baos);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        byte[] bytes = baos.toByteArray();
        Class c = defineClass(className, bytes, 0,
            bytes.length);
        internalPool.put(className, c);
        return c;
    }
    
    @Override
    protected Class<?> findClass(String name)
        throws ClassNotFoundException
    {
        /*
         * When this is called, the vm classes and the opengroove classes have
         * already been searched. At this point, we need to search the plugin's
         * jar file.
         */
        Class c = resolveInternal(name);
        if (c != null)
            return c;
        /*
         * OpenGroove's classes, the vm classes, and this plugin's classes have
         * been searched, without success. Now we search the plugin's
         * dependencies. We start at the first dependency, and attempt to find
         * it's corresponding plugin in the plugin list. If it is found, and has
         * a plugin class loader, we call resolveInternal. We do this for each
         * dependency declared.
         */
        for (DependencyModel dependency : plugin.getModel()
            .getDependencies())
        {
            Plugin dependedPlugin = null;
            for (Plugin p : allPlugins)
            {
                if (p.getModel().getId().equals(
                    dependency.getPlugin()))
                {
                    dependedPlugin = p;
                    break;
                }
            }
            if (dependedPlugin == null)
                /*
                 * This means that the dependency is not present. This usually
                 * occurs if the dependency is optional and the user has not
                 * chosen to install it.
                 */
                continue;
            if (dependedPlugin.getClassLoader() == null)
                /*
                 * This means that the plugin doesn't have a class loader. The
                 * only practical reason for this to occur would be that the
                 * plugin is an internal plugin, in which case it's classes
                 * would have already been searched
                 */
                continue;
            /*
             * Now let's see if the dependency knows about the class. If it
             * does, then we return it. The dependency's class loader caches the
             * loaded classes, so this will return the exact same Class instance
             * returned to that plugin itself if it has already tried to load
             * the class.
             */
            c = dependedPlugin.getClassLoader()
                .resolveInternal(name);
            if (c != null)
                return c;
        }
        /*
         * At this point we've searched the vm classes, OpenGroove's classes,
         * the plugin's classes, and all of it's dependencies' classes, without
         * success. This leaves us nothing left to do but throw a
         * ClassNotFoundException.
         */
        throw new ClassNotFoundException(
            "The class couldn't be found in the plugin "
                + plugin.getModel().getId()
                + " or any of it's dependencies (a total of "
                + plugin.getModel().getDependencies().length
                + ")");
    }
}
