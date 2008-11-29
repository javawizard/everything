package net.sf.opengroove.es;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import net.sf.opengroove.es.utils.InteractiveConsole;
import net.sf.opengroove.es.utils.InteractiveConsole.Direction;

public class TelnetTest01
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
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
            Thread.sleep(2000);
            socket.close();
            ss.close();
            System.exit(0);
        }
    }
    
}
