package net.sf.opengroove.es;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import net.sf.opengroove.es.utils.InteractiveConsole;

public class TelnetTest03
{
    private static String spaceString = "";
    
    /**
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args)
        throws IOException, InterruptedException
    {
        ServerSocket ss = new ServerSocket(36574);
        while (true)
        {
            final Socket socket = ss.accept();
            final OutputStream out = socket
                .getOutputStream();
            InteractiveConsole console = new InteractiveConsole(
                out);
            console.setup();
            console.setColor(console.GREEN);
            for (int c = 0; c < 60; c++)
            {
                spaceString += " ";
            }
            for (int r = 0; r < 25; r++)
            {
                console.setPos(r, 1);
                console.write(spaceString);
            }
            console.write("Hello.");
            Thread.sleep(2000);
            socket.close();
            ss.close();
            System.exit(0);
        }
        
    }
    
}
