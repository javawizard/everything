package org.bzflag.jzapi.examples;

import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.BzfsEvent;
import org.bzflag.jzapi.BzfsEventHandler;
import org.bzflag.jzapi.BzfsAPI.EventType;

/**
 * A simple plugin that prints out when it is loaded and when it is unloaded,
 * and includes the arguments that were passed to it.
 * 
 * @author Alexander Boyd
 * 
 */
public class PlayerJoinListenPlugin
{
    public static void load(String args)
    {
        BzfsAPI.registerEventHandler(EventType.playerJoin,
            new BzfsEventHandler()
            {
                
                public void process(BzfsEvent event)
                {
                    System.out
                        .println("listener triggered for event class "
                            + event.getClass().getName());
                }
            });
    }
    
    public static void unload()
    {
    }
}
