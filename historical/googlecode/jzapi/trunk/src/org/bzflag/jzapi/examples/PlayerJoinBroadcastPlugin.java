package org.bzflag.jzapi.examples;

import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.BzfsEvent;
import org.bzflag.jzapi.BzfsEventHandler;
import org.bzflag.jzapi.BzfsAPI.EventType;
import org.bzflag.jzapi.internal.SimpleBind;

public class PlayerJoinBroadcastPlugin
{
    public static BzfsEventHandler handler;
    
    public static void load(String args)
    {
        handler = new BzfsEventHandler()
        {
            
            public void process(final BzfsEvent event)
            {
                final EventType eventType =
                    event.getEventType();
                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        SimpleBind.bz_sendTextMessage(
                            BzfsAPI.SERVER_PLAYER,
                            BzfsAPI.ALL_PLAYERS,
                            "A new player has joined. The type is "
                                + eventType);
                    }
                }.start();
            }
        };
        BzfsAPI.registerEventHandler(EventType.playerJoin
            .ordinal(), handler);
    }
    
    public static void unload()
    {
        BzfsAPI.removeEventHandler(EventType.playerJoin
            .ordinal(), handler);
    }
}
