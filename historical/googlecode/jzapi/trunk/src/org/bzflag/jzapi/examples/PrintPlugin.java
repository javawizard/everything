package org.bzflag.jzapi.examples;

/**
 * A simple plugin that prints out when it is loaded and when it is unloaded,
 * and includes the arguments that were passed to it.
 * 
 * @author Alexander Boyd
 * 
 */
public class PrintPlugin
{
    public static void load(String args)
    {
        System.out
            .println("PrintPlugin loaded with the following arguments: " + args);
    }
    
    public static void unload()
    {
        System.out.println("PrintPlugin unloaded");
    }
}
