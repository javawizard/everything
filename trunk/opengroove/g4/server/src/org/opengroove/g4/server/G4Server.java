package org.opengroove.g4.server;

import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.opengroove.g4.common.G4Defaults;
import org.opengroove.g4.common.user.Userid;

public class G4Server
{
    public static File storageFolder;
    /**
     * The message store folder, Within here is one folder for each username,
     * within that is one folder for each computer, within that is one file per
     * message which is the serialized InboundMessage packet that should be sent
     * for the message. OutboundMessage packets to be sent to other servers will
     * be stored in a similar folder in the future.
     */
    public static File messageFolder;
    /**
     * The auth folder. This contains one folder per user. The user's folder
     * contains a file called password which is the user's password. It contains
     * a folder called computers which has one file per computer in it. That
     * file is named the computer's name and is empty. In the future, it will
     * contain a file called roster whose format I haven't figured out yet.
     */
    public static File authFolder;
    public static ServerSocket server;
    public static Properties configProperties = new Properties();
    /**
     * All client-server connections that are active.
     */
    public static HashMap<Userid, ServerConnection> connections =
        new HashMap<Userid, ServerConnection>();
    
    public static ThreadPoolExecutor threadPool =
        new ThreadPoolExecutor(5, 100, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(500));
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        storageFolder = new File("storage");
        messageFolder = new File(storageFolder, "messages");
        authFolder = new File(storageFolder, "auth");
        messageFolder.mkdirs();
        authFolder.mkdirs();
        configProperties.load(new FileInputStream(new File(storageFolder,
            "config.props")));
        threadPool.allowCoreThreadTimeOut(true);
        scheduleIdleConnectionKiller();
        server = new ServerSocket(G4Defaults.CLIENT_SERVER_PORT);
        runServer();
    }
    
    private static void runServer()
    {
        // TODO Auto-generated method stub
        
    }
    
    private static void scheduleIdleConnectionKiller()
    {
        /*
         * Does nothing for now, but times out connections that are idle in the
         * future.
         */
    }
    
}
