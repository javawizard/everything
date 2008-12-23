package org.bzflag.jzapi.examples;

import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.internal.SimpleBind;

/**
 * A simple plugin that checks every 10 seconds and fires one guided missile at
 * each player that is alive. The guided missile is fired from 20 world units
 * up, at the center of the world, facing north. It is locked on the player that
 * it corresponds to. If there is more than one player alive, then one guided
 * missile will be fired for each player.<br/><br/>
 * 
 * Right now, this plugin seems to crash bzfs if the player dies after a gm was
 * fired. It also seems to have trouble with actually firing gms. Any assistance
 * in solving this would be welcome.
 * 
 * @author Alexander Boyd
 * 
 */
public class GMLockFirePlugin
{
    private static boolean isRunning = true;
    
    public static void load(String args)
    {
        new Thread()
        {
            public void run()
            {
                System.out.println("running");
                while (isRunning)
                {
                    System.out.println("waiting");
                    for (int i = 0; i < 10 && isRunning; i++)
                    {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    System.out.println("firing");
                    int[] players =
                        BzfsAPI.getPlayerIndexList();
                    for (int player : players)
                    {
                        System.out.println("targeted");
                        SimpleBind
                            .bz_sendTextMessage(
                                BzfsAPI.SERVER_PLAYER,
                                BzfsAPI.ALL_PLAYERS,
                                "firing gm at "
                                    + SimpleBind
                                        .bz_getPlayerCallsign(player));
                        SimpleBind.bz_fireWorldGM(player,
                            1, new float[] { 0, 0, 20 }, 0,
                            0, 0);
                    }
                }
            }
        }.start();
    }
    
    public static void unload()
    {
        isRunning = false;
    }
    
}
