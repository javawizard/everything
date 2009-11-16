package org.opengroove.g4.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.g4.common.G4Defaults;
import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.data.ByteBlock;
import org.opengroove.g4.common.messaging.Message;
import org.opengroove.g4.common.messaging.MessageAttachment;
import org.opengroove.g4.common.messaging.MessageHeader;
import org.opengroove.g4.common.protocol.InboundMessagePacket;
import org.opengroove.g4.common.protocol.LoginPacket;
import org.opengroove.g4.common.protocol.PresencePacket;
import org.opengroove.g4.common.protocol.RosterPacket;
import org.opengroove.g4.common.roster.Contact;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.common.utils.PropUtils;
import org.opengroove.g4.server.commands.types.ComputerCommand;
import org.opengroove.g4.server.commands.types.UnauthCommand;
import org.opengroove.g4.server.commands.types.UserCommand;

public class G4Server
{
    public static final Object authLock = new Object();
    public static HashMap<Class, Command> unauthCommands = new HashMap<Class, Command>();
    public static HashMap<Class, Command> computerCommands = new HashMap<Class, Command>();
    public static HashMap<Class, Command> userCommands = new HashMap<Class, Command>();
    /**
     * Maps computer userids to the point in time (in server time) at which the
     * computer went idle. If a computer doesn't have an entry here, then the
     * computer is not idle or the computer has since disconnected.
     */
    public static HashMap<Userid, Long> idleTimes = new HashMap<Userid, Long>();
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
     * All client-server connections that are active and that have both a
     * username and a computer.
     */
    public static HashMap<Userid, ServerConnection> connections = new HashMap<Userid, ServerConnection>();
    
    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5,
            100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));
    /**
     * A thread pool executor that has only one thread. This guarantees that
     * tasks will be executed strictly sequentially. Since there is only one
     * thread, tasks added to this queue should execute quickly to avoid holding
     * up everything else.
     */
    public static ThreadPoolExecutor stackedThreadPool = new ThreadPoolExecutor(
            1, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));
    
    /**
     * In the future, this will probably be split out into per-user variables.
     * All operations that read from or write to a user's roster file lock on
     * this first, so that none of them will overwrite each other.
     */
    public static final Object rosterLock = new Object();
    public static String serverName;
    public static Userid serverUserid;
    
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
        serverUserid = new Userid(serverName + "::");
        threadPool.allowCoreThreadTimeOut(true);
        scheduleIdleConnectionKiller();
        startGarbageCollectingThread();
        server = new ServerSocket(G4Defaults.CLIENT_SERVER_PORT);
        loadCommands();
        System.out.println("G4 Server on domain \"" + serverName
                + "\" is up and running.");
        runServer();
    }
    
    private static void startGarbageCollectingThread()
    {
        Thread t = new Thread("g4-garbage-collector")
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(30 * 1000);
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                    System.gc();
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }
    
    private static void runServer()
    {
        while (!server.isClosed())
        {
            try
            {
                Socket socket = server.accept();
                new ServerConnection(socket).start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private static void scheduleIdleConnectionKiller()
    {
        /*
         * Does nothing for now, but times out connections that are idle in the
         * future. Consider using SO_TIMEOUT or something else in the future.F
         */
    }
    
    private static void loadCommands()
    {
        String thisPackageName = G4Server.class.getPackage().getName();
        File commandsFolder = new File("classes/"
                + thisPackageName.replace(".", "/") + "/commands");
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
                command = (Command) Class.forName(thisPackageName + "." + name)
                        .newInstance();
            }
            catch (Exception e)
            {
                System.out.println("Exception while instantiating command "
                        + name + ":");
                e.printStackTrace();
                throw new RuntimeException(e.getClass().getName() + ": "
                        + e.getMessage(), e);
            }
            installCommand(command);
        }
    }
    
    private static void installCommand(Command command)
    {
        boolean isUnauth = command.getClass().isAnnotationPresent(
                UnauthCommand.class);
        boolean isUser = command.getClass().isAnnotationPresent(
                UserCommand.class);
        boolean isComputer = command.getClass().isAnnotationPresent(
                ComputerCommand.class);
        Method[] methods = command.getClass().getMethods();
        Class argumentType = null;
        for (Method method : methods)
        {
            if (method.getName().equals("process")
                    && method.getParameterTypes().length == 1
                    && Packet.class
                            .isAssignableFrom(method.getParameterTypes()[0]))
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
                    + " did not specify where it should be installed. "
                    + "This command will be silently ignored.");
    }
    
    /**
     * Gets the message folder for the computer specified. The userid specified
     * must be absolute and must contain a computer, and the folder returned
     * will always exist.
     * 
     * @param user
     *            The computer userid to look up
     * @return The message folder for the computer userid in question
     */
    public static File getMessageFolder(Userid user)
    {
        user = user.validateServer(serverName);
        if (!user.hasComputer())
            throw new RuntimeException();
        File folder = new File(messageFolder, "" + user.getUsername() + "/"
                + user.getComputer());
        folder.mkdirs();
        return folder;
    }
    
    /**
     * Lists the computers for the user specified. The userid can contain a
     * computer, but this will be ignored.
     * 
     * @param user
     * @return
     */
    public static Userid[] listComputers(Userid user)
    {
        user = user.validateServer(serverName);
        if (!user.hasUsername())
            throw new RuntimeException("No username");
        String username = user.getUsername();
        if (username.equals("_profile"))
            return new Userid[]
            {
                new Userid(user.getServer(), "_profile", "_computer")
            };
        File userFolder = new File(authFolder, username);
        File computersFolder = new File(userFolder, "computers");
        File[] files = computersFolder.listFiles();
        Userid[] userids = new Userid[files.length];
        for (int i = 0; i < files.length; i++)
        {
            userids[i] = new Userid(serverName, username, files[i].getName());
        }
        return userids;
    }
    
    /**
     * Returns true if the specified user exists. The user _profile returns true
     * from this method.
     * 
     * @param user
     * @return
     */
    public static boolean userExists(Userid user)
    {
        user = user.validateServer(serverName);
        if (user.getUsername().equals("_profile"))
            return true;
        return new File(authFolder, user.getUsername()).exists();
    }
    
    public static boolean computerExists(Userid computer)
    {
        computer = computer.validateServer(serverName);
        return new File(authFolder, computer.getUsername() + "/computers/"
                + computer.getComputer()).exists();
    }
    
    public static void verifyUserExists(Userid user)
    {
        if (!userExists(user))
            throw new RuntimeException("User " + user + " doesn't exist");
    }
    
    public static void verifyComputerExists(Userid user)
    {
        if (!computerExists(user))
            throw new RuntimeException("Computer " + user + " doesn't exist");
    }
    
    /**
     * Broadcasts a roster update to all users that have the specified user as a
     * contact. This is much the same as updateContainingPresence, but it
     * dynamically creates the roster packet for each user since each user's
     * roster will be different.<br/> <br/>
     * 
     * This method returns immediately, executing the actual update on the stack
     * thread pool. This executed runnable is rather slow, since it has a
     * maximum complexity of O(n<sup>2</sup>).
     * 
     * @param user
     *            The userid that has changed and hence requires all users with
     *            this userid on their contact list to receive a new roster
     */
    // TODO: review the actual algorithm, actual complexity might be O(n^3)
    // instead of O(n^2) like is documented (listing all users, listing each
    // user's contacts, listing each contact's computers)
    public static void updateContainingRosters(final Userid user)
    {
        /*
         * First, we'll loop over all users, searching out those that have this
         * user on their contact list.
         */
        final String useridString = toContactUseridString(user);
        stackedThreadPool.execute(new Runnable()
        {
            
            public void run()
            {
                File[] userFileList = authFolder.listFiles();
                for (File userFolder : userFileList)
                {
                    try
                    {
                        if (PropUtils.getProperty(
                                new File(userFolder, "roster"), useridString) != null)
                        {
                            /*
                             * This user has us on their contact list. We'll
                             * resend their roster now.
                             */
                            resendRosterSync(new Userid(serverName, userFolder
                                    .getName(), null), user);
                        }
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }
    
    /**
     * Synchronously (IE on the current thread) creates a new roster packet for
     * the user specified, which contains all of their current contacts and
     * information for those contacts, and sends it to all online computers for
     * that user.
     * 
     * @param userid
     *            The userid of the user whose roster we are updating
     * @param source
     */
    public static void resendRosterSync(Userid userid, Userid source)
    {
        sendToAnyOnlineComputers(userid, createRosterPacket(userid, false,
                source));
    }
    
    /**
     * Same as resendRosterSync, but executes on the stacked thread pool.
     * 
     * @param userid
     * @param source
     */
    public static void resendRoster(final Userid userid, final Userid source)
    {
        stackedThreadPool.execute(new Runnable()
        {
            
            public void run()
            {
                resendRosterSync(userid, source);
            }
        });
    }
    
    /**
     * Creates a roster packet that contains the roster of the specified user.
     * This roster packet can then be sent to the user.
     * 
     * @param user
     *            The user whose roster we are assembling
     * @param isInitial
     *            True if this roster packet should be marked as initial, false
     *            if it should not
     * @param source
     *            If isInitial is false, then this is the user that generated
     *            this roster packet update
     * @return A new roster packet, ready for sending to the user
     */
    public static RosterPacket createRosterPacket(Userid user,
            boolean isInitial, Userid source)
    {
        File userFolder = new File(authFolder, user.getUsername());
        File rosterFile = new File(userFolder, "roster");
        Properties rosterProps = PropUtils.getProperties(rosterFile);
        RosterPacket packet = new RosterPacket();
        packet.setInitial(isInitial);
        packet.setSource(source);
        String[] contactUsernameList = rosterProps.keySet().toArray(
                new String[0]);
        Contact[] contacts = new Contact[contactUsernameList.length];
        packet.setContacts(contacts);
        for (int i = 0; i < contacts.length; i++)
        {
            String propValue = rosterProps.getProperty(contactUsernameList[i]);
            Contact contact = new Contact();
            contacts[i] = contact;
            File contactUserFolder = new File(authFolder, new Userid(
                    contactUsernameList[i]).getUsername());
            contact.setExists(contactUserFolder.exists());
            contact.setUserid(new Userid(contactUsernameList[i])
                    .relativeTo(serverUserid));
            contact.setVisible(propValue.toLowerCase().startsWith("true"));
            String contactLocalName = propValue.contains(":") ? propValue
                    .split("\\:", 2)[1] : null;
            if (contactLocalName.trim().equals(""))
                contactLocalName = null;
            contact.setName(contactLocalName);
            if (contact.isExists())
            {
                if (new File(contactUserFolder, "realname").exists())
                    contact.setRealName(StringUtils.readFile(new File(
                            contactUserFolder, "realname")));
                if (contact.getRealName() != null
                        && contact.getRealName().trim().equals(""))
                    contact.setRealName(null);
                String[] contactComputerNames = new File(contactUserFolder,
                        "computers").list();
                Userid[] computerUserids = new Userid[contactComputerNames.length];
                contact.setComputers(computerUserids);
                for (int c = 0; c < computerUserids.length; c++)
                {
                    computerUserids[c] = new Userid(":"
                            + contactComputerNames[c]).relativeTo(contact
                            .getUserid());
                }
            }
            else
            {
                contact.setComputers(new Userid[0]);
            }
        }
        return packet;
    }
    
    /**
     * Finds all users that have this user as a contact and sends them this
     * presence packet. This is used to tell these users that the user just came
     * online, went offline, went idle, or came back from being idle.
     * 
     * @param user
     */
    public static void updateContainingPresence(Userid user,
            final PresencePacket packet)
    {
        /*
         * This is basically the user's userid without their computer but with
         * their server. So something like "trivergia.com::javawizard". This is
         * the format that contacts are stored in.
         */
        final String useridString = toContactUseridString(user);
        stackedThreadPool.execute(new Runnable()
        {
            
            public void run()
            {
                File[] userFileList = authFolder.listFiles();
                for (File userFolder : userFileList)
                {
                    try
                    {
                        if (PropUtils.getProperty(
                                new File(userFolder, "roster"), useridString) != null)
                        {
                            /*
                             * This user has us on their contact list. We'll
                             * scan for any of their computers and send this
                             * presence packet to them.
                             */
                            sendToAnyOnlineComputers(new Userid(serverName,
                                    userFolder.getName(), null), packet);
                        }
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }
    
    /**
     * Sends this packet to any of this user's online computers. This userid is
     * treated as if it were a username userid, regardless of whether or not it
     * is a computer userid.
     * 
     * @param userid
     *            The userid to send to, which should be on this server (but
     *            this is not checked)
     * @param packet
     *            The packet to send
     */
    public static void sendToAnyOnlineComputers(Userid userid, Packet packet)
    {
        for (Userid potential : new ArrayList<Userid>(connections.keySet()))
        {
            if (potential.getUsername().equals(userid.getUsername()))
            {
                /*
                 * Match! We'll get the connection and send the packet.
                 */
                ServerConnection connection = connections.get(potential);
                if (connection != null)// could be null if the user just
                // barely signed off
                {
                    try
                    {
                        connection.send(packet);
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
    
    public static String toContactUseridString(Userid user)
    {
        return user.withoutComputer().relativeTo(serverUserid).toString();
    }
    
    public static boolean rosterLineIsVisible(String line)
    {
        return line.startsWith("true");
    }
    
    public static String rosterLineContactLocal(String propValue)
    {
        String contactLocalName = propValue.contains(":") ? propValue.split(
                "\\:", 2)[1] : null;
        if (contactLocalName.trim().equals(""))
            contactLocalName = null;
        return contactLocalName;
    }
    
    public static String createRosterLine(boolean visible, String localName)
    {
        return "" + ("" + visible).toLowerCase()
                + (localName == null ? "" : ":" + localName);
    }
    
    /**
     * Sends a user message to the specified connection. A new message id will
     * be generated for the message.
     * 
     * @param connection
     *            The connection to send the message to
     * @param from
     *            The userid that the message should show up as coming from.
     *            This is usually the server's userid.
     * @param to
     *            The userid list that the message should show. When the user
     *            reads the message, they will see this list as the recipient
     *            list for the message. This doesn't cause the server to send
     *            the message to each of these recipients, though.
     * @param subject
     *            The subject of the message
     * @param body
     *            The body of the message
     * @param inReplyId
     *            The id of the message that this one is in reply to, or null if
     *            this one is not in reply
     * @param inReplySubject
     *            The subject of the message that this one is in reply to, or
     *            null if this one is not in reply
     */
    public static void sendUserMessage(ServerConnection connection,
            Userid from, Userid[] to, String subject, String body,
            String inReplyId, String inReplySubject)
    {
        Message message = new Message();
        MessageHeader header = new MessageHeader();
        header.setDate(System.currentTimeMillis());
        header.setInReplyMessageId(inReplyId);
        header.setInReplySubject(inReplySubject);
        header.setMessageId("" + from + "$" + System.currentTimeMillis() + "."
                + Math.random() + ".server.sendUserMessage");
        header.setRecipients(to);
        header.setSender(from);
        header.setSubject(subject);
        header.setBody(new ByteBlock(body));
        message.setHeader(header);
        message.setAttachments(new MessageAttachment[0]);
        InboundMessagePacket packet = new InboundMessagePacket();
        packet.setSender(from);
        packet.setMessageId(header.getMessageId());
        packet.setMessage(message);
        packet.setPacketThread(header.getMessageId());
        connection.send(packet);
    }
}
