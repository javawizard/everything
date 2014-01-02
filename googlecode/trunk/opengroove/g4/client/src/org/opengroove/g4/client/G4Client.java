package org.opengroove.g4.client;

public class G4Client
{
    
    public static void main(String[] args)
    {
        /*
         * Initial housekeeping chores
         */
        Statics.run();
        startGarbageCollector();
        
    }
    
    private static void startGarbageCollector()
    {
        Thread t = new Thread()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(30 * 1000);
                        System.gc();
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }
}
