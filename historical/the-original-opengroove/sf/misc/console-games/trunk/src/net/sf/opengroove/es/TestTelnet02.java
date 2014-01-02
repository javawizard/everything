package net.sf.opengroove.es;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestTelnet02
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
                        out
                            .write("\u001B[=3;7h\u001B[42mWhat is your name?\u001B[1;35HHi"
                                .getBytes());
                        out.flush();
                        Thread.sleep(2000);
                        socket.close();
                        System.exit(0);
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
