package jw.bznetwork.server.live;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * A thread that reads from the bzfs instance and processes the read data.
 * Currently, this includes updating its parent LiveServer to reflect the list
 * of players and such, sending log events to the database
 * 
 * @author Alexander Boyd
 * 
 */
public class ReadThread extends Thread
{
    private static final int BUFFER_SIZE = 1024;
    
    private LiveServer server;
    
    private InputStream serverUnbufferedIn;
    
    private BufferedInputStream in;
    
    public ReadThread(LiveServer server)
    {
        this.server = server;
        this.serverUnbufferedIn = server.getProcess().getInputStream();
        this.in = new BufferedInputStream(serverUnbufferedIn, BUFFER_SIZE);
    }
    
    public void run()
    {
        /*
         * Any output relevant to us will begin with a single pipe character
         * followed by 5 characters which represents a number. That number is
         * the number of bytes after that to read, so we then read that number
         * of bytes.
         */
        int i;
        try
        {
            while (true)
            {
                i = in.read();
                if(i == '|')
                {
                    StringBuffer lengthBuffer = new StringBuffer();
                    for(int j = 0; j < 5; j++)
                    {
                        
                    }
                }
            }
        }
        catch (Exception e)
        {
            // FIXME: implement this to shut down the server and log an error
            // message about the exception
        }
    }
}
