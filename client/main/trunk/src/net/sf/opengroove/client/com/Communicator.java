package net.sf.opengroove.client.com;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.sf.opengroove.security.Crypto;

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
    public static final int[] WAIT_TIMES = { 0, 0, 2, 3, 5,
        10, 10, 10, 10, 10, 10, 20, 20 };
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
                while(isRunning)
                {
                    
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
    
    public synchronized void send(Packet packet)
        throws IOException
    {
        // TODO: instead of this method being synchronized, have it push packets
        // on to a packet spooler, which then forwards them to the server.
        
    }
    
    private synchronized void performHandshake()
    {
        
    }
}
