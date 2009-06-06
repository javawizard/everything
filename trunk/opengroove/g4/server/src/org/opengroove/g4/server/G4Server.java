package org.opengroove.g4.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.opengroove.g4.common.G4Defaults;
import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.LoginPacket;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.server.commands.types.ComputerCommand;
import org.opengroove.g4.server.commands.types.UnauthCommand;
import org.opengroove.g4.server.commands.types.UserCommand;

public class G4Server
{
    public static HashMap<Class, Command> unauthCommands =
        new HashMap<Class, Command>();
    public static HashMap<Class, Command> computerCommands =
        new HashMap<Class, Command>();
    public static HashMap<Class, Command> userCommands = new HashMap<Class, Command>();
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
    public static String serverName;
    
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
        serverName = configProperties.getProperty("server-name");
        threadPool.allowCoreThreadTimeOut(true);
        scheduleIdleConnectionKiller();
        server = new ServerSocket(G4Defaults.CLIENT_SERVER_PORT);
        loadCommands();
        System.out.println("G4 Server is up and running.");
        runServer();
    }
    
    private static void runServer()
    {
        
    }
    
    private static void scheduleIdleConnectionKiller()
    {
        /*
         * Does nothing for now, but times out connections that are idle in the
         * future.
         */
    }
    
    private static void loadCommands()
    {
        String thisPackageName = G4Server.class.getPackage().getName();
        File commandsFolder =
            new File("classes/" + thisPackageName.replace(".", "/") + "/commands");
        File[] files = commandsFolder.listFiles(new FilenameFilter()
        {
            
            public boolean accept(File parent, String name)
            {
                return name.endsWith(".class");
            }
        });
        for (File file : files)
        {
            String name = file.getName();
            name = name.substring(0, name.length() - ".class".length());
            Command command;
            try
            {
                command =
                    (Command) Class.forName(thisPackageName + "." + name).newInstance();
            }
            catch (Exception e)
            {
                System.out.println("Exception while instantiating command " + name
                    + ":");
                e.printStackTrace();
                throw new RuntimeException(e.getClass().getName() + ": "
                    + e.getMessage(), e);
            }
            installCommand(command);
        }
    }
    
    private static void installCommand(Command command)
    {
        boolean isUnauth = command.getClass().isAnnotationPresent(UnauthCommand.class);
        boolean isUser = command.getClass().isAnnotationPresent(UserCommand.class);
        boolean isComputer =
            command.getClass().isAnnotationPresent(ComputerCommand.class);
        Method[] methods = command.getClass().getMethods();
        Class argumentType = null;
        for (Method method : methods)
        {
            if (method.getName().equals("process")
                && method.getParameterTypes().length == 1
                && Packet.class.isAssignableFrom(method.getParameterTypes()[0]))
            {
                /*
                 * If the method's name is process, the method has exactly one
                 * parameter, and that parameter is a subclass of Packet
                 */
                argumentType = method.getParameterTypes()[0];
                break;
            }
        }
        /*
         * argumentType will never be null here because of the Command
         * interface, which defines exactly the method we're searching for but
         * leaves the parameter type up to the subclass so long as it extends
         * Packet
         */
        if (isUnauth)
            unauthCommands.put(argumentType, command);
        if (isComputer)
            computerCommands.put(argumentType, command);
        if (isUser)
            userCommands.put(argumentType, command);
        if (!(isUser || isUnauth || isComputer))
            System.err.println("Warning: command class "
                + command.getClass().getSimpleName()
                + " did not specify where it should be installed");
    }
}
