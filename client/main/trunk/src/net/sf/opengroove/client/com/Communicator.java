package net.sf.opengroove.client.com;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.LoginPacket;

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
    
    private static final int MAX_WAIT_DELAY = 20;
    
    private int currentWaitDelay = 0;
    
    private Map<String, BlockingQueue<Packet>> syncBlocks =
        Collections.synchronizedMap(new HashMap<String, BlockingQueue<Packet>>());
    
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
     * packet for this to work.
     * 
     * @param packet
     */
    public void setLoginPacket(LoginPacket packet)
    {
        
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
        
    }
}
