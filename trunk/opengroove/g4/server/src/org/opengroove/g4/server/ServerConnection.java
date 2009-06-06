package org.opengroove.g4.server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.PacketSpooler;
import org.opengroove.g4.common.user.Userid;

/**
 * A connection from a client.
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerConnection extends Thread
{
    /**
     * The socket that the client is connecting with
     */
    private Socket socket;
    /**
     * The client's input stream
     */
    private InputStream socketIn;
    /**
     * The client's output stream
     */
    private OutputStream socketOut;
    /**
     * The object input stream
     */
    private ObjectInputStream in;
    /**
     * The object output stream
     */
    private ObjectOutputStream out;
    /**
     * The id of the user that this connection is for, or null if the user has
     * not authenticated yet. This is always absolute, and will either be a
     * username userid or a computer userid.
     */
    private Userid userid;
    private PacketSpooler spooler;
    
    public void send(Packet packet)
    {
        
    }
    
    public Userid getUserid()
    {
        return userid;
    }
    
    public ServerConnection(Socket socket)
    {
        this.socket = socket;
    }
    
    public void run()
    {
        try
        {
            socketIn = socket.getInputStream();
            socketOut = socket.getOutputStream();
            out = new ObjectOutputStream(socketOut);
            in = new ObjectInputStream(socketIn);
            spooler = new PacketSpooler(out, 300);
            while(!socket.isClosed())
            {
                Packet packet = (Packet) in.readObject();
                process(packet);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void process(Packet packet)
    {
    }

}
