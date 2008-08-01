package net.sf.opengroove.client.com;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import DE.knp.MicroCrypt.Aes256;

import net.sf.opengroove.security.Crypto;
import net.sf.opengroove.security.Hash;
import net.sf.opengroove.security.RSA;

/**
 * This class can be used to communicate with an OpenGroove server. It takes
 * care of performing the handshake. It also takes care of ensuring that there
 * is an active connection to the server at all times. Additionally, it can be
 * set to re-issue the authenticate command if it needs to re-connect.<br/><br/>
 * 
 * <code>CommandListener</code>s can be added to the class to be alerted of
 * commands or responses that are received. This class also has synchronous (IE
 * blocking) methods for calling a command and waiting for a response to that
 * command. When a <code>CommandListener</code> is registered, it can specify
 * whether or not to notify it of synchronous responses as well as asynchrounous
 * responses. A synchronous response is one where a command method is currently
 * blocking waiting for that response, and an asynchronous response is one where
 * there is no method blocking for the result of that command.<br/><br/>
 * 
 * When the connecton to the server is lost, this class will throw an exception
 * from all blocking synchronous command methods, and attempt to re-establish a
 * connection. Once it has managed to do so, it will call a corresponding method
 * on all StatusListeners to alert them of this fact. It will then run the
 * authenticate command if it has been set to do so upon connection
 * re-establishment. If the authenticate command fails, the StatusListeners will
 * be notified and the connection will be dropped, and re-tried later. If it
 * succeeds, then command methods can again be called on this class.<br/><br/>
 * 
 * If this class loses it's connection to the server, it will wait a length
 * equal to the number of seconds specified in the first element of the array
 * WAIT_TIMES, and then try to re-connect. If this fails, it will move to the
 * next element of the aforementioned array and wait again before attempting to
 * re-connect. This goes on until a connection is established. If the last
 * element of the array is encountered, it will continue to wait that number of
 * seconds and then attempt to re-connect, as if the array's length was infinite
 * and the remaining items were equal to the last item in the previous array.
 * 
 * @author Alexander Boyd
 * 
 */
public class Communicator
{
    private static final SecureRandom random = new SecureRandom();
    public static final int[] WAIT_TIMES = { 0, 0, 2, 3, 5,
        10, 10, 10, 10, 10, 10, 20, 20, 20, 20, 30, 30, 30 };
    private int waitIndex = 0;
    private BigInteger serverRsaPublic;
    private BigInteger serverRsaMod;
    private String realm;
    private boolean authenticateOnConnect = false;
    /**
     * This is the status of the last auto-auth. If it's null, then the server
     * isn't connected right now, or an auto-auth wasn't requested. If it's not
     * null, then it's value is either "OK" or one of the response codes
     * returned by the server in response to an authenticate command.
     */
    private String lastAuthStatus = null;
    private String connectionType;
    private String connectionUsername;
    private String connectionComputer;
    private String connectionPassword;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Thread coordinator;
    /**
     * This is a list of all status listeners registered to this communicator
     * instance.
     */
    private ArrayList<StatusListener> statusListeners = new ArrayList<StatusListener>();
    
    public void addStatusListener(StatusListener listener)
    {
        statusListeners.add(listener);
    }
    
    public void removeStatusListener(StatusListener listener)
    {
        statusListeners.remove(listener);
    }
    
    /**
     * This is a list of all status listeners registered to this communicator
     * instance.
     */
    private ArrayList<PacketListener> packetListeners = new ArrayList<PacketListener>();
    
    public void addPacketListener(PacketListener listener)
    {
        packetListeners.add(listener);
    }
    
    public void removePacketListener(PacketListener listener)
    {
        packetListeners.remove(listener);
    }
    
    /**
     * This map is used to store a list of queues that correspond to
     * currently-blocking invocations of the synchronous communication methods.
     * The key is the packet id to watch for, and the value is a queue that an
     * incoming response should be placed into.
     */
    private Map<String, BlockingQueue<Packet>> syncBlocks = Collections
        .synchronizedMap(new HashMap<String, BlockingQueue<Packet>>());
    /**
     * This packet is added to all queues in the field <code>syncBlocks</code>
     * if a stream error occures while communicating with the server.
     * Synchronous methods for receiving packets can check to see if the packet
     * received is identity equal to this packet, and, if so, throw an
     * exception.
     */
    private static final Packet CONNECTION_ERROR = new Packet();
    
    private boolean isRunning = true;
    /**
     * The security key for the current connection. Information sent across the
     * connection is encrypted and decrypted with this key.
     */
    private Aes256 securityKey;
    
    public Communicator(String realm, boolean auth,
        String connectionType, String connectionUsername,
        String connectionComputer,
        String connectionPassword,
        BigInteger serverRsaPublic, BigInteger serverRsaMod)
    {
        this.realm = realm;
        this.serverRsaPublic = serverRsaPublic;
        this.serverRsaMod = serverRsaMod;
        this.authenticateOnConnect = auth;
        this.connectionType = connectionType;
        this.connectionComputer = connectionComputer;
        this.connectionUsername = connectionUsername;
        this.connectionPassword = connectionPassword;
        coordinator = new Thread("Communicator-Manager")
        {
            public void run()
            {
                boolean isFirst = true;
                while (isRunning)
                {
                    // If the wait index is greater than 2, clear the cache.
                    // This is used so that old lookup entries that would
                    // otherwise prevent new ones from being downloaded (because
                    // of the cache) don't block up connections to the server.
                    // This also makes it so that every time the client is
                    // disconnected from the internet, and then re-connects, the
                    // dns lookup is performed again, which is a bit of a
                    // performance hit, but I can't think of any better way to
                    // do it right now.
                    if (!isFirst)
                        notifyStatusListeners(new Notifier<StatusListener>()
                        {
                            
                            @Override
                            public void notify(
                                StatusListener listener)
                            {
                                listener
                                    .connectionLost(Communicator.this);
                            }
                        });
                    isFirst = false;
                    lastAuthStatus = null;
                    if (waitIndex > 2)
                        ConnectionResolver.clearCache();
                    try
                    {
                        Thread
                            .sleep(WAIT_TIMES[waitIndex++] * 1000);
                    }
                    catch (Exception ex1)
                    {
                        ex1.printStackTrace();
                    }
                    // First, we get a list of servers to connect to from the
                    // ConnectionResolver class. Then, for each server, in the
                    // order returned, we try to connect. If connecting to a
                    // particular server fails (even if a connection is
                    // established but the handshake fails), we advance to the
                    // next server. If we've cycled through all of the servers
                    // without a successful connection, then we continue with
                    // the next while loop.
                    try
                    {
                        final ServerContext[] servers = ConnectionResolver
                            .lookup(Communicator.this.realm);
                        boolean setupConnection = false;
                        Socket tSocket = null;
                        for (int i = 0; i < servers.length; i++)
                        {
                            final int fI = i;
                            try
                            {
                                tSocket = new Socket(
                                    servers[i]
                                        .getHostname(),
                                    servers[i].getPort());
                                notifyStatusListeners(new Notifier<StatusListener>()
                                {
                                    
                                    @Override
                                    public void notify(
                                        StatusListener listener)
                                    {
                                        listener
                                            .connectionEstablished(
                                                Communicator.this,
                                                servers[fI]);
                                    }
                                });
                                securityKey = performHandshake(tSocket);
                                setupConnection = true;
                                break;
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                try
                                {
                                    tSocket.close();
                                }
                                catch (Exception e2)
                                {
                                }
                            }
                        }
                        if (!setupConnection)
                        {
                            System.err
                                .println("Failed to set up a connection.");
                            continue;
                        }
                        if (authenticateOnConnect)
                        {
                            Packet authPacket = new Packet(
                                "_auth",
                                "authenticate",
                                (Communicator.this.connectionType
                                    + "\n"
                                    + Communicator.this.connectionUsername
                                    + "\n" + Communicator.this.connectionPassword)
                                    .getBytes());
                            send(authPacket, tSocket);
                        }
                        notifyStatusListeners(new Notifier<StatusListener>()
                        {
                            
                            @Override
                            public void notify(
                                StatusListener listener)
                            {
                                listener
                                    .connectionReady(Communicator.this);
                            }
                            
                        });
                        // Ok, we have a connection to the server, we've
                        // performed a handshake, and we've sent an
                        // authentication request if the user wanted one to be
                        // sent. Now we inject the streams into the in and out
                        // fields, inject the socket into the socket field, and
                        // start listening for response packets.
                        in = tSocket.getInputStream();
                        out = tSocket.getOutputStream();
                        socket = tSocket;
                        // Now the actual listening stuff.
                        boolean b = true;
                        while (b)
                        {
                            byte[] packet = Crypto.dec(
                                securityKey, in, 65535);
                            byte[] first128bytes = new byte[Math
                                .min(128, packet.length)];
                            System.arraycopy(packet, 0,
                                first128bytes, 0,
                                first128bytes.length);
                            String first128 = new String(
                                first128bytes);
                            String[] first128split = first128
                                .split("\\ ", 4);
                            if (first128split.length < 4)

                            {
                                System.err
                                    .println("Packet received that was too short");
                                continue;
                            }
                            String packetId = first128split[0];
                            String commandName = first128split[1];
                            String responseName = first128split[2];
                            int startDataIndex = packetId
                                .length()
                                + commandName.length()
                                + responseName.length() + 3;
                            byte[] data = new byte[packet.length
                                - (startDataIndex)];
                            System.arraycopy(packet,
                                startDataIndex, data, 0,
                                data.length);
                            final Packet iPacket = new Packet(
                                packetId, commandName,
                                responseName, data);
                            // We have a valid packet. Now we need to process
                            // it. First, if it's packetid is _auth, then we
                            // check to see if it was successful, and notify the
                            // status listeners about this. If it's packetid is
                            // not _auth, we then check to see if it has a sync
                            // block queue in the syncBlocks map, and if it
                            // does, we post the packet to the sync block queue.
                            // Then (regardless of whether there was or wasn't a
                            // sync block queue), we scan through all of the
                            // packetlisteners and hand them the packet.
                            if (packetId
                                .equalsIgnoreCase("_auth"))
                            {
                                // This is the response to the auto-auth packet.
                                // If it was successful, we stick that value
                                // into lastAuthStatus and notify all
                                // StatusListeners. If it failed, we pretty much
                                // do the same thing.
                                lastAuthStatus = responseName;
                                notifyStatusListeners(new Notifier<StatusListener>()
                                {
                                    
                                    @Override
                                    public void notify(
                                        StatusListener listener)
                                    {
                                        if (lastAuthStatus
                                            .equalsIgnoreCase("OK"))
                                            listener
                                                .authenticationSuccessful(Communicator.this);
                                        else
                                            listener
                                                .authenticationFailed(
                                                    Communicator.this,
                                                    iPacket);
                                    }
                                });
                            }
                            else
                            {
                                BlockingQueue<Packet> syncQueue = syncBlocks
                                    .get(packetId);
                                if (syncQueue != null)
                                {
                                    // A sync block queue for this packet was
                                    // found, so we'll send the packet to it.
                                    syncQueue
                                        .offer(iPacket);
                                }
                                // Now we'll loop through all of the
                                // PacketHandlers and send the packet to them.
                                notifyPacketListeners(new Notifier<PacketListener>()
                                {
                                    
                                    @Override
                                    public void notify(
                                        PacketListener listener)
                                    {
                                        listener
                                            .receive(iPacket);
                                    }
                                });
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        System.err
                            .println("Uncategorized exception while connecting to server");
                        e.printStackTrace();
                        try
                        {
                            socket.close();
                        }
                        catch (Exception e2)
                        {
                            
                        }
                        finally
                        {
                            socket = null;
                            in = null;
                            out = null;
                        }
                        try
                        {
                            for (BlockingQueue<Packet> queue : new ArrayList<BlockingQueue<Packet>>(
                                syncBlocks.values()))
                            {
                                try
                                {
                                    queue
                                        .offer(CONNECTION_ERROR);
                                }
                                catch (Exception exception)
                                {
                                    exception
                                        .printStackTrace();
                                }
                            }
                        }
                        catch (Exception e2)
                        {
                            
                        }
                    }
                }
            }
        };
        coordinator.start();
    }
    
    private void notifyPacketListeners(
        Notifier<PacketListener> notifier)
    {
        for (PacketListener listener : new ArrayList<PacketListener>(
            packetListeners))
        {
            try
            {
                notifier.notify(listener);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }
    
    /**
     * Returns true if this communicator is still alive. If a communicator has
     * died (IE this method returns false), then this communicator can no longer
     * be used, and a new one must be constructed.
     * 
     * @return
     */
    public boolean isAlive()
    {
        return coordinator.isAlive();
    }
    
    /**
     * Stops this communicator. After this is called, isAlive() should be polled
     * until it returns false, which indicates that the communicator has
     * finished shutting down.
     * 
     * @return
     */
    public void shutdown()
    {
        
    }
    
    /**
     * Sends the packet specified to the server, and waits the specified number
     * of milliseconds for a response to the packet. The packet's id will be set
     * to a unique id, and the response packet's id will be the same.
     * 
     * @param packet
     *            The packet to send
     * @param timeout
     *            The number of milliseconds to wait for a response before
     *            throwing a TimeoutException
     * @return The response from the server to this packet
     * @throws IOException
     *             if an I/O exception occurs.
     */
    public Packet query(Packet packet, int timeout)
        throws IOException
    {
        return query(packet, timeout, socket);
    }
    
    public Packet query(Packet packet, int timeout,
        Socket socket) throws IOException
    {
        try
        {
            packet.setPacketId(generateRuntimeId());
            BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>(
                1);
            syncBlocks.put(packet.getPacketId(), queue);
            send(packet, socket);
            Packet responsePacket = queue.poll(timeout,
                TimeUnit.MILLISECONDS);
            if (responsePacket == CONNECTION_ERROR)
                throw new IOException(
                    "A connection error was encountered "
                        + "while waiting for a response.");
            if (responsePacket == null)
                throw new TimeoutException(
                    "The specified timeout expired before "
                        + "a response was received.");
            return responsePacket;
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            syncBlocks.remove(packet.getPacketId());
        }
    }
    
    /**
     * concatenates a bunch of byte arrays together.
     * 
     * @param bytes
     *            the byte arrays to concatenate
     * @return a byte array. who's length is the length of all of the input byte
     *         arrays added together, and who's contents are the contents of the
     *         input byte arrays, one after another.
     */
    static byte[] concat(byte[]... bytes)
    {
        int length = 0;
        for (byte[] cb : bytes)
        {
            length += cb.length;
        }
        byte[] result = new byte[length];
        int pointer = 0;
        for (byte[] cb : bytes)
        {
            System.arraycopy(cb, 0, result, pointer,
                cb.length);
            pointer += cb.length;
        }
        return result;
    }
    
    /**
     * Sends the specified packet asynchronously. A PacketListener must be
     * registered in order to receive responses for this packet. Unlike query(),
     * this method does not generate a unique id for the packet. If a unique id
     * is desired, it must be generated by calling a method such as
     * generateRuntimeId().
     * 
     * @param packet
     * @throws IOException
     */
    public void send(Packet packet) throws IOException
    {
        send(packet, socket);
    }
    
    private synchronized void send(Packet packet,
        Socket socket) throws IOException
    {
        // TODO: instead of this method being synchronized, have it push packets
        // on to a packet spooler, which then forwards them to the server.
        if (out == null || socket == null
            || socket.isClosed()
            || socket.isOutputShutdown())
            throw new IOException(
                "The communicator doesn't have an active "
                    + "connection to the server right now.");
        Crypto.enc(securityKey, concat((""
            + packet.getPacketId() + " "
            + packet.getCommand() + " ").getBytes(), packet
            .getContents()), out);
    }
    
    /**
     * This method performs a handshake with the server. This does not include
     * running the authenticate command, it only includes negotiating security
     * keys with the server, and everything up to where you can use Crypto.ec()
     * and Crypto.dc() to send and receive packets.
     * 
     * @param socket
     *            The socket on which to perform the handshake
     * @return An AES-256 security key generated during the handshake. This
     *         should be passed to all calls of Crypto.ec() and Crypto.dc() used
     *         to communicate with this socket.
     * @throws IOException
     *             if an I/O error occures
     */
    private synchronized Aes256 performHandshake(
        Socket socket) throws IOException
    {
        final OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        out.write("OpenGroove\n".getBytes());
        String s = "";
        for (int i = 0; i < 30; i++)
        {
            int read = in.read();
            s += (char) read;
            if ((read == '\r' || read == '\n') && i != 0)
                break;
            if (i == 29)
                throw new ProtocolMismatchException(
                    "too much first initialization data sent by the server");
        }
        s = s.trim();
        if (!s.equalsIgnoreCase("OpenGrooveServer"))
        {
            throw new ProtocolMismatchException(
                "Invalid initial response sent");
        }
        BigInteger aesRandomNumber = new BigInteger(3060,
            random);
        byte[] securityKeyBytes = new byte[32];
        System.arraycopy(aesRandomNumber.toByteArray(), 0,
            securityKeyBytes, 0, 32);
        final Aes256 securityKey = new Aes256(
            securityKeyBytes);
        BigInteger securityKeyEncrypted = RSA.encrypt(
            serverRsaPublic, serverRsaMod, aesRandomNumber);
        out
            .write((securityKeyEncrypted.toString(16) + "\n")
                .getBytes());
        BigInteger randomServerCheckInteger = new BigInteger(
            3060, random);
        byte[] randomServerCheckBytes = new byte[16];
        System.arraycopy(randomServerCheckInteger
            .toByteArray(), 0, randomServerCheckBytes, 0,
            16);
        BigInteger serverCheckEncrypted = RSA.encrypt(
            serverRsaPublic, serverRsaMod,
            randomServerCheckInteger);
        out
            .write((serverCheckEncrypted.toString(16) + "\n")
                .getBytes());
        out.flush();
        s = "";
        for (int i = 0; i < 1024; i++)
        {
            int read = in.read();
            s += (char) read;
            if ((read == '\r' || read == '\n') && i != 0)
                break;
            if (i == 1023)
                throw new ProtocolMismatchException(
                    "too much second initialization data sent by the server");
        }
        s = s.trim();
        byte[] confirmServerCheckBytes = new byte[16];
        // FIXME: arrayindexoutofboundsexception for small arrays
        securityKey.decrypt(new BigInteger(s, 16)
            .toByteArray(), 0, confirmServerCheckBytes, 0);
        if (!Arrays.equals(randomServerCheckBytes,
            confirmServerCheckBytes))
            throw new RuntimeException(
                "Server failed check bytes with sent "
                    + Hash.hexcode(randomServerCheckBytes)
                    + " and received "
                    + Hash.hexcode(confirmServerCheckBytes)
                    + " and unenc received "
                    + Hash.hexcode(new BigInteger(s, 16)
                        .toByteArray()));
        out.write('c');
        out.flush();
        for (int i = 0; i < 5; i++)
        {
            if (in.read() == 'c')
                break;
            if (i == 4)
                throw new ProtocolMismatchException(
                    "no terminating 'c' at end of handshake");
        }
        byte[] antiReplayMessage = Crypto.dec(securityKey,
            in, 200);
        String antiReplayHash = Hash
            .hash(antiReplayMessage);
        Crypto.enc(securityKey, antiReplayHash.getBytes(),
            out);
        out.flush();
        return securityKey;
    }
    
    private static volatile long cid = 1;
    
    /**
     * Generates a unique id. The id is only guaranteed to be unique throughout
     * this JVM instance, so it should only be used for stuff such as generating
     * unique packet ids.
     * 
     * @return
     */
    public static String generateRuntimeId()
    {
        return "p" + (cid++);
    }
    
    private void notifyStatusListeners(
        Notifier<StatusListener> notifier)
    {
        for (StatusListener listener : new ArrayList<StatusListener>(
            statusListeners))
        {
            try
            {
                notifier.notify(listener);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }
}
