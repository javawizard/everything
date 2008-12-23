package org.bzflag.jzapi.examples;

import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.BzfsEvent;
import org.bzflag.jzapi.BzfsEventHandler;
import org.bzflag.jzapi.BzfsAPI.EventType;
import org.bzflag.jzapi.events.BzfsPlayerJoinPartEvent;
import org.bzflag.jzapi.internal.SimpleBind;

public class PlayerJoinWelcomePlugin
{
    public static BzfsEventHandler listener;
    
    public static void load(String args)
    {
        listener = new BzfsEventHandler()
        {
            
            public void process(BzfsEvent event)
            {
                BzfsPlayerJoinPartEvent joinEvent =
                    (BzfsPlayerJoinPartEvent) event;
                final int player = joinEvent.getPlayerId();
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
                            BzfsAPI.SERVER_PLAYER, player,
                            "Welcome to the server!");
                    }
                }.start();
            }
        };
        BzfsAPI.registerEventHandler(EventType.playerJoin
            .ordinal(), listener);
    }
    
    public static void unload()
    {
        BzfsAPI.removeEventHandler(EventType.playerJoin
            .ordinal(), listener);
    }
}
