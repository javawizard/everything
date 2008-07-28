package net.sf.opengroove.client.com;

import java.math.BigInteger;
import java.net.Socket;

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
    private String serverHost;
    private int serverPort;
    private Socket socket;
    private boolean authenticateOnConnect = false;
    private String connectionType;
    private String connectionUsername;
    private String connectionComputer;
    private String connectionPassword;
    //TODO: add some sort of list of blocking queues, where the calling blocker method
    //blocks until an item is on the queue. The object type of the queue could contain
    //either the contents of the response, or a boolean flag indicating that the response
    //couldn't be received, either because a certain timeout for the response expired, or
    //because the communicator lost it's connection to the server.
    
    public Communicator(String serverHost, int serverPort,
        BigInteger serverRsaPublic, BigInteger serverRsaMod)
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serverRsaPublic = serverRsaPublic;
        this.serverRsaMod = serverRsaMod;
    }
    
    private void performHandshake()
    {
        
    }
}
