package org.bzflag.jzapi.examples;

import org.bzflag.jzapi.internal.SimpleBind;

public class ShockwavePeriodicFirePlugin
{
    public static boolean isRunning = true;
    
    /**
     * @param args
     */
    public static void load(String args)
    {
        new Thread()
        {
            public void run()
            {
                while (isRunning)
                {
                    try
                    {
                        Thread.sleep(4000);
                    }
                    catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    SimpleBind.bz_fireWorldWep("SW", 5,
                        new float[] { 0, 0, 10 }, 0, 0, 0,
                        0);
                }
            }
        }.start();
    }
    
    public static void unload()
    {
        isRunning = false;
    }
    
}
