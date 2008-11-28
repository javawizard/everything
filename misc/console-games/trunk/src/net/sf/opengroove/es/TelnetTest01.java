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
        ServerSocket ss = new ServerSocket(36574);
        while (true)
        {
            final Socket socket = ss.accept();
            final OutputStream out = socket
                .getOutputStream();
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        InteractiveConsole console = new InteractiveConsole(
                            out);
                        console.setup();
                        console.setSpecial(31);
                        out.write("Hello. How are you?"
                            .getBytes());
                        out.flush();
                        Thread.sleep(2000);
                        console.setPos(1, 1);
                        console.clearToEndLine();
                        console.setSpecial(32);
                        out.write("What is your name?"
                            .getBytes());
                        out.flush();
                        Thread.sleep(2000);
                        console.move(Direction.DOWN, 1);
                        console.setPos(2,0);
                        console.setSpecial(33);
                        out.write("Hi again.".getBytes());
                        out.flush();
                        Thread.sleep(2000);
                        console.setPos(1,40);
                        console.setSpecial(34);
                        out.write("Testing".getBytes());
                        out.flush();
                        Thread.sleep(2000);
                        socket.close();
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }.start();
        }
    }
    
}
