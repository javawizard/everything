package net.sf.opengroove.client.com;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
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
        10, 10, 10, 10, 10, 10, 20, 20 };
    private int waitIndex = 0;
    private BigInteger serverRsaPublic;
    private BigInteger serverRsaMod;
    private String realm;
    private boolean authenticateOnConnect = false;
    private String connectionType;
    private String connectionUsername;
    private String connectionComputer;
    private String connectionPassword;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Thread coordinator;
    // TODO: add some sort of list of blocking queues, where the calling blocker
    // method
    // blocks until an item is on the queue. The object type of the queue could
    // contain
    // either the contents of the response, or a boolean flag indicating that
    // the response
    // couldn't be received, either because a certain timeout for the response
    // expired, or
    // because the communicator lost it's connection to the server.
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
    
    public Communicator(String realm,
        BigInteger serverRsaPublic, BigInteger serverRsaMod)
    {
        this.realm = realm;
        this.serverRsaPublic = serverRsaPublic;
        this.serverRsaMod = serverRsaMod;
        coordinator = new Thread()
        {
            public void run()
            {
                while (isRunning)
                {
                    try
                    {
                        Thread
                            .sleep(WAIT_TIMES[waitIndex++] * 1000);
                    }
                    catch (Exception ex1)
                    {
                        ex1.printStackTrace();
                    }
                    // First, we create a socket, and perform the handshake. We
                    // don't want to put the socket in the field socket until
                    // the handshake is complete and the initial authenticate
                    // command sent, to avoid packet conflicts.
                    try
                    {
                        
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
            }
        };
        coordinator.start();
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
     * of milliseconds for a response to the packet.
     * 
     * @param packet
     *            The packet to send
     * @param timeout
     *            The number of milliseconds to wait for a response before
     *            throwing a TimeoutException
     * @return
     */
    public Packet query(Packet packet, int timeout)
        throws IOException
    {
        try
        {
            BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>(
                1);
            syncBlocks.put(packet.getPacketId(), queue);
            send(packet);
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
     * @return
     */
    private static byte[] concat(byte[]... bytes)
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
    
    public synchronized void send(Packet packet)
        throws IOException
    {
        // TODO: instead of this method being synchronized, have it push packets
        // on to a packet spooler, which then forwards them to the server.
        if (out == null || socket == null
            || socket.isClosed()
            || socket.isOutputShutdown())
            throw new IllegalStateException(
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
}
