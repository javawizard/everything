package org.bzflag.jzapi.examples;

import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.BzfsEvent;
import org.bzflag.jzapi.BzfsEventHandler;
import org.bzflag.jzapi.BzfsAPI.EventType;
import org.bzflag.jzapi.internal.SimpleBind;

/**
 * TODO: changes all player's limbo text when just one joins, but we only want
 * to set limbo text on the player that just joined
 * 
 * @author Alexander Boyd
 * 
 */
public class LimboTextPlugin
{
    public static BzfsEventHandler listener;
    
    public static void load(String args)
    {
        listener = new BzfsEventHandler()
        {
            
            public void process(BzfsEvent event)
            {
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
                        }
                        int[] players =
                            BzfsAPI.getPlayerIndexList();
                        System.out
                            .println("registering for "
                                + players.length
                                + " players");
                        for (int player : players)
                        {
                            SimpleBind
                                .bz_setPlayerLimboMessage(
                                    player,
                                    "You're in Limbo!");
                        }
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
