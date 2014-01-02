package org.opengroove.g4.server;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.g4.common.G4Defaults;
import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.PacketSpooler;
import org.opengroove.g4.common.protocol.ExceptionPacket;
import org.opengroove.g4.common.protocol.InboundMessagePacket;
import org.opengroove.g4.common.protocol.InitialCompletePacket;
import org.opengroove.g4.common.protocol.MessageResponse;
import org.opengroove.g4.common.protocol.OutboundMessagePacket;
import org.opengroove.g4.common.protocol.PresencePacket;
import org.opengroove.g4.common.protocol.RealNamePacket;
import org.opengroove.g4.common.protocol.RosterPacket;
import org.opengroove.g4.common.roster.Contact;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.common.utils.ObjectUtils;
import org.opengroove.g4.common.utils.PropUtils;
import org.opengroove.g4.common.utils.ProtocolUtils;

/**
 * A connection from a client.
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerConnection extends Thread
{
    private static final int PACKET_SPOOLER_SIZE = 90 * 1000;
    private static ThreadLocal<ServerConnection> threadLocalConnection = new ThreadLocal<ServerConnection>();
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
    public PacketSpooler spooler;
    
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
            socket.setSoTimeout(G4Defaults.SOCKET_TIMEOUT);
            socketIn = socket.getInputStream();
            socketOut = socket.getOutputStream();
            out = new ObjectOutputStream(socketOut);
            in = new ObjectInputStream(socketIn);
            spooler = new PacketSpooler(out, PACKET_SPOOLER_SIZE);
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
            connectionCleanup();
        }
    }
    
    private void connectionCleanup()
    {
        if (userid.hasComputer())
        {
            /*
             * Remove this connection from the connection map
             */
            G4Server.connections.remove(userid);
            /*
             * Remove the idle time for this computer from the connection map
             */
            G4Server.idleTimes.remove(userid);
            /*
             * Broadcast to all users that this user is offline
             */
            PresencePacket presencePacket = new PresencePacket();
            presencePacket.setStatus(PresencePacket.Status.Offline);
            presencePacket.setUserid(userid);
            G4Server.updateContainingPresence(userid, presencePacket);
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
     * should be called from the server connection thread. In addition to
     * sending initial state to this user, it also broadcasts to all other users
     * that this user is now online.
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
            RosterPacket rosterPacket = G4Server.createRosterPacket(userid,
                    true, null);
            send(rosterPacket);
            /*
             * Send the user's current real name
             */
            RealNamePacket realNamePacket = new RealNamePacket();
            File realNameFile = new File(userFolder, "realname");
            if (realNameFile.exists())
                realNamePacket.setName(StringUtils.readFile(realNameFile));
            send(realNamePacket);
            /*
             * Send the initial presence of all of the user's contacts
             */
            for (Contact contact : rosterPacket.getContacts())
            {
                for (Userid contactComputer : contact.getComputers())
                {
                    PresencePacket computerPresence = createCurrentPresencePacket(contactComputer);
                    send(computerPresence);
                }
            }
            /*
             * Schedule the user for initial presence sending. This can take
             * some time, since it has to scan through every other user's
             * contact list to see if this user is on their contact list, so
             * we'll do it asynchronously.
             */
            PresencePacket presencePacket = new PresencePacket();
            presencePacket.setStatus(PresencePacket.Status.Online);
            presencePacket.setUserid(userid);
            G4Server.updateContainingPresence(userid, presencePacket);
            /*
             * Send messages cached for the user
             */
            File messageFolder = G4Server.getMessageFolder(userid);
            for (File messageFile : messageFolder.listFiles())
            {
                InboundMessagePacket messageObject = (InboundMessagePacket) ObjectUtils
                        .readObject(messageFile);
                messageObject.setPacketThread(ProtocolUtils.generateId());
                send(messageObject);
            }
        }
        InitialCompletePacket initialDonePacket = new InitialCompletePacket();
        initialDonePacket.setPacketThread(ProtocolUtils.generateId());
        send(initialDonePacket);
    }
    
    /**
     * Creates a PresencePacket that reflects the current status of the
     * specified computer.
     * 
     * @param computer
     *            The computer whose status we are generating
     * @return The new packet representing the computer's status
     */
    private PresencePacket createCurrentPresencePacket(Userid computer)
    {
        PresencePacket packet = new PresencePacket();
        packet.setUserid(computer);
        if (G4Server.connections.get(computer) == null)
        {
            packet.setStatus(PresencePacket.Status.Offline);
        }
        else
        {
            Long idleTime = G4Server.idleTimes.get(computer);
            if (idleTime != null)
            {
                packet.setStatus(PresencePacket.Status.Idle);
                packet.setDuration(System.currentTimeMillis() - idleTime);
            }
            else
            {
                packet.setStatus(PresencePacket.Status.Online);
            }
        }
        return packet;
    }
    
    public void respond(Packet response)
    {
        response.respondTo(currentPacket);
        send(response);
    }
    
    public void dispatchProfileMessage(OutboundMessagePacket packet)
    {
        Object payload = packet.getMessage();
        Command command = G4Server.computerCommands
                .get(payload.getClass());
        boolean wasProcessed = command != null;
        if (command != null)
            command.process((Packet) payload);
        MessageResponse response = new MessageResponse();
        response.setPacketThread(packet.getPacketThread());
        response.setMessageId(packet.getMessageId());
        send(response.respondTo(packet));
        if (!wasProcessed)
            throw new RuntimeException(
                    "This server doesn't support the profile message type specified.");
    }
}
