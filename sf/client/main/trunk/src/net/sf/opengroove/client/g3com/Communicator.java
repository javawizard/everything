package net.sf.opengroove.client.g3com;

import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import DE.knp.MicroCrypt.Aes256;

import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.TrustedCertificate;
import net.sf.opengroove.common.com.DatagramUtils;
import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.security.Crypto;
import net.sf.opengroove.common.security.Hash;
import net.sf.opengroove.common.security.PromptTrustManager;
import net.sf.opengroove.common.security.RSA;
import net.sf.opengroove.common.security.TrustAlwaysListener;
import net.sf.opengroove.common.utils.StringUtils;

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
 * and the remaining items were equal to the last item in the previous array.<br/><br/>
 * 
 * It should be noted that this class does <i>not</i> automatically send ping
 * commands to keep the connection to the server alive, so classes that user
 * this class will need to take care of that themselves.
 * 
 * @author Alexander Boyd
 * 
 */
public class Communicator
{
    private static final SecureRandom random = new SecureRandom();
    public static final int[] WAIT_TIMES = { 0, 0, 2, 2, 2,
        3, 5, 10, 10, 10, 10, 10, 10, 20, 20, 20, 20, 30,
        30, 30 };
    private int waitIndex = 0;
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
    private SSLSocket socket;
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
     * A list of certificates that are trusted. If this communicator is for a
     * local user, then this will typically be the StoredList obtained from the
     * LocalUser object's getTrustedCertificates() method. If the user chooses
     * "trust always" when prompted on whether or not to trust a certificate,
     * then the certificate will be added to this list.<br/><br/>
     * 
     * The items of this list should be X509 certificates, encoded in the PEM
     * format.
     * {@link CertificateUtils#writeCert(java.security.cert.X509Certificate)}
     * can be used to encode an X509Certificate into a format suitable for
     * adding to this list.
     */
    private List<TrustedCertificate> trustedCertificateList;
    /**
     * If this is true, then this is a one-time-only communicator. This means
     * that the constructor
     * {@link #Communicator(String, boolean, String, String, String, String, BigInteger, BigInteger)}
     * will not return until either a connection has been established, or
     * establishing a connection failed (IE the coordinator has died). In
     * addition, when the coordinator loses it's connection, it will die
     * immediately without repeatedly trying to connect again.
     */
    private boolean isOneTime;
    /**
     * The object that the constructor will lock on when isOneTime = true. Just
     * before the coordinator dies, it will notify this object. It will also
     * notify it when it successfully connects to the server. The constructor
     * waits for 1000ms on this object repeatedly.
     */
    private final Object oneTimeLock = new Object();
    /**
     * If one-time initialization fails because of an exception, the exception
     * is placed here.
     */
    private Exception oneTimeException;
    /**
     * The OpenGroove CA Certificate.
     */
    private X509Certificate rootCertificate;
    
    /**
     * Creates a new Communicator object. The communicator will attempt to
     * re-establish a connection to the server whenever it's connection is lost,
     * unless <code>isOneTime</code> is true.<br/><br/>
     * 
     * Note that, upon the first packet being received from the server, the
     * coordinator thread will pause until at least one packet listener is
     * present. This ensures that no packets will be received without a packet
     * listener to sink them.
     * 
     * @param certErrorOwner
     *            A window that will be used as the parent of the certificate
     *            error dialog. If this is null, the certificate error dialog
     *            will be shown without a parent window.
     * @param realm
     *            The name of the realm to connect to. A server will be
     *            automatically selected from among the specified realm's
     *            servers.
     * @param auth
     *            True to automatically run the authenticate command upon
     *            connecting to the server, false if not.
     * @param isOneTime
     *            True for the communicator to connect just once, false to
     *            attempt to stay connected by re-connecting if a connection is
     *            lost. If this is true, the constructor will not return until a
     *            connection to the server is established, and will throw an
     *            exception if a connection cannot be established.
     * @param connectionType
     *            The type of connection. This is typically normal, although if
     *            the communicator will be used for channel communication, then
     *            this would be channel.
     * @param connectionUsername
     *            If auth is true, the username to authenticate with
     * @param connectionComputer
     *            If auth is true, the computer name to authenticate with
     * @param connectionPassword
     *            If auth is true, the plain-text password to authenticate with
     * @param serverRsaPublic
     *            The RSA public key of the server to connect to. This can
     *            either be exported from the server via it's web interface, or
     *            it can be downloaded from the public server list for servers
     *            that are listed there.
     * @param serverRsaMod
     *            The RSA modulus of the server to connect to. This can either
     *            be exported from the server via it's web interface, or it can
     *            be downloaded from the public server list for servers that are
     *            listed there.
     * @param initialStatusListener
     *            A status listener to register before doing anything with
     *            connecting. If an initial status listener is not required,
     *            this can be null. This is provided since registering a status
     *            listener to this communicator immediately after constructing
     *            it is not safe since the communicator's coordinator thread may
     *            have established a connection, and the status listener would
     *            have missed some status events.
     * @param initialPacketListener
     *            A packet listener to register before doing anything with
     *            connecting.
     */
    
    public Communicator(final Window certErrorOwner,
        String realm, boolean auth, boolean isOneTime,
        String connectionType, String connectionUsername,
        String connectionComputer,
        String connectionPassword,
        List<TrustedCertificate> trustedCertificates,
        StatusListener initialStatusListener,
        PacketListener initialPacketListener)
    {
        this.realm = realm;
        this.trustedCertificateList = trustedCertificates;
        this.rootCertificate = CertificateUtils
            .readCert(StringUtils.readFile(new File(
                "cacert.pem")));
        this.authenticateOnConnect = auth;
        this.isOneTime = isOneTime;
        this.connectionType = connectionType;
        this.connectionComputer = connectionComputer;
        this.connectionUsername = connectionUsername;
        this.connectionPassword = connectionPassword;
        if (initialStatusListener != null)
            statusListeners.add(initialStatusListener);
        if (initialPacketListener != null)
            packetListeners.add(initialPacketListener);
        coordinator = new Thread("Communicator-Manager")
        {
            public void run()
            {
                boolean isFirst = true;
                while (isRunning
                    && ((!Communicator.this.isOneTime) || isFirst))
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
                        waitIndex++;
                        if (waitIndex >= WAIT_TIMES.length)
                            waitIndex = WAIT_TIMES.length - 1;
                        Thread
                            .sleep(WAIT_TIMES[waitIndex] * 1000);
                    }
                    catch (Exception ex1)
                    {
                        ex1.printStackTrace();
                    }
                    if (!isRunning)
                        return;
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
                        timeoutCount = 0;
                        final ServerContext[] servers = ConnectionResolver
                            .lookup(Communicator.this.realm);
                        boolean setupConnection = false;
                        SSLSocket tSocket = null;
                        for (int i = 0; i < servers.length; i++)
                        {
                            final int fI = i;
                            try
                            {
                                SSLContext context = SSLContext
                                    .getInstance("TLS");
                                ArrayList<X509Certificate> builtCertificateList = new ArrayList<X509Certificate>();
                                for (TrustedCertificate s : trustedCertificateList)
                                {
                                    builtCertificateList
                                        .add(CertificateUtils
                                            .readCert(s
                                                .getEncoded()));
                                }
                                PromptTrustManager trustmanager = new PromptTrustManager(
                                    certErrorOwner,
                                    rootCertificate,
                                    trustedCertificateList == null ? new ArrayList<X509Certificate>()
                                        : builtCertificateList,
                                    new TrustAlwaysListener()
                                    {
                                        
                                        public void trustAlways(
                                            X509Certificate endCertificate)
                                        {
                                            String encoded = CertificateUtils
                                                .writeCert(endCertificate);
                                            TrustedCertificate tcert = Storage
                                                .createTrustedCertificate(encoded);
                                            trustedCertificateList
                                                .add(tcert);
                                        }
                                    });
                                context
                                    .init(
                                        new KeyManager[0],
                                        new TrustManager[] { trustmanager },
                                        new SecureRandom());
                                SocketFactory socketFactory = context
                                    .getSocketFactory();
                                tSocket = (SSLSocket) socketFactory
                                    .createSocket(
                                        servers[i]
                                            .getHostname(),
                                        servers[i]
                                            .getPort());
                                SSLSession session = tSocket
                                    .getSession();
                                X509Certificate serverCert = (X509Certificate) session
                                    .getPeerCertificates()[0];
                                if (!serverCert
                                    .getSubjectX500Principal()
                                    .getName()
                                    .equalsIgnoreCase(
                                        "CN="
                                            + Communicator.this.realm))
                                    throw new RuntimeException(
                                        "The certificate was issued to "
                                            + serverCert
                                                .getSubjectX500Principal()
                                                .getName()
                                            + " but the realm needs a certificate issued to CN="
                                            + Communicator.this.realm);
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
                                performHandshake(tSocket);
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
                        if (!isRunning)
                        {
                            socket.close();
                            return;
                        }
                        if (authenticateOnConnect)
                        {
                            Packet authPacket = new Packet(
                                "_auth",
                                "authenticate",
                                (Communicator.this.connectionType
                                    + "\n"
                                    + Communicator.this.connectionUsername
                                    + "\n"
                                    + Communicator.this.connectionComputer
                                    + "\n" + Communicator.this.connectionPassword)
                                    .getBytes());
                            System.out
                                .println("sending packet "
                                    + authPacket);
                            send(authPacket, tSocket,
                                tSocket.getOutputStream());
                            tSocket.getOutputStream()
                                .flush();
                        }
                        
                        // Ok, we have a connection to the server, we've
                        // performed a handshake, and we've sent an
                        // authentication request if the user wanted one to be
                        // sent. Now we inject the streams into the in and out
                        // fields, inject the socket into the socket field, and
                        // start listening for response packets.
                        in = tSocket.getInputStream();
                        out = tSocket.getOutputStream();
                        socket = tSocket;
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
                        synchronized (oneTimeLock)
                        {
                            oneTimeLock.notifyAll();
                        }
                        // Now the actual listening stuff.
                        boolean b = true;
                        while (b)
                        {
                            if (!isRunning)
                            {
                                socket.close();
                                return;
                            }
                            byte[] packet = DatagramUtils
                                .read(in, 65535);
                            byte[] first128bytes = new byte[Math
                                .min(128, packet.length)];
                            System.arraycopy(packet, 0,
                                first128bytes, 0,
                                first128bytes.length);
                            String first128 = new String(
                                first128bytes);
                            String[] first128split = first128
                                .split("\\ ", 4);
                            if (!isRunning)
                            {
                                socket.close();
                                return;
                            }
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
                            while (packetListeners.size() == 0)
                                Thread.sleep(500);
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
                        oneTimeException = e;
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
                isRunning = false;
                for (StatusListener listener : new ArrayList<StatusListener>(
                    statusListeners))
                {
                    try
                    {
                        listener
                            .communicatorShutdown(Communicator.this);
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
                synchronized (oneTimeLock)
                {
                    oneTimeLock.notifyAll();
                }
            }
        };
        coordinator.start();
        if (isOneTime)
        {
            while (isAlive() && isRunning && socket == null)
            {
                synchronized (oneTimeLock)
                {
                    try
                    {
                        oneTimeLock.wait(1000);
                    }
                    catch (InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (oneTimeException != null)
            {
                throw new RuntimeException(
                    "Setting up the communicator failed",
                    oneTimeException);
            }
            if ((!isRunning) || (!isAlive()))
                throw new RuntimeException(
                    "Setting up the communicator failed for an unknown reason");
        }
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
    
    /**
     * Sends the packet specified to the server, and waits the specified number
     * of milliseconds for a response to the packet. The packet's id will be set
     * to a unique id, and the response packet's id will be the same.<br/><br/>
     * 
     * Currently, if the response status code is anything other than OK, a
     * FailedResponseException is thrown.
     * 
     * @param packet
     *            The packet to send
     * @param timeout
     *            The number of milliseconds to wait for a response before
     *            throwing a TimeoutException
     * @return The response from the server to this packet
     * @throws IOException
     *             if an I/O exception occurs.
     * @throws FailedResponseException
     *             If the response status code is anything other than OK
     */
    public Packet query(Packet packet, int timeout)
        throws IOException
    {
        return query(packet, timeout, socket);
    }
    
    private int timeoutCount;
    /**
     * If this many timeouts occur, then the communicator assumes that something
     * has happened to it's connection, and it reconnects the connection.
     */
    private int MAX_TIMEOUTS = 20;
    
    private Packet query(Packet packet, int timeout,
        Socket socket) throws IOException
    {
        try
        {
            packet.setPacketId(generateRuntimeId());
            BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>(
                1);
            syncBlocks.put(packet.getPacketId(), queue);
            send(packet, socket, socket.getOutputStream());
            Packet responsePacket = queue.poll(timeout,
                TimeUnit.MILLISECONDS);
            if (responsePacket == CONNECTION_ERROR)
                throw new IOException(
                    "A connection error was encountered "
                        + "while waiting for a response.");
            if (responsePacket == null)
            {
                if (timeoutCount++ > MAX_TIMEOUTS)
                {
                    try
                    {
                        timeoutCount = 0;
                        reconnect();
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
                throw new TimeoutException(
                    "The specified timeout expired before "
                        + "a response was received. The command was "
                        + packet.getCommand()
                        + " with first128 equal to "
                        + new String(packet.getContents(),
                            0, Math.min(packet
                                .getContents().length, 128))
                        + " packet id "
                        + packet.getPacketId());
            }
            if (!responsePacket.getResponse().trim()
                .equalsIgnoreCase("OK"))
            {
                if (responsePacket.getResponse().trim()
                    .equalsIgnoreCase("AUTHUNAUTHORIZED"))
                {
                    reconnect();
                }
                throw new FailedResponseException(
                    responsePacket.getResponse(),
                    "For response code: \""
                        + responsePacket.getResponse()
                        + "\" with responsecontents "
                        + new String(responsePacket
                            .getContents(), 0, Math
                            .min(responsePacket
                                .getContents().length, 128))
                        + " and message "
                        + new String(packet.getContents(),
                            0, Math.min(packet
                                .getContents().length, 128)));
            }
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
     * @return a byte array who's length is the length of all of the input byte
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
        send(packet, socket, out);
    }
    
    private synchronized void send(Packet packet,
        Socket socket, OutputStream out) throws IOException
    {
        // TODO: instead of this method being synchronized, have it push packets
        // on to a packet spooler, which then forwards them to the server.
        if (out == null || socket == null
            || socket.isClosed()
            || socket.isOutputShutdown())
            throw new IOException(
                "The communicator doesn't have an active "
                    + "connection to the server right now.");
        DatagramUtils.write(concat((""
            + packet.getPacketId() + " "
            + packet.getCommand() + " ").getBytes(), packet
            .getContents()), out);
    }
    
    /**
     * This method performs a handshake with the server. This does not include
     * running the authenticate command, it only includes negotiating security
     * keys with the server, and everything up to where you can communicate over
     * this socket using the methods in DatagramUtils.
     * 
     * @param socket
     *            The socket on which to perform the handshake
     * @throws IOException
     *             if an I/O error occures
     */
    private synchronized void performHandshake(
        SSLSocket socket) throws IOException
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
    
    /**
     * Returns true if this communicator is active. This means that it currently
     * has a connection to the server.
     * 
     * @return
     */
    public boolean isActive()
    {
        try
        {
            if (socket == null)
                return false;
            return (!socket.isClosed())
                && socket.isConnected();
        }
        // Catch an NPE in addition to if/else test for null to solve concurrent
        // threading issues
        catch (NullPointerException e)
        {
            return false;
        }
    }
    
    /**
     * Returns this communicator's socket, or null if it doesn't have a
     * connection to the server right now.
     * 
     * @return
     */
    public Socket getSocket()
    {
        return socket;
    }
    
    /**
     * Returns the host that this communicator is currently connected to, or
     * null if it doesn't have a connection right now.
     * 
     * @return
     */
    public String getConnectedHost()
    {
        if (socket == null)
            return null;
        return socket.getInetAddress().getHostName();
    }
    
    /**
     * Returns the port that this communicator is connected on, or -1 if it's
     * not connected right now.
     * 
     * @return
     */
    public int getConnectedPort()
    {
        if (socket == null)
            return -1;
        return socket.getPort();
    }
    
    /**
     * Terminates this communicator's current connection, but keeps it running.
     * The communicator will, naturally, try to connect again as soon as
     * possible.
     */
    public void reconnect()
    {
        try
        {
            socket.close();
            in = null;
            out = null;
            socket = null;
        }
        catch (Exception e)
        {
        }
    }
}
