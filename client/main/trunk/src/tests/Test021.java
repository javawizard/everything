package tests;

import net.sf.opengroove.client.com.ConnectionResolver;
import net.sf.opengroove.client.com.ServerContext;

public class Test021
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        // class for testing out the ConnectionResolver class.
        System.out.println("first lookup");
        lookup();
        Thread.sleep(700);
        System.out.println("second lookup");
        lookup();
        Thread.sleep(700);
        System.out.println("third lookup");
        lookup();
    }
    
    public static void lookup()
    {
        ServerContext[] servers = ConnectionResolver
            .lookup("alexlaptop");
        for (ServerContext server : servers)
        {
            System.out.println(server.getHostname() + ":"
                + server.getPort());
        }
    }
    
}
