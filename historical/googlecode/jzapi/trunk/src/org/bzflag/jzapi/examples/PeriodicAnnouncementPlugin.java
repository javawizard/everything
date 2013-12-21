package org.bzflag.jzapi.examples;

import java.util.Date;

import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.BzfsAPI.TeamType;
import org.bzflag.jzapi.internal.SimpleBind;

public class PeriodicAnnouncementPlugin
{
    private static boolean isRunning = true;
    
    public static void load(String args)
    {
        new Thread()
        {
            public void run()
            {
                while (isRunning)
                {
                    for (int i = 0; i < 5 && isRunning; i++)
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
                    }
                    SimpleBind
                        .bz_sendTextMessage(
                            BzfsAPI.SERVER_PLAYER,
                            BzfsAPI.ALL_PLAYERS,
                            "The current time is "
                                + new Date());
                }
            }
        }.start();
    }
    
    public static void unload()
    {
        isRunning = false;
    }
}
