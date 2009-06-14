package net.sf.opengroove.client.com;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.opengroove.g4.common.G4Defaults;
import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.PacketSpooler;
import org.opengroove.g4.common.protocol.LoginPacket;
import org.opengroove.g4.common.protocol.LoginResponse;

/**
 * A class that can communicate with a G4 server. It support automatic
 * reconnection, automatic authentication on connection, synchronous response
 * waiting, and marshalling of exceptions on the server back to the client.<br/>
 * <br/>
 * 
 * This class takes care of sending periodic NOPs to the server to prevent it
 * from dropping the connection. It also takes care of dropping the connection
 * if periodic data is not received from the server.<br/>
 * <br/>
 * 
 * Currently, the default port for the server is always used. I'll add the
 * ability to change that later, probably by re-adding the concept of SRV
 * records that was present in G3 into G4. (probably _og4c and _og4s will be the
 * service names.)
 * 
 * @author Alexander Boyd
 * 
 */
public class Communicator
{
    private String serverName;
    private LoginPacket loginPacket;
    
    private boolean isRunning = true;
    /**
     * The thread that manages this communicator. It is the thread that connects
     * the communicator if it is one that attempts to reconnect when
     * disconnected, and it is the thread that reads packets from the server and
     * processes them.
     */
    private Thread coordinator;
    /**
     * A packet spooler that will spool packets to the server. This is set to
     * null until a connection is established to the server, and is again set to
     * null when the connection is dropped.
     */
    private PacketSpooler spooler;
    /**
     * The socket that is currently connected to the server. This is not set
     * until after successful authentication, and is cleared when the connection
     * to the server is lost.
     */
    private Socket socket;
    
    private static final int MAX_WAIT_DELAY = 20;
    
    private static final int PACKET_SPOOLER_SIZE = 500 * 1000;
    
    private int currentWaitDelay = 0;
    
    private Map<String, BlockingQueue<Packet>> syncBlocks =
        Collections.synchronizedMap(new HashMap<String, BlockingQueue<Packet>>());
    
    private List<StatusListener> statusListeners =
        Collections.synchronizedList(new ArrayList<StatusListener>());
    
    private List<PacketListener> packetListeners =
        Collections.synchronizedList(new ArrayList<PacketListener>());
    
    public void addStatusListener(StatusListener listener)
    {
        statusListeners.add(listener);
    }
    
    public void removeStatusListener(StatusListener listener)
    {
        statusListeners.remove(listener);
    }
    
    public void addPacketListener(PacketListener listener)
    {
        packetListeners.add(listener);
    }
    
    public void removePacketListener(PacketListener listener)
    {
        packetListeners.remove(listener);
    }
    
    /**
     * Creates a new communicator using the settings specified. After the
     * communicator is created, packet listeners and status listeners can be
     * added to it, and then it can be {@link #start() started}.
     * 
     * @param serverName
     *            The name of the server that this communicator is to connect
     *            to. Currently, this is always connected to as if this
     *            communicator were a G4 client. In the future, there will be an
     *            option for connecting as if this were another G4 server, and
     *            this class will then be used for server-to-server
     *            communications and message delivery.
     * @param loginPacket
     *            A login packet to issue. If this is not null, then the
     *            communicator will try to reconnect when it is disconnected,
     *            and it will reissue this login packet whenever it connects. If
     *            this is null, then this communicator will not attempt to
     *            reconnect when it is disconnected.
     */
    public Communicator(String serverName, LoginPacket loginPacket)
    {
        this.serverName = serverName;
        this.loginPacket = loginPacket;
    }
    
    /**
     * Changes the login packet that should be used by this communicator to log
     * in. The communicator must have been constructed with a non-null login
     * packet for this to work. This is typically used when the server-side
     * password has changed, and this communicator needs to be updated with the
     * new password.
     * 
     * @param packet
     */
    public void setLoginPacket(LoginPacket packet)
    {
        this.loginPacket = packet;
    }
    
    /**
     * Starts this communicator. If this communicator reconnects on
     * disconnection (which can be specified by specifying a non-null login
     * packet), then this returns immediately, and connection can be detected
     * from a StatusListener added prior to this being called. If this
     * communicator does not reconnect on disconnection, then this method blocks
     * until a connection to the server has been established, and throws an
     * exception if one could not be established.
     */
    public void start()
    {
        if (loginPacket == null)
        {
            /*
             * One-time communicator. Synchronously connect to the server and
             * then asynchronously process packets.
             */
            setupConnection();
            coordinator = new Thread()
            {
                public void run()
                {
                    runConnection();
                }
            };
            coordinator.start();
        }
        else
        {
            /*
             * Multi-use communicator. Asynchronously connect to the server and
             * process packets.
             */
            coordinator = new Thread()
            {
                public void run()
                {
                    while (isRunning)
                    {
                        setupConnection();
                        runConnection();
                    }
                }
            };
            coordinator.start();
        }
    }
    
    /**
     * Sets up a connection to the server, throwing an exception if a problem
     * occurs while doing so.
     */
    private void setupConnection()
    {
        /*
         * First, wait for currentWaitDelay seconds.
         */
        try
        {
            Thread.sleep(currentWaitDelay++ * 1000);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        /*
         * Now try to connect.
         */
        try
        {
            Socket lSock = new Socket(serverName, G4Defaults.CLIENT_SERVER_PORT);
            lSock.setSoTimeout(G4Defaults.SOCKET_TIMEOUT);
            OutputStream lsOut = lSock.getOutputStream();
            InputStream lsIn = lSock.getInputStream();
            ObjectOutputStream lOut = new ObjectOutputStream(lsOut);
            ObjectInputStream lIn = new ObjectInputStream(lsIn);
            PacketSpooler lSpooler = new PacketSpooler(lOut, PACKET_SPOOLER_SIZE);
            lSpooler.start();
            /*
             * We've successfully connected. ObjectInputStream reads off a
             * header from the stream upon construction, so we know we have
             * communication with the server. Now we'll try to log in.
             */
            if (loginPacket != null)
            {
                lSpooler.send(loginPacket);
                LoginResponse loginResponse = (LoginResponse) lIn.readObject();
                if (loginResponse.getStatus() != LoginResponse.Status.Successful)
                {
                    try
                    {
                        lSpooler.close();
                        lSock.close();
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                    throw new Faile
                }
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Processes packets from the server, returning when the connection to the
     * server is lost after cleaning up the connection.
     */
    private void runConnection()
    {
        
    }
    
    public void close()
    {
        isRunning = false;
        try
        {
            socket.close();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
    
    public boolean isAlive()
    {
        return coordinator != null && coordinator.isAlive();
    }
    
    public boolean isConnected()
    {
        return socket != null;
    }
}
