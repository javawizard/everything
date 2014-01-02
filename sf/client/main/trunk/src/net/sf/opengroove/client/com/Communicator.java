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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.opengroove.g4.common.G4Defaults;
import org.opengroove.g4.common.NopThread;
import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.PacketSpooler;
import org.opengroove.g4.common.protocol.ExceptionPacket;
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
    /**
     * The object input stream associated with the current socket.
     */
    private ObjectInputStream objectIn;
    
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
                        try
                        {
                            setupConnection();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        try
                        {
                            runConnection();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
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
                /*
                 * Send the login packet
                 */
                lSpooler.send(loginPacket);
                /*
                 * Read the login response
                 */
                LoginResponse loginResponse = (LoginResponse) lIn.readObject();
                /*
                 * If login failed, close the connection, notify everyone, and
                 * throw an exception.
                 */
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
                    notifyStatusFailedAuth(loginResponse);
                    throw new FailedLoginException(loginResponse);
                }
            }
            /*
             * We've successfully logged in, or we're not a communicator that
             * should automatically log in at the start of a connection. Either
             * way, the connection is now ready to go. We'll reset the wait
             * time, inject everything into the appropiate fields, and be on our
             * merry way.
             */
            currentWaitDelay = 0;
            this.objectIn = lIn;
            this.spooler = lSpooler;
            this.socket = lSock;
            new NopThread(lSpooler, G4Defaults.NOP_INTERVAL).start();
            notifyStatusConnected();
            /*
             * We're done. Everything else is left up to the runConnection
             * method, so we'll return.
             */
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
    
    private void notifyStatusConnected()
    {
        for (StatusListener listener : new ArrayList<StatusListener>(statusListeners))
        {
            listener.connectionReady();
        }
    }
    
    private void notifyStatusDisconnected()
    {
        for (StatusListener listener : new ArrayList<StatusListener>(statusListeners))
        {
            listener.lostConnection();
        }
    }
    
    private void notifyStatusFailedAuth(LoginResponse loginResponse)
    {
        for (StatusListener listener : new ArrayList<StatusListener>(statusListeners))
        {
            listener.authenticationFailed(loginResponse);
        }
    }
    
    /**
     * Processes packets from the server, returning when the connection to the
     * server is lost after cleaning up the connection.
     */
    private void runConnection()
    {
        while (isRunning)
        {
            try
            {
                /*
                 * Read a packet from the server.
                 */
                Packet packet = (Packet) objectIn.readObject();
                /*
                 * We've received a packet. Now we'll see if a synchronous block
                 * is waiting for it.
                 */
                BlockingQueue<Packet> thisBlock =
                    syncBlocks.get(packet.getPacketThread());
                if (thisBlock != null)
                {
                    /*
                     * There is a block waiting. We'll hand it the packet.
                     */
                    if (!thisBlock.offer(packet))
                        thisBlock = null;
                    syncBlocks.remove(packet.getPacketThread());
                }
                /*
                 * Now we'll send the packet to any listeners that are
                 * interested in processing it.
                 */
                if (thisBlock == null)
                    /*
                     * Packet wasn't dispatched to a sync block
                     */
                    notifyProcessPacket(packet);
                notifyProcessSyncBlockedPacket(packet);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                break;
            }
        }
        /*
         * The connection has ended. We'll go clean up after it.
         */
        try
        {
            Socket lSock = socket;
            socket = null;
            objectIn = null;
            spooler.close();
            spooler = null;
            notifyStatusDisconnected();
            killPendingSyncBlocks();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
    
    private void killPendingSyncBlocks()
    {
        for (String threadId : new ArrayList<String>(syncBlocks.keySet()))
        {
            BlockingQueue queue = syncBlocks.get(threadId);
            ExceptionPacket exceptionPacket = new ExceptionPacket();
            exceptionPacket.setException(new DisconnectedException());
            if (queue != null)
                queue.offer(exceptionPacket);
            syncBlocks.remove(threadId);
        }
    }
    
    private void notifyProcessPacket(Packet packet)
    {
        for (PacketListener listener : new ArrayList<PacketListener>(packetListeners))
        {
            try
            {
                listener.packetReceived(packet);
            }
            catch (ClassCastException e)
            {
                /*
                 * Will occur if the packet listener isn't listening for this
                 * type of packet; we'll just ignore it
                 */
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }
    
    private void notifyProcessSyncBlockedPacket(Packet packet)
    {
        for (PacketListener listener : new ArrayList<PacketListener>(packetListeners))
        {
            try
            {
                listener.processedPacketReceived(packet);
            }
            catch (ClassCastException e)
            {
                /*
                 * Will occur if the packet listener isn't listening for this
                 * type of packet; we'll just ignore it
                 */
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
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
    
    public void reconnect()
    {
        try
        {
            if (socket != null)
                socket.close();
        }
        catch (Exception exception)
        {
            /*
             * Typically will be an exception indicating that the socket is
             * already closed. There is a race condition, however, that occurs
             * if the socket is set to null between the two lines in the try
             * block; this will result in this exception being an NPE. Either
             * way, we'll treat it the same.
             */
            exception.printStackTrace();
        }
    }
    
    /**
     * Generates a unique thread id for this packet and sends the packet. When a
     * response is received, it is returned. If an exception occurs on the
     * server side while processing this packet, it is wrapped in a
     * ResponseException and thrown from this method. If the server connection
     * is lost, an exception is thrown.
     * 
     * @param packet
     * @return
     */
    public Packet query(Packet packet)
    {
        if (socket == null)
            throw new RuntimeException("Not connected");
        packet.setPacketThread(generateThreadId());
        BlockingQueue thisBlock = new ArrayBlockingQueue(1);
        syncBlocks.put(packet.getPacketThread(), thisBlock);
        /*
         * We've been added to the sync block map, and we have a connection. Now
         * we'll send the packet and wait for a response.
         */
        PacketSpooler lSpooler = spooler;
        if (lSpooler == null)
        {
            syncBlocks.remove(packet.getPacketThread());
            throw new RuntimeException("Not connected");
        }
        lSpooler.send(packet);
        /*
         * Now we wait for the response.
         */
        try
        {
            Packet response = (Packet) thisBlock.take();
            if (packet instanceof ExceptionPacket)
            {
                throw new ResponseException("The server threw an exception",
                    ((ExceptionPacket) packet).getException());
            }
            return response;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
    }
    
    /**
     * Sends a packet to the server, ignoring its response if the server ever
     * sends one.
     * 
     * @param packet
     */
    public void send(Packet packet)
    {
        if (socket == null)
            throw new RuntimeException("Not connected");
        spooler.send(packet);
    }
    
    private static final AtomicLong threadIdSequence = new AtomicLong();
    
    public static String generateThreadId()
    {
        return System.currentTimeMillis() + "-" + threadIdSequence.getAndIncrement();
    }
}
