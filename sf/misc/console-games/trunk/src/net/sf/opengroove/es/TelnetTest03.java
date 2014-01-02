package net.sf.opengroove.es;

import java.io.IOException;
import java.io.InputStream;
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
        ServerSocket ss = new ServerSocket(45678);
        while (true)
        {
            final Socket socket = ss.accept();
            final OutputStream out = socket
                .getOutputStream();
            InteractiveConsole console = new InteractiveConsole(
                out);
            console.setup();
            console.setBackground(console.GREEN);
            for (int c = 0; c < 80; c++)
            {
                spaceString += " ";
            }
            for (int r = 0; r < 26; r++)
            {
                console.setPos(r, 1);
                console.write(spaceString);
            }
            console.setPos(1, 1);
            console.write("Hello.");
            InputStream in = socket.getInputStream();
            int read;
            while ((read = in.read()) != -1)
            {
                System.out.println("" + read);
            }
            socket.close();
            ss.close();
            System.exit(0);
        }
        
    }
    
}
