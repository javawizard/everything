package org.opengroove.g4.server;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicLong;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.PacketSpooler;
import org.opengroove.g4.common.messages.RosterMessage;
import org.opengroove.g4.common.protocol.ExceptionPacket;
import org.opengroove.g4.common.protocol.InboundMessagePacket;
import org.opengroove.g4.common.protocol.InitialCompletePacket;
import org.opengroove.g4.common.protocol.MessageResponse;
import org.opengroove.g4.common.protocol.OutboundMessagePacket;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.common.utils.ObjectUtils;
import org.opengroove.g4.common.utils.ProtocolUtils;

/**
 * A connection from a client.
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerConnection extends Thread
{
    private static ThreadLocal<ServerConnection> threadLocalConnection =
        new ThreadLocal<ServerConnection>();
    private static AtomicLong threadNameCounter = new AtomicLong(1);
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
     * The current packet being read by this connection. This is used in
     * respond().
     */
    private Packet currentPacket;
    /**
     * The id of the user that this connection is for, or null if the user has
     * not authenticated yet. This is always absolute, and will either be a
     * username userid or a computer userid.
     */
    public Userid userid;
    public File userFolder;
    private PacketSpooler spooler;
    
    public void send(Packet packet)
    {
        spooler.send(packet);
    }
    
    public Userid getUserid()
    {
        return userid;
    }
    
    public ServerConnection(Socket socket)
    {
        super("G4-server-connection-" + threadNameCounter.getAndIncrement());
        this.socket = socket;
    }
    
    public void run()
    {
        try
        {
            threadLocalConnection.set(this);
            socketIn = socket.getInputStream();
            socketOut = socket.getOutputStream();
            out = new ObjectOutputStream(socketOut);
            in = new ObjectInputStream(socketIn);
            spooler = new PacketSpooler(out, 300);
            spooler.start();
            while (!socket.isClosed())
            {
                Packet packet = (Packet) in.readObject();
                this.currentPacket = packet;
                process(packet);
                this.currentPacket = null;
            }
        }
        catch (Exception e)
        {
            this.currentPacket = null;
            e.printStackTrace();
            try
            {
                socket.close();
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        finally
        {
            if (userid.hasComputer())
                G4Server.connections.remove(userid);
        }
    }
    
    private void process(Packet packet)
    {
        try
        {
            Command command = null;
            Class packetClass = packet.getClass();
            if (userid == null)
                command = G4Server.unauthCommands.get(packetClass);
            else if (userid.hasComputer())
                command = G4Server.computerCommands.get(packetClass);
            else
                command = G4Server.userCommands.get(packetClass);
            if (command == null)
                throw new RuntimeException("Unknown command class: "
                    + packetClass.getName());
            command.process(packet);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ExceptionPacket response = new ExceptionPacket();
            response.respondTo(packet);
            send(response);
        }
    }
    
    /**
     * This method should only be called from within classes that implement
     * Command. It returns the server connection for that command.
     * 
     * @return
     */
    public static ServerConnection getConnection()
    {
        return threadLocalConnection.get();
    }
    
    /**
     * Sends initial login state. This is called when the user logs in, and
     * should be called from the server connection thread.
     * 
     * @param hasComputer
     */
    public void sendInitialLoginState(boolean hasComputer)
    {
        if (hasComputer)
        {
            /*
             * Send the user's initial roster
             */
            /*
             * Send the initial presence of all of the user's contacts
             */
            /*
             * Schedule the user for initial presence sending. This can take
             * some time, since it has to scan through every other user's
             * contact list to see if this user is on their contact list, so
             * we'll do it asynchronously.
             */
            /*
             * Send messages cached for the user
             */
            File messageFolder = G4Server.getMessageFolder(userid);
            for (File messageFile : messageFolder.listFiles())
            {
                InboundMessagePacket messageObject =
                    (InboundMessagePacket) ObjectUtils.readObject(messageFile);
                messageObject.setPacketThread(ProtocolUtils.generateId());
                send(messageObject);
            }
        }
        InitialCompletePacket initialDonePacket = new InitialCompletePacket();
        initialDonePacket.setPacketThread(ProtocolUtils.generateId());
        send(initialDonePacket);
    }
    
    public void respond(Packet response)
    {
        response.respondTo(currentPacket);
        send(response);
    }
    
    public void dispatchProfileMessage(OutboundMessagePacket packet)
    {
        boolean wasProcessed = false;
        /*
         * We'll go through and see if the message is of a supported class type.
         * If it is, we'll process it appropriately. If it isn't, we'll send a
         * message response first (so that the client will delete the message),
         * then we'll throw an exception.
         */
        Object messageObject = packet.getMessage();
        if (messageObject instanceof RosterMessage)
        {
            
        }
        MessageResponse response = new MessageResponse();
        response.setPacketThread(packet.getPacketThread());
        response.setMessageId(packet.getMessageId());
        send(response.respondTo(packet));
        if (!wasProcessed)
            throw new RuntimeException(
                "This server doesn't support the profile message type specified.");
    }
}
