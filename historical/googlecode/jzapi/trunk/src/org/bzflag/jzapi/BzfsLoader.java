package org.bzflag.jzapi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bzflag.jzapi.BzfsAPI.EventType;
import org.bzflag.jzapi.events.BzfsPlayerJoinPartEvent;

/**
 * This method handles loading of actual plugins. The C++ code calls methods on
 * it to actually load java plugins. It also manages user-registered class
 * loaders.
 * 
 * @author Alexander Boyd
 * 
 */
public class BzfsLoader
{
    private static final ArrayList<Class> pluginClasses =
        new ArrayList<Class>();
    
    private static final HashMap<EventType, Class<? extends BzfsEvent>> eventTypeClasses =
        new HashMap<EventType, Class<? extends BzfsEvent>>();
    
    private static volatile boolean initialized = false;
    
    private static synchronized boolean loadPlugin(
        String name, String parameters)
    {
        if (!initialized)
        {
            try
            {
                init();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
            initialized = true;
        }
        if (name.length() < 7)
        {
            System.out
                .println("You need to specify a class to load. Specify the class "
                    + "in the format package.name.ClassName.class for it to be "
                    + "loaded correctly. Use the suffix '.class' even if your "
                    + "class isn't stored in an actual file.");
            return false;
        }
        if (!name.endsWith(".class"))
        {
            System.out
                .println("You didn't suffix your classname with '.class'. You'll "
                    + "need to add this suffix even if your class isn't stored "
                    + "in an actual .class file. This suffix is used to tell bzfs "
                    + "that the plugin is a java plugin.");
            return false;
        }
        Class c;
        try
        {
            c =
                Class.forName(name.substring(0, name
                    .length()
                    - ".class".length()));
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            System.out
                .println("The class you specified couldn't be found. Make sure that "
                    + "you used . instead of / to separate package components, and "
                    + "make sure that, if your class uses a custom class loader, the "
                    + "plugin that provides that class loader was specified first.");
            return false;
        }
        Method method;
        try
        {
            method = c.getMethod("load", String.class);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
            System.out
                .println("The class you specified was found, but it "
                    + "doesn't contain a static "
                    + "load(String) method. Add this method, then try again.");
            return false;
        }
        try
        {
            method.invoke(null, parameters);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out
                .println("An exception was thrown in the plugin's load method. "
                    + "The stack trace has been printed out. The "
                    + "plugin's unload method will not be called "
                    + "when it's time to unload the plugins.");
            return false;
        }
        pluginClasses.add(c);
        return true;
    }
    
    private static void init()
        throws ClassNotFoundException
    {
        loadEventTypeClasses();
    }
    
    private static void loadEventTypeClasses()
        throws ClassNotFoundException
    {
        eventTypeClasses.put(EventType.playerJoin,
            BzfsPlayerJoinPartEvent.class);
        eventTypeClasses.put(EventType.playerPart,
            BzfsPlayerJoinPartEvent.class);
    }
    
    private static BzfsEvent getEventForType(int type)
    {
        EventType typeEnum = EventType.values()[type];
        Class<? extends BzfsEvent> c =
            eventTypeClasses.get(typeEnum);
        if (c == null)
        {
            System.out
                .println("class was null for event type "
                    + typeEnum + " with ordinal " + type);
            return null;
        }
        try
        {
            return c.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out
                .println("An exception occured while constructing an instance of the event type class. The class is "
                    + c.getName()
                    + " with enum type "
                    + typeEnum + " and ordinal " + type);
            return null;
        }
    }
    
    private static void unloadPlugins()
    {
        for (Class c : pluginClasses)
        {
            System.out.println("unloading plugin "
                + c.getName());
            try
            {
                Method m = c.getMethod("unload");
                m.invoke(null);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                System.out
                    .println("An exception occured while unloading the plugin "
                        + c.getName()
                        + ". The stack trace has been printed out. Other "
                        + "plugins will continue to be unloaded. ");
            }
        }
    }
    
    public static void throwRuntimeException(String message)
    {
        throw new RuntimeException(message);
    }
}
