package jw.bznetwork.server.live;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
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
    
    private BufferedInputStream bufferedIn;
    
    private DataInputStream in;
    
    public ReadThread(LiveServer server)
    {
        this.server = server;
        this.serverUnbufferedIn = server.getProcess().getInputStream();
        this.bufferedIn = new BufferedInputStream(serverUnbufferedIn,
                BUFFER_SIZE);
        this.in = new DataInputStream(bufferedIn);
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
                if (i == '|')
                {
                    byte[] lengthBytes = new byte[5];
                    in.readFully(lengthBytes);
                    int length = Integer.parseInt(new String(lengthBytes));
                    byte[] data = new byte[length];
                    in.readFully(data);
                    /*
                     * At this point we've read all the data that we need to.
                     * We'll go ahead and process it.
                     */
                    processData(data);
                }
            }
        }
        catch (Exception e)
        {
            // FIXME: implement this to shut down the server and log an error
            // message about the exception
        }
    }
    
    private void processData(byte[] dataBytes)
    {
        /*
         * Currently, all data output from the server will contain only visible
         * characters, so we'll just put it into a string.
         */
        String data = new String(dataBytes);
        if (data.startsWith("playerjoin "))
            processPlayerJoin(data.substring("playerjoin ".length()));
        else if (data.startsWith("playerpart "))
            processPlayerPart(data.substring("playerpart ".length()));
    }
    
    private void processPlayerPart(String substring)
    {
        // TODO Auto-generated method stub
        
    }
    
    private void processPlayerJoin(String substring)
    {
        // TODO Auto-generated method stub
        
    }
}
