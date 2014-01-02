package net.sf.opengroove.realmserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URLEncoder;
import java.security.KeyPair;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.opengroove.common.com.DatagramUtils;
import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.security.Hash;
import net.sf.opengroove.common.security.RSA;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.Userids;
import net.sf.opengroove.realmserver.data.model.Computer;
import net.sf.opengroove.realmserver.data.model.ComputerSetting;
import net.sf.opengroove.realmserver.data.model.Message;
import net.sf.opengroove.realmserver.data.model.MessageRecipient;
import net.sf.opengroove.realmserver.data.model.Subscription;
import net.sf.opengroove.realmserver.data.model.User;
import net.sf.opengroove.realmserver.data.model.UserSetting;
import net.sf.opengroove.realmserver.gwt.core.rcp.NotificationException;
import net.sf.opengroove.realmserver.web.LoginFilter;
import net.sf.opengroove.realmserver.web.rpc.AnonLinkImpl;
import net.sf.opengroove.realmserver.web.rpc.AuthLinkImpl;

import org.apache.jasper.servlet.JspServlet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class OpenGrooveRealmServer
{
    /**
     * A Runnable that sends the messageavailable command to all recipients of
     * this message on this realm server.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class MessageAvailableNotifier implements Runnable
    {
        private String messageId;
        
        public MessageAvailableNotifier(String messageId)
        {
            super();
            this.messageId = messageId;
        }
        
        public void run()
        {
            try
            {
                Message message = DataStore.getMessage(messageId);
                if (message == null)
                {
                    /*
                     * This won't usually happen. Typically, this would only
                     * happen if the message was sent but the executor queue was
                     * stopped up due to a long-running task, and all recipients
                     * read and deleted the message and the scheduled executor
                     * removed it from the database before this task got around
                     * to being executed. Either way, the message has already
                     * been read, so we'll just return.
                     */
                    return;
                }
                MessageRecipient[] recipients = DataStore.listMessageRecipients(messageId);
                for (MessageRecipient recipient : recipients)
                {
                    try
                    {
                        String userid = recipient.getRecipient();
                        String computer = recipient.getComputer();
                        if (!Userids.toRealm(userid).equalsIgnoreCase(serverRealmAddress))
                        {
                            continue;
                        }
                        if (computer == null || computer.equals(""))
                        {
                            continue;
                        }
                        /*
                         * The computer is not null and the user is of this
                         * realm. Now we'll see if this user is online.
                         */
                        ConnectionHandler connection =
                            getConnectionForComputer(Userids.toUsername(userid), computer);
                        if (connection != null)
                        {
                            /*
                             * The user is online, so we'll send them a
                             * messageavailable message.
                             */
                            connection.sendEncryptedPacket(generateId(), "messageavailable",
                                Status.OK, messageId);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * A Runnable that schedules the specified message for sending to the
     * inter-realm servers of all of it's recipients that are of a different
     * realm.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class InterRealmMessageSender implements Runnable
    {
        private String messageId;
        
        public InterRealmMessageSender(String messageId)
        {
            super();
            this.messageId = messageId;
        }
        
        public void run()
        {
            /*
             * FIXME: actually implement this runnable
             */
        }
        
    }
    
    public static ArrayList<String> messageDeletionCanidates = new ArrayList<String>();
    
    /**
     * A runnable that is added to internalTasks upon OpenGroove Realm Server
     * startup. It is built such that it has to run twice after a message
     * becomes available for deletion for that message to be deleted. Every time
     * it runs, it first checks all messages in it's canidate list (I'll explain
     * that in a sec), and, if they have no backing db message, deletes the
     * filesystem file. Then, it scans through all files and adds those that
     * don't have a backing db message to it's canidate list. Essentially, then,
     * when a message is deleted, the first execution of this runnable adds it
     * to the canidate list, and the second execution deletes it. The reason for
     * this double-pass is so a file won't be deleted for a message that is
     * about to be created.<br/><br/>
     * 
     * It is imperative that multiple instances of this runnable never exist at
     * the same time.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class MessageDataRemover implements Runnable
    {
        
        public void run()
        {
            for (String messageId : messageDeletionCanidates)
            {
                try
                {
                    File messageFile =
                        new File(messageDataFolder, Message.getFileId(messageId));
                    if (!messageFile.exists())
                        continue;
                    Message message = DataStore.getMessage(messageId);
                    if (message != null)
                        continue;
                    /*
                     * The file exists but the message doesn't, so we'll delete
                     * the file.
                     */
                    messageFile.delete();
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
            messageDeletionCanidates.clear();
            /*
             * Now we need to iterate over all message files and add as
             * canidates those without a backing message.
             */
            File[] messageFiles = messageDataFolder.listFiles();
            for (File messageFile : messageFiles)
            {
                try
                {
                    if (messageFile.isDirectory())
                        /*
                         * Probably some folder put in by subversion
                         */
                        continue;
                    Message message =
                        DataStore.getMessage(Message.getMessageId(messageFile.getName()));
                    if (message == null)
                        messageDeletionCanidates.add(Message.getMessageId(messageFile
                            .getName()));
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }
    
    /**
     * A task that removes messages without any recipients.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class ReadMessageRemover implements Runnable
    {
        
        public void run()
        {
            try
            {
                Message[] readMessages = DataStore.listMessagesWithoutRecipients();
                for (Message message : readMessages)
                {
                    DataStore.deleteMessage(message.getId());
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
            try
            {
                MessageRecipient[] orphanRecipients =
                    DataStore.listOrphanMessageRecipients();
                for (MessageRecipient recipient : orphanRecipients)
                {
                    DataStore.deleteMessageRecipient(recipient);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static final long MIN_FREE_DISK_SPACE = 500 * 1000 * 1000;
    
    public interface ToString<S>
    {
        public String toString(S object);
    }
    
    // TODO: consider using SRV records for realm server (or main server)
    // lookups. This has two benefits. First, a user's domain need not run an
    // OpenGroove server on the domain's server itself. This would allow users
    // of the realm opengroove.org, which would have the SRV record
    // "_opengroove._tcp.opengroove.org 86400 IN SRV 10 10 63745 trivergia.com"
    // to send connections to that server and messages to that domain over to a
    // server running on trivergia. The second advantage would be that if a user
    // of a particular realm server would frequently be in a location that
    // doesn't allow connections to anything besides port eighty, there could be
    // another SRV record, this one looking like
    // "_opengroove._tcp.opengroove.org 86400 IN SRV 20 10 80
    // svn.trivergia.com", so that a connection to trivergia.com would be
    // attempted by default, and, failing that, a connection to
    // svn.trivergia.com would be attempted. Some sort of load balancing should
    // be made possible, so that if servers with the lowest priority and weight
    // are too busy servicing other users, they can reject the connection in
    // such a way that servers with a higher weight could be tried.
    //
    // TODO: Ok, I just read up on what SRV weights are intended for. Instead of
    // trying the server with the lowest weight, it generates a random number,
    // and from that chooses (based on weights) which server to connect to. This
    // means that if a particular server has a weight of 10, and another server
    // has a weight of 20, the server with a weigh of 20 will get approximately
    // 2/3 of requests, and the server with a weight of 10 will get
    // approximately 1/3 of requests.
    /**
     * This class is a Runnable designed to be added to the task thread queue.
     * It sends notifications to all users that have subscriptions for this
     * user's status (or this computer's status), indicating that the user's (or
     * computer's) status has changed.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class UserStatusNotifier implements Runnable
    {
        private String username;
        private String computer;
        
        public UserStatusNotifier(String username, String computer)
        {
            this.username = username;
            this.computer = computer;
        }
        
        @Override
        public void run()
        {
            try
            {
                Subscription[] subscriptions =
                    DataStore.listSubscriptionsByTargetUser(this.username);
                // TODO: change this to two calls of
                // listSubscriptionsByTypedTargetUser, as this is more effecient
                // because it doesn't list subscriptions to the user's
                // properties as
                // well
                for (Subscription subscription : subscriptions)
                {
                    String sUser = subscription.getOnusername();
                    String sComputer = subscription.getOncomputername();
                    if ((subscription.getType().equalsIgnoreCase("userstatus") && sUser
                        .equals(username))
                        || (subscription.getType().equalsIgnoreCase("computerstatus")
                            && computer.equals(sComputer) && sUser.equals(username)))
                    {
                        // the subscription is for this user, so let's check to
                        // see if the subscription's creator is online
                        Map<String, ConnectionHandler> userMap =
                            connectionsByAuth.get(subscription.getUsername());
                        if (userMap != null)
                        {
                            for (ConnectionHandler handler : userMap.values())
                            {
                                if (handler.isAlive())
                                {
                                    handler.sendEncryptedPacket(generateId(),
                                        "subscriptionevent", Status.OK, subscription
                                            .getType()
                                            + "\n"
                                            + subscription.getOnusername()
                                            + "\n"
                                            + subscription.getOncomputername()
                                            + "\n"
                                            + subscription.getOnsettingname()
                                            + "\n"
                                            + subscription.isDeletewithtarget());
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * A runnable that can be run (usually on the
     * {@link OpenGrooveRealmServer#tasks} executor) to notify users that have
     * subscribed to updates for a specific property about the changes to that
     * property. This currently only notifies users that have subscribed
     * locally; in the future, it will notify other realm servers that have
     * subscribed. They will receive the subscription events and distribute them
     * to any of their users that have subscribed.
     * 
     * If a user has subscribed to updates for computer settings for a
     * particular user, but they have not specified a computer to subscribe to
     * (IE the computer name is the empty string), then they will receive
     * notifications if any of the computers change that setting.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class UserSettingNotifier implements Runnable
    {
        private String username;
        private String computer;
        private String setting;
        
        public UserSettingNotifier(String username, String computer, String setting)
        {
            this.username = username;
            this.computer = computer;
            this.setting = setting;
            if (computer != null && computer.equals(""))
                this.computer = null;
        }
        
        @Override
        public void run()
        {
            if (!setting.startsWith("public-"))
            {
                // don't allow subscriptions to non-public settings, if a user
                // needs to be notified of changes to one of it's computer's
                // settings it should tell the computer that and have the
                // computer send it an imessage whenever it changes a setting
                //
                // TODO: rethink the above, now that I think about it, it seems
                // like a user should be able to subscribe to their own private
                // properties, as well as private properties of any of their
                // computers.
                System.out.println("non-public subscription attempted. Returning.");
                return;
            }
            try
            {
                Subscription[] subscriptions =
                    DataStore.listSubscriptionsByTargetUser(this.username);
                // TODO: change this to two calls of
                // listSubscriptionsByTypedTargetUser, as this is more effecient
                // because it doesn't list subscriptions to the user's status as
                // well
                for (Subscription subscription : subscriptions)
                {
                    String sUser = subscription.getOnusername();
                    String sComputer = subscription.getOncomputername();
                    String sSetting = subscription.getOnsettingname();
                    boolean isUserSetting = computer == null;
                    boolean isComputerSetting = !isUserSetting;
                    boolean isSubscriberUserSetting =
                        subscription.getType().equalsIgnoreCase("usersetting");
                    boolean isSubscriberComputerSetting =
                        subscription.getType().equalsIgnoreCase("computersetting");
                    System.out.println("targeting "
                        + (isUserSetting ? "usersetting" : "computersetting")
                        + " subscriptions");
                    if (isSubscriberComputerSetting)
                    {
                        System.out.println("computersetting subscription found");
                    }
                    else if (isSubscriberUserSetting)
                    {
                        System.out.println("usersetting subscription found");
                    }
                    
                    // FIXME: clean up the rat's-nest boolean arithmetic that
                    // follows
                    boolean acceptOnUser =
                        (isUserSetting && isSubscriberUserSetting && subscription
                            .getOnsettingname().equalsIgnoreCase(setting));
                    boolean isCorrectComputer =
                        (subscription.getOncomputername().equalsIgnoreCase(computer) || subscription
                            .getOncomputername().trim().equals(""));
                    boolean acceptOnComputer =
                        (isComputerSetting && isSubscriberComputerSetting
                            && isCorrectComputer && subscription.getOnsettingname()
                            .equalsIgnoreCase(setting));
                    if (acceptOnUser)
                        System.out.println("accepting on user");
                    else if (acceptOnComputer)
                        System.out.println("accepting on computer");
                    if (acceptOnUser || acceptOnComputer)
                    {
                        // the subscription is for this setting, so send a
                        // subscription event to the user
                        Map<String, ConnectionHandler> userMap =
                            connectionsByAuth.get(subscription.getUsername());
                        if (userMap != null)
                        {
                            for (ConnectionHandler handler : userMap.values())
                            {
                                if (handler.isAlive())
                                {
                                    System.out.println("notifying subscriber");
                                    handler.sendEncryptedPacket(generateId(),
                                        "subscriptionevent", Status.OK, subscription
                                            .getType()
                                            + "\n"
                                            + subscription.getOnusername()
                                            + "\n"
                                            + subscription.getOncomputername()
                                            + "\n"
                                            + subscription.getOnsettingname()
                                            + "\n"
                                            + subscription.isDeletewithtarget());
                                }
                            }
                        }
                    }
                }
            }
            /*
             * TODO: WAY too many close-braces in a row. Figure out how to
             * simplify this.
             */
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static enum Status
    {
        /**
         * Indicates that a command succeeded.
         */
        OK,
        /**
         * indicates that a command failed for some unknown reason.
         */
        FAIL,
        /**
         * Indicates that the user provided an incorrect username/password.
         */
        BADAUTH,
        /**
         * Indicates that the user provided an incorrect computer name when
         * trying to authenticate
         */
        BADCOMPUTER,
        /**
         * Indicates that performing a command would exceed one of the user's
         * quotas
         */
        QUOTAEXCEEDED,
        /**
         * Indicates that the computer specified in the command does not exist
         */
        NOSUCHCOMPUTER,
        /**
         * Indicates that the userid/username provided in the command does not
         * exist
         */
        NOSUCHUSER,
        /**
         * Indicates that the command succeeded but has no results to return
         */
        NORESULTS,
        /**
         * Indicates that a resource or entity which was to be created already
         * exists
         */
        ALREADYEXISTS,
        /**
         * Indicates that the subscription provided in the command does not
         * exist
         */
        NOSUCHSUBSCRIPTION,
        /**
         * Indicates that the connected user is not authorized to perform this
         * command at this time. Most commands require that a user be
         * authenticated as a computer to perform. Calling a command when
         * authenticated as a user but not as a computer is a common cause of
         * getting this error. See
         * www.opengroove.org/dev/protocol/commands/client---realm-server for a
         * list of commands and what authentication is required to execute them
         */
        UNAUTHORIZED,
        /**
         * Indicates that the realm of a userid provided is invalid
         */
        INVALIDREALM,
        /**
         * Indicates that the message id provided in the command was invalid.
         * This will occur if the user attempts to create a message with an id
         * that does not start with their username followed by a hyphen.
         */
        INVALIDMESSAGEID,
        /**
         * Indicates that there is no message with the id provided in the
         * command
         */
        NOSUCHMESSAGE,
        /**
         * Indicates that a data range specified is out of range. For example,
         * if a message is 5 bytes long and bytes 1 through 6 are read, then
         * this error will occur, since there is no 6th byte.
         */
        INDEXOUTOFBOUNDS, AUTHUNAUTHORIZED
    };
    
    public static final SecureRandom random = new SecureRandom();
    
    public static abstract class Command
    {
        /**
         * The maximum size that a packet is allowed to be when arriving for
         * this command. There is a hard packet size limit of 65535 bytes, so
         * this must not be larger than that.
         */
        private int mps;
        private boolean whenUnauth;
        private boolean whenNoComputer;
        private String commandName;
        
        public Command(String commandName, int maxPacketSize, boolean whenUnauth,
            boolean whenNoComputer)
        {
            this.mps = maxPacketSize;
            this.whenUnauth = whenUnauth;
            this.whenNoComputer = whenNoComputer;
            this.commandName = commandName;
            commands.put(commandName.toLowerCase(), this);
        }
        
        protected String command()
        {
            return commandName.toLowerCase();
        }
        
        public boolean whenUnauth()
        {
            return whenUnauth;
        }
        
        public boolean whenNoComputer()
        {
            return whenNoComputer;
        }
        
        public int maxPacketSize()
        {
            return this.mps;
        }
        
        public abstract void handle(String packetId, InputStream data,
            ConnectionHandler connection) throws Exception;
    }
    
    private static Map<String, Map<String, ConnectionHandler>> connectionsByAuth =
        new Hashtable<String, Map<String, ConnectionHandler>>();
    /**
     * A map that holds packets waiting to be delivered to other realm servers.
     * Since not every known realm server will have a connection to it non-stop,
     * there will frequently be items in the queues of this map for servers that
     * this server is not connected to. For each item in the map, the key is the
     * name of the realm of the realm server, and the value is the queue that
     * holds the realm's pending items.
     */
    private static Map<String, BlockingQueue<InterServerPacket>> interRealmCache =
        new HashMap<String, BlockingQueue<InterServerPacket>>();
    /**
     * @deprecated Since switching to Jetty from NanoHTTPD (when I decided that
     *             I was going to need a full-fledged servlet container instead
     *             of a class that I could just extend), the folder that stores
     *             web resources has changed.
     */
    @Deprecated()
    protected static final File HTTPD_RES_FOLDER = new File("httpdres");
    /**
     * The connection to the persistant database
     */
    public static Connection pdb;
    /**
     * The connection to the large database
     */
    public static Connection ldb;
    public static SqlMapClient pdbclient;
    public static SqlMapClient ldbclient;
    /**
     * The prefix string for tables in the persistant database
     */
    public static String pfix;
    /**
     * The prefix string for tables in the large database
     */
    public static String lfix;
    
    public static X509Certificate[] serverCertificateChain;
    
    public static PrivateKey serverCertificatePrivateKey;
    
    private static final File configFile = new File("config.properties");
    /*
     * TODO: make this configurable when the server's administrator is setting
     * the server up
     */
    private static File messageDataFolder = new File("messagedata");
    
    private static boolean setupStillRunning = true;
    
    private static boolean setupStillAllowed = true;
    
    private static Properties config = new Properties();
    
    protected static boolean doneSettingUp = false;
    public static SSLServerSocket serverSocket;
    private static ArrayList<ConnectionHandler> connections =
        new ArrayList<ConnectionHandler>();
    public static BigInteger rsaEncryptionPublicKey;
    public static BigInteger rsaEncryptionModulus;
    public static BigInteger rsaEncryptionPrivateKey;
    public static BigInteger rsaSignaturePublicKey;
    public static BigInteger rsaSignatureModulus;
    public static BigInteger rsaSignaturePrivateKey;
    
    public static int getNumConnections()
    {
        return connections.size();
    }
    
    public static int getNumAuthConnections()
    {
        int i = 0;
        for (ConnectionHandler c : new ArrayList<ConnectionHandler>(connections))
        {
            if (c.username != null)
                i++;
        }
        return i;
    }
    
    public static int getNumComputerAuthConnections()
    {
        int i = 0;
        for (ConnectionHandler c : new ArrayList<ConnectionHandler>(connections))
        {
            if (c.username != null && c.computerName != null)
                i++;
        }
        return i;
    }
    
    public static final ScheduledThreadPoolExecutor internalTasks =
        new ScheduledThreadPoolExecutor(15);
    
    public static final ThreadPoolExecutor tasks =
        new ThreadPoolExecutor(20, 400, 20, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10000));
    
    protected static final byte[] EMPTY = new byte[0];
    
    private static final int MAX_CONNECTIONS = 500;
    protected static final int MAX_MESSAGE_SIZE = 2 * 1000 * 1000 * 1000;
    
    public static class TimedInputStream extends FilterInputStream
    {
        private long lastTime;
        
        protected TimedInputStream(InputStream in)
        {
            super(in);
            lastTime = System.currentTimeMillis();
        }
        
        @Override
        public int read() throws IOException
        {
            // TODO Auto-generated method stub
            int i = super.read();
            lastTime = System.currentTimeMillis();
            return i;
        }
        
        @Override
        public int read(byte[] b, int off, int len) throws IOException
        {
            int i = super.read(b, off, len);
            lastTime = System.currentTimeMillis();
            return i;
        }
        
        @Override
        public int read(byte[] b) throws IOException
        {
            // TODO Auto-generated method stub
            int i = super.read(b);
            lastTime = System.currentTimeMillis();
            return i;
        }
        
        public long getLastTime()
        {
            return lastTime;
        }
        
        public void resetTime()
        {
            lastTime = System.currentTimeMillis();
        }
        
    }
    
    public static class TimedOutputStream extends FilterOutputStream
    {
        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            // TODO Auto-generated method stub
            super.write(b, off, len);
            lastTime = System.currentTimeMillis();
        }
        
        @Override
        public void write(byte[] b) throws IOException
        {
            // TODO Auto-generated method stub
            super.write(b);
            lastTime = System.currentTimeMillis();
        }
        
        @Override
        public void write(int b) throws IOException
        {
            // TODO Auto-generated method stub
            super.write(b);
            lastTime = System.currentTimeMillis();
        }
        
        protected TimedOutputStream(OutputStream out)
        {
            super(out);
            lastTime = System.currentTimeMillis();
        }
        
        private long lastTime;
        
        public long getLastTime()
        {
            return lastTime;
        }
        
        public void resetTime()
        {
            lastTime = System.currentTimeMillis();
        }
        
    }
    
    public static HashMap<String, Command> commands = new HashMap<String, Command>();
    
    private static String serverRealmAddress;
    
    public static class ConnectionHandler extends Thread
    {
        private SSLSocket socket;
        private InputStream internalInputStream;
        private TimedInputStream in;
        private OutputStream internalOutputStream;
        private TimedOutputStream out;
        private PacketSpooler spooler;
        public int allowedIdleMilliseconds = 10000;
        public String username;
        public String computerName;
        
        /**
         * Returns a userid composed of this server's realm name and this
         * connection's username.
         * 
         * @return
         */
        public String getUserid()
        {
            return Userids.toUserid(serverRealmAddress, username);
        }
        
        public long getLastInTime()
        {
            return in.getLastTime();
        }
        
        public long getLastOutTime()
        {
            return out.getLastTime();
        }
        
        public ConnectionHandler(SSLSocket socket) throws IOException
        {
            this.socket = socket;
            internalInputStream = socket.getInputStream();
            in = new TimedInputStream(internalInputStream);
            internalOutputStream = socket.getOutputStream();
            out = new TimedOutputStream(internalOutputStream);
        }
        
        /**
         * sends a packet to this connection's client. If a security handshake
         * has not occured yet, an IllegalStateException is thrown.
         * 
         * @throws IOException
         */
        public boolean sendPacketTo(Packet packet) throws IOException
        {
            return spooler.send(packet);
        }
        
        public boolean sendEncryptedPacket(String id, String command, Status status,
            String message)
        {
            return sendEncryptedPacket(id, command, status, message.getBytes());
        }
        
        public boolean sendEncryptedPacket(String id, String command, Status status,
            byte[] message)
        {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(id.getBytes());
                baos.write(' ');
                baos.write(command.getBytes());
                baos.write(' ');
                baos.write(status.toString().getBytes());
                baos.write(' ');
                baos.write(message);
                return sendEncryptedPacket(baos.toByteArray());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        public boolean sendEncryptedPacket(byte[] packet)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try
            {
                DatagramUtils.write(packet, baos);
                Packet encPacket = new Packet(new ByteArrayInputStream(baos.toByteArray()));
                encPacket
                    .setDescription(new String(packet, 0, Math.min(packet.length, 128)));
                return sendPacketTo(encPacket);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        
        public void run()
        {
            try
            {
                spooler = new PacketSpooler(out, 100);
                spooler.start();
                allowedIdleMilliseconds = 10000;
                in.resetTime();
                out.resetTime();
                // Ok, we've set up the connection. Now it's time to do the
                // handshake.
                String s = "";
                for (int i = 0; i < 200; i++)
                {
                    int read = in.read();
                    s += (char) read;
                    if ((read == '\r' || read == '\n') && i != 0)
                        break;
                    if (i == 29)
                        throw new ProtocolMismatchException(
                            "too much initialization data sent by the client");
                }
                s = s.trim();
                if (!s.toLowerCase().startsWith("OpenGroove".toLowerCase()))
                /*
                 * startsWith instead of equals to allow for additional data to
                 * be passed in the future. My idea is to add the ability in the
                 * future to have one server serve different realms. The main
                 * difficulty with this would be handling the SSL stuff, since
                 * the target domain wouldn't be known until after the
                 * certificate had been negotiated, so this may end up unused.
                 */
                {
                    // Incorrect connection, probably an HTTP client or
                    // something
                    throw new ProtocolMismatchException(
                        "Invalid header string sent by client, it "
                            + "should be \"OpenGroove\" with a newline after");
                }
                // Ok, correct header, now we send back our header
                out.write("OpenGrooveServer\r\n".getBytes());
                out.flush();
                // Now the client should send us the letter 'c' but NOT followed
                // by a newline. This is used to sync up stream positions, since
                // we're not sure whether the client ends packets with \r, \n,
                // or \r\n.
                for (int i = 0; i < 5; i++)
                {
                    if (in.read() == 'c')
                        break;
                    if (i == 4)
                        throw new ProtocolMismatchException(
                            "no terminating 'c' at end of handshake");
                }
                // Ok, the client's stream of data to us is in sync. Now we send
                // the letter 'c' to the client so that the client can
                // synchronize our data stream.
                out.write('c');
                // The handshake is now complete. Now we start listening for
                // packets and deal with them accordingly.
                // We keep attempting to receive packets until an exception is
                // thrown. (when the quit command is called, the command class
                // closes the connection, so an exception will be thrown on the
                // next read)
                while ((!socket.isClosed()) && (!socket.isInputShutdown())
                    && (!socket.isOutputShutdown()))
                {
                    byte[] packet = DatagramUtils.read(in, 65535);
                    processIncomingPacket(packet);
                }
                //
                // TODO: PICK UP HERE JULY 10, 2008
                // consider using a hash map to map authenticated users and
                // computers to their connections
                // have a scheduled thread that watchdogs all connections, and
                // one that removes dead connections from the username and
                // computername hash maps
            }
            catch (Exception e)
            {
                System.err.println("Connection handler closing due to exception, "
                    + "stack trace follows");
                e.printStackTrace();
            }
            finally
            {
                connections.remove(this);
                try
                {
                    if (username != null && computerName != null)
                    {
                        Map userMap = connectionsByAuth.get(username);
                        if (userMap != null)
                        {
                            userMap.remove(computerName);
                            if (userMap.size() == 0)
                                connectionsByAuth.remove(username);
                        }
                        tasks.execute(new UserStatusNotifier(this.username,
                            this.computerName));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                try
                {
                    spooler.close();
                    socket.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        public void processIncomingPacket(byte[] packet) throws Exception
        {
            System.out.println("dealing with packet of length " + packet.length);
            String packetId = "UNKNOWN";
            String commandName = "UNKNOWN";
            String first128 = "UNKNOWN";
            try
            {
                byte[] first128bytes = new byte[Math.min(128, packet.length)];
                System.arraycopy(packet, 0, first128bytes, 0, first128bytes.length);
                first128 = new String(first128bytes);
                String[] first128split = first128.split("\\ ", 3);
                if (first128split.length < 3)
                    throw new FailedResponseException(Status.FAIL,
                        "no command input (packets should be of"
                            + " the form packetId commandName arguments)");
                packetId = first128split[0];
                commandName = first128split[1];
                int startDataIndex = packetId.length() + commandName.length() + 2;
                ByteArrayInputStream data =
                    new ByteArrayInputStream(packet, startDataIndex, packet.length
                        - (startDataIndex));
                // In the future, the packet could be cached to the file system
                // if
                // it's larger than, say, 2048 bytes, to avoid memory errors.
                // Since the resultant info is passed into the command via an
                // input stream, this wouldn't be too hard to do.
                Command command = commands.get(commandName.toLowerCase());
                if (command == null)
                    throw new FailedResponseException(
                        "The command specified is not a valid command");
                if (username == null && !command.whenUnauth())
                    throw new FailedResponseException(Status.AUTHUNAUTHORIZED,
                        "You must run the authenticate command before this one.");
                if (computerName == null && !command.whenNoComputer())
                    throw new FailedResponseException(Status.UNAUTHORIZED,
                        "This command can only be run when authenticated as a computer.");
                command.handle(packetId, data, this);
            }
            catch (FailedResponseException e)
            {
                System.out.println("Failing response for user " + username + " with code "
                    + e.getStatus() + " and message " + e.getMessage()
                    + " in reply to command " + commandName);
                sendEncryptedPacket(
                    packetId,
                    commandName,
                    e.getStatus(),
                    e.getMessage() == null ? "An error occured while processing this command."
                        .getBytes()
                        : e.getMessage().getBytes());
            }
            catch (Exception e)
            {
                System.out
                    .println("Connection-fatal exception on packet with first128 equal to "
                        + first128);
                throw e;
            }
        }
    }
    
    /**
     * A class that maintains a queue of Packet objects, and streams them to the
     * output stream specified as the output stream allows.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class PacketSpooler extends Thread
    {
        private OutputStream out;
        private BlockingQueue<Packet> queue;
        
        public PacketSpooler(OutputStream out, int queueSize)
        {
            this.out = out;
            this.queue = new LinkedBlockingQueue<Packet>(queueSize);
        }
        
        public synchronized boolean send(Packet packet)
        {
            boolean canOffer = queue.offer(packet);
            return canOffer;
        }
        
        private boolean closed = false;
        private final Object sendLock = new Object();
        
        public void close() throws IOException
        {
            closed = true;
            out.close();
        }
        
        public void run()
        {
            try
            {
                while (true)
                {
                    Packet packet = queue.take();
                    synchronized (sendLock)
                    {
                        copy(packet.getStream(), out);
                        out.flush();
                    }
                }
            }
            catch (Exception e)
            {
                if (closed)
                {
                    System.out.println("Closed packet spooler with exception");
                }
                else
                {
                    e.printStackTrace();
                    System.out
                        .println("Closed packet spooler with the above abnormal exception");
                }
            }
        }
        
    }
    
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        Thread gcThread = new Thread("periodic-gc")
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(60 * 1000);
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                    System.gc();
                }
            }
        };
        gcThread.setDaemon(true);
        gcThread.start();
        System.out.println("OpenGroove Realm Server");
        System.out.println("www.opengroove.org");
        System.out.println("Initializing...");
        if (!messageDataFolder.exists())
            messageDataFolder.mkdirs();
        ogcaCert = CertificateUtils.readCert(StringUtils.readFile(new File("cacert.pem")));
        /*
         * Configuration that needs to be gathered before we can get up and
         * running:
         * 
         * Database type (use internal or connect to external), coming later
         * 
         * Database url, username, and password if external, see above, coming
         * later
         * 
         * web admin username and password
         * 
         * whether or not to sign up with the management server
         * 
         * if management server sign-up requested, a short name for this realm,
         * a long name for this realm, a description for this realm, whether or
         * not to make publicly available this realm's description, whether or
         * not to make publicly available this realm's list of users,
         */
        if (!configFile.exists())
        {
            // OpenGroove Realm Server hasn't been initialized for the first
            // time yet, so all we need to do is start a web server listening
            // for connections, and provide the user with a captcha to validate
            // that they can init the server and then get information from them
            // such as their requested realm server name, web admin username and
            // password, etc
            // Properties props = new Properties();
            // props.setProperty("cap.border", "yes");
            // props.setProperty("cap.border.c", "black");
            // props.setProperty("cap.char.arr.l", "8");
            // DefaultCaptchaIml cap = new DefaultCaptchaIml(
            // props);
            // final String text = cap.createText();
            // cap.createImage(new FileOutputStream(new File(
            // HTTPD_RES_FOLDER, "setup/captcha.png")),
            // text);
            // FIXME: A new captcha should probably be generated every few
            // minutes, so that
            // the user only has one guess at a captcha
            Server server = new Server(34567);
            final Context context = createServerContext(server, "webinit");
            context.addFilter(new FilterHolder(new Filter()
            {
                
                @Override
                public void destroy()
                {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public synchronized void doFilter(ServletRequest sRequest,
                    ServletResponse sResponse, FilterChain chain) throws IOException,
                    ServletException
                {
                    HttpServletRequest request = (HttpServletRequest) sRequest;
                    HttpServletResponse response = (HttpServletResponse) sResponse;
                    if (request.getRequestURI().startsWith("/bypass/"))
                    {
                        chain.doFilter(request, response);
                        return;
                    }
                    else if (doneSettingUp)
                    {
                        response.sendRedirect("/bypass/done.jsp");
                        return;
                    }
                    else if (request.getRequestURI().equals("/setup"))
                    {
                        // build url containing all of the parameters in case
                        // the user mis-entered something
                        String redoUrl = "/bypass/start.jsp?";
                        for (String param : (Collection<String>) Collections.list(request
                            .getParameterNames()))
                        {
                            for (String value : request.getParameterValues(param))
                            {
                                redoUrl +=
                                    "" + URLEncoder.encode(param) + "="
                                        + URLEncoder.encode(value) + "&";
                            }
                        }
                        redoUrl += "errormessage=";
                        // load parameters into variables
                        String username = request.getParameter("username");
                        String password = request.getParameter("password");
                        String passwordagain = request.getParameter("passwordagain");
                        String pdbclass = request.getParameter("pdbclass");
                        config.setProperty("pdbclass", pdbclass);
                        String pdburl = request.getParameter("pdburl");
                        config.setProperty("pdburl", pdburl);
                        String pdbprefix = request.getParameter("pdbprefix");
                        config.setProperty("pdbprefix", pdbprefix);
                        String pdbusername = request.getParameter("pdbusername");
                        config.setProperty("pdbusername", pdbusername);
                        String pdbpassword = request.getParameter("pdbpassword");
                        config.setProperty("pdbpassword", pdbpassword);
                        String ldbclass = request.getParameter("ldbclass");
                        config.setProperty("ldbclass", ldbclass);
                        String ldburl = request.getParameter("ldburl");
                        config.setProperty("ldburl", ldburl);
                        String ldbprefix = request.getParameter("ldbprefix");
                        config.setProperty("ldbprefix", ldbprefix);
                        String ldbusername = request.getParameter("ldbusername");
                        config.setProperty("ldbusername", ldbusername);
                        String ldbpassword = request.getParameter("ldbpassword");
                        config.setProperty("ldbpassword", ldbpassword);
                        String serverport = request.getParameter("serverport");
                        String webport = request.getParameter("webport");
                        context.setAttribute("serverport", webport);
                        String serverhostname = request.getParameter("serverhostname");
                        boolean forceEncryption =
                            request.getParameter("forceencryption") != null;
                        // template error message:
                        //
                        // setuperror(redoUrl, response, "");
                        // return;
                        //
                        // check to see if the passwords match
                        if (!password.equals(passwordagain))
                        {
                            setuperror(redoUrl, response,
                                "The passwords you entered didn't match.");
                            return;
                        }
                        // make sure that password is at least 5 characters long
                        if (password.length() < 5)
                        {
                            setuperror(redoUrl, response,
                                "The password you entered for your "
                                    + "web administration password "
                                    + "isn't long enough. The password needs to be "
                                    + "at least 5 characters long.");
                            return;
                        }
                        if (username.length() < 1)
                        {
                            setuperror(redoUrl, response,
                                "The username you entered for your"
                                    + "web administration username "
                                    + "isn't long enough. The username needs to be"
                                    + " at least 1 character long.");
                            return;
                        }
                        // create connections to the persistant and large
                        // databases, and test them out
                        System.out.println("connecting to persistant database...");
                        pfix = pdbprefix;
                        lfix = ldbprefix;
                        try
                        {
                            Class.forName(pdbclass);
                            pdb =
                                DriverManager
                                    .getConnection(pdburl, pdbusername, pdbpassword);
                        }
                        catch (Exception e)
                        {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured when trying to initialize"
                                    + " the persistant database. Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString() + "</pre>");
                            return;
                        }
                        System.out.println("connecting to large database...");
                        try
                        {
                            Class.forName(ldbclass);
                            ldb =
                                DriverManager
                                    .getConnection(ldburl, ldbusername, ldbpassword);
                        }
                        catch (Exception e)
                        {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured when trying to initialize"
                                    + " the persistant database. Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString() + "</pre>");
                            return;
                        }
                        System.out.println("loading sql files for table creation...");
                        // create the tables
                        String psql = readFile(new File("pinit.sql"));
                        String lsql = readFile(new File("linit.sql"));
                        psql = psql.replace("$$prefix$$", pfix);
                        lsql = lsql.replace("$$prefix$$", lfix);
                        System.out.println("creating persistant tables...");
                        try
                        {
                            runLongSql(psql, pdb);
                        }
                        catch (SQLException e)
                        {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            setuperror(redoUrl, response,
                                "An error occured when trying to initialize"
                                    + " the persistant database. Some data may have"
                                    + "already been inserted into the database. "
                                    + "Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString() + "</pre>");
                            return;
                        }
                        // TODO: what if it fails in the middle of creating
                        // tables? should we try to roll back and delete those
                        // tables? perhaps put the table creates all within one
                        // transaction?
                        System.out.println("creating large tables...");
                        try
                        {
                            runLongSql(lsql, ldb);
                        }
                        catch (SQLException e)
                        {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            setuperror(redoUrl, response,
                                "An error occured when trying to initialize"
                                    + " the large database. Some data may have"
                                    + "already been inserted into the database. "
                                    + "Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString() + "</pre>");
                            return;
                        }
                        // store the configuration settings in the tables
                        System.out.println("setting configuration settings...");
                        try
                        {
                            setConfig("serverport", serverport);
                            setConfig("webport", webport);
                            setConfig("realm", serverhostname);
                            setConfig("forceencryption", forceEncryption ? "true" : "false");
                            PreparedStatement st =
                                pdb.prepareStatement("insert into " + pfix
                                    + "webusers (username,role,password)"
                                    + " values (?,?,?)");
                            st.setString(1, username);
                            st.setString(2, "admin");
                            st.setString(3, Hash.hash(password));
                            st.executeUpdate();
                            st.close();
                        }
                        catch (SQLException e)
                        {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured while setting up the server's initial"
                                    + "configuration. Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString() + "</pre>");
                            return;
                        }
                        // generate the RSA keys for the server
                        try
                        {
                            System.out
                                .println("generating rsa security keys for encryption...");
                            RSA rsaEnc = new RSA(3072);
                            setConfig("rsa-enc-pub", rsaEnc.getPublicKey().toString(16));
                            setConfig("rsa-enc-prv", rsaEnc.getPrivateKey().toString(16));
                            setConfig("rsa-enc-mod", rsaEnc.getModulus().toString(16));
                            System.out
                                .println("generating rsa security keys for signing...");
                            RSA rsaSgn = new RSA(3072);
                            setConfig("rsa-sgn-pub", rsaSgn.getPublicKey().toString(16));
                            setConfig("rsa-sgn-prv", rsaSgn.getPrivateKey().toString(16));
                            setConfig("rsa-sgn-mod", rsaSgn.getModulus().toString(16));
                            /*
                             * We've generated the standard keys. Now we need to
                             * generate an X.509 self-signed certificate and
                             * private key. The distinguished name of the
                             * certificate will consist of only a common name,
                             * which is the hostname for their server that they
                             * provided.
                             */
                            KeyPair certpair = CertificateUtils.createKeyPair("RSA", 3072);
                            X509Certificate gencert =
                                CertificateUtils.createCert(new X500Principal("CN="
                                    + serverhostname), new X500Principal("CN="
                                    + serverhostname), 365 * 100, null,
                                    certpair.getPublic(), certpair.getPrivate());
                            setConfig("certificate-chain", CertificateUtils
                                .writeCertChain(new X509Certificate[] { gencert }));
                            setConfig("certificate-self", CertificateUtils
                                .writeCert(gencert));
                            setConfig("certificate-private-key", CertificateUtils
                                .writePrivateKey(certpair.getPrivate()));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured while generating RSA security keys"
                                    + "for the server. Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString() + "</pre>");
                            return;
                        }
                        config.store(new FileOutputStream(configFile), "");
                        // We're done!
                        System.out.println("Server configuration complete.");
                        doneSettingUp = true;
                        response.sendRedirect("/");
                        return;
                    }
                    else
                    {
                        response.sendRedirect("/bypass/start.jsp?pdbclass=org.h2.Driver&"
                            + "pdburl="
                            + URLEncoder.encode("jdbc:h2:appdata/dbp/persistant") + "&"
                            + "pdbprefix=opengroove_&pdbusername=sa"
                            + "&ldbclass=org.h2.Driver&" + "ldburl="
                            + URLEncoder.encode("jdbc:h2:appdata/dbl/large") + "&"
                            + "ldbprefix=opengroove_&ldbusername=sa"
                            + "&serverport=63745&webport=34567");
                        return;
                    }
                }
                
                private void setuperror(String redoUrl, HttpServletResponse response,
                    String string) throws IOException
                {
                    response.sendRedirect(redoUrl + URLEncoder.encode(string));
                }
                
                @Override
                public void init(FilterConfig filterConfig) throws ServletException
                {
                    // TODO Auto-generated method stub
                    
                }
            }), "/*", Context.ALL);
            finishContext(context);
            server.start();
            Thread.sleep(200);
            System.out.println(""
                + "This is the first time you've run OpenGroove Realm Server,\r\n"
                + "so you'll need to provide some information so that the\r\n"
                + "server can be configured. Open a browser and go to\r\n"
                + "http://localhost:34567 to get OpenGroove Realm Server\r\n"
                + "up and running.");
            server.join();
            return;
        }
        // If we get here then OpenGroove has been set up, so get everything up
        // and running
        System.out.println("loading configuration files...");
        config.load(new FileInputStream(configFile));
        String pdbclass = config.getProperty("pdbclass");
        String pdburl = config.getProperty("pdburl");
        String pdbprefix = config.getProperty("pdbprefix");
        String pdbusername = config.getProperty("pdbusername");
        String pdbpassword = config.getProperty("pdbpassword");
        String ldbclass = config.getProperty("ldbclass");
        String ldburl = config.getProperty("ldburl");
        String ldbprefix = config.getProperty("ldbprefix");
        String ldbusername = config.getProperty("ldbusername");
        String ldbpassword = config.getProperty("ldbpassword");
        pfix = pdbprefix;
        lfix = ldbprefix;
        System.out.println("loading database template files...");
        // copy persistantsqlmap.xml and largesqlmap.xml to the classes folder
        // with $$prefix$$ replaced as necessary
        String psqlmaptext = readFile(new File("persistantsqlmap.xml"));
        String lsqlmaptext = readFile(new File("largesqlmap.xml"));
        psqlmaptext = psqlmaptext.replace("$$prefix$$", pfix);
        lsqlmaptext = lsqlmaptext.replace("$$prefix$$", lfix);
        writeFile(psqlmaptext, new File("classes/persistantsqlmap.xml"));
        writeFile(lsqlmaptext, new File("classes/largesqlmap.xml"));
        System.out.println("connecting to persistant database...");
        Class.forName(pdbclass);
        pfix = pdbprefix;
        pdb = DriverManager.getConnection(pdburl, pdbusername, pdbpassword);
        String psqlconfigtext = readFile(new File("persistantsql.xml"));
        psqlconfigtext = psqlconfigtext.replace("$$driver$$", pdbclass);
        psqlconfigtext = psqlconfigtext.replace("$$url$$", pdburl);
        psqlconfigtext = psqlconfigtext.replace("$$username$$", pdbusername);
        psqlconfigtext = psqlconfigtext.replace("$$password$$", pdbpassword);
        pdbclient = SqlMapClientBuilder.buildSqlMapClient(new StringReader(psqlconfigtext));
        System.out.println("connecting to large database...");
        Class.forName(ldbclass);
        lfix = ldbprefix;
        ldb = DriverManager.getConnection(ldburl, ldbusername, ldbpassword);
        String lsqlconfigtext = readFile(new File("largesql.xml"));
        lsqlconfigtext = lsqlconfigtext.replace("$$driver$$", ldbclass);
        lsqlconfigtext = lsqlconfigtext.replace("$$url$$", ldburl);
        lsqlconfigtext = lsqlconfigtext.replace("$$username$$", ldbusername);
        lsqlconfigtext = lsqlconfigtext.replace("$$password$$", ldbpassword);
        ldbclient = SqlMapClientBuilder.buildSqlMapClient(new StringReader(lsqlconfigtext));
        System.out.println("loading web server...");
        Server server = new Server(Integer.parseInt(getConfig("webport")));
        Context context = createServerContext(server, "web");
        context.addFilter(new FilterHolder(new LoginFilter()), "/*", Context.ALL);
        // context.addServlet(new ServletHolder(
        // new RendererServlet(readFile(new File(
        // "webconfig/layout.properties")))),
        // "/layout/*");
        context.addServlet(new ServletHolder(new AnonLinkImpl()), "/bypass/anonlink");
        context.addServlet(new ServletHolder(new AuthLinkImpl()), "/authlink");
        context.addServlet(new ServletHolder(new HttpServlet()
        {
            
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException
            {
                resp.setHeader("Content-type", "application/octet-stream");
                req.getRequestDispatcher("/keydownload.jsp").forward(req, resp);
            }
        }), "/serverkey.ogvs");
        finishContext(context);
        server.start();
        Thread.sleep(300);// so that stdout and stderr don't get mixed up
        System.out.println("loading periodic tasks...");
        loadPeriodicTasks();
        System.out.println("loading OpenGroove server on port " + getConfig("serverport")
            + "...");
        rsaEncryptionPublicKey = new BigInteger(getConfig("rsa-enc-pub"), 16);
        rsaEncryptionModulus = new BigInteger(getConfig("rsa-enc-mod"), 16);
        rsaEncryptionPrivateKey = new BigInteger(getConfig("rsa-enc-prv"), 16);
        rsaSignaturePublicKey = new BigInteger(getConfig("rsa-sgn-pub"), 16);
        rsaSignatureModulus = new BigInteger(getConfig("rsa-sgn-mod"), 16);
        rsaSignaturePrivateKey = new BigInteger(getConfig("rsa-sgn-prv"), 16);
        serverCertificateChain =
            CertificateUtils.readCertChain(getConfig("certificate-chain"));
        serverCertificatePrivateKey =
            CertificateUtils.readPrivateKey(getConfig("certificate-private-key"));
        serverRealmAddress = getConfig("realm");
        X509KeyManager keyManager = new X509KeyManager()
        {
            
            @Override
            public String chooseClientAlias(String[] keyType, Principal[] issuers,
                Socket socket)
            {
                return "key";
            }
            
            @Override
            public String chooseServerAlias(String keyType, Principal[] issuers,
                Socket socket)
            {
                return "key";
            }
            
            @Override
            public X509Certificate[] getCertificateChain(String alias)
            {
                return serverCertificateChain;
            }
            
            @Override
            public String[] getClientAliases(String keyType, Principal[] issuers)
            {
                return null;
            }
            
            @Override
            public PrivateKey getPrivateKey(String alias)
            {
                return serverCertificatePrivateKey;
            }
            
            @Override
            public String[] getServerAliases(String keyType, Principal[] issuers)
            {
                return null;
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(new KeyManager[] { keyManager }, new TrustManager[] {},
            new SecureRandom());
        SSLServerSocketFactory sslFactory = sslContext.getServerSocketFactory();
        serverSocket =
            (SSLServerSocket) sslFactory.createServerSocket(Integer
                .parseInt(getConfig("serverport")));
        // serverSocket
        // .setEnabledCipherSuites(new String[] { "TLS_RSA_WITH_AES_256_CBC_SHA"
        // });
        /*
         * FIXME: the above is *very* insecure (although the jvm only has a few
         * cipher suites enabled by default, so it's not the worst thing in the
         * world), but it was causing problems with only a few machines being
         * able to connect. In the future, more cipher suites should be added,
         * and the above statement re-enabled.
         */
        loadCommands();
        tasks.prestartAllCoreThreads();
        internalTasks.prestartAllCoreThreads();
        System.out.println("OpenGroove Realm Server is up and running.");
        while (!serverSocket.isClosed())
        {
            try
            {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                if (connections.size() > MAX_CONNECTIONS)
                {
                    socket.close();
                    continue;
                }
                ConnectionHandler c = new ConnectionHandler(socket);
                connections.add(c);
                c.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Thread.sleep(20);
            }
        }
        System.out.println("OpenGroove Realm Server is shutting down...");
        System.out.println("Shutting down task pool...");
        tasks.shutdown();
        for (int i = 0; i < 6; i++)
        {
            if (tasks.awaitTermination(5, TimeUnit.SECONDS))
            {
                break;
            }
            System.out.println("waited " + ((i + 1) * 5) + " seconds, waiting "
                + ((6 * 5) - ((i + 1) * 5)) + " more seconds");
            if (i == 5)
                System.out.println("Forcing shutdown of task pool...");
        }
        tasks.shutdownNow();
        System.out.println("Disconnecting all users...");
        System.out.println("Shutting down internal pool...");
        internalTasks.shutdown();
        for (int i = 0; i < 6; i++)
        {
            if (internalTasks.awaitTermination(5, TimeUnit.SECONDS))
            {
                break;
            }
            System.out.println("waited " + ((i + 1) * 5) + " seconds, waiting "
                + ((6 * 5) - ((i + 1) * 5)) + " more seconds");
            if (i == 5)
                System.out.println("Forcing shutdown of internal pool...");
        }
        internalTasks.shutdownNow();
        System.out.println("Closing connection to persistant database...");
        pdb.close();
        System.out.println("Closing connection to large database...");
        ldb.close();
        System.out.println("OpenGroove Realm Server has successfully shut down.");
        Thread.sleep(1000);
        System.exit(0);
    }
    
    private static void loadPeriodicTasks()
    {
        // task for removing stale connection handlers (connection handlers that
        // are still in the list of connections or the user maps but are using a
        // socket that has been closed or dropped)
        internalTasks.scheduleWithFixedDelay(new Runnable()
        {
            
            @Override
            public void run()
            {
                try
                {
                    for (ConnectionHandler handler : new ArrayList<ConnectionHandler>(
                        connections))
                    {
                        if (handler.socket.isClosed() || handler.socket.isInputShutdown()
                            || handler.socket.isOutputShutdown()
                            || (!handler.socket.isConnected()))
                        {
                            try
                            {
                                handler.socket.close();
                            }
                            catch (Exception ex1)
                            {
                                ex1.printStackTrace();
                            }
                            connections.remove(handler);
                        }
                    }
                    // TODO: stale connection handlers in the connections list
                    // are handled but not those in the user connection maps
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 30, 307, TimeUnit.SECONDS);
        // task for removing idle connection handlers (connection handlers where
        // nothing has been sent or received within the connection's idle time
        // limit)
        internalTasks.scheduleWithFixedDelay(new Runnable()
        {
            
            @Override
            public void run()
            {
                
            }
        }, 30, 63, TimeUnit.SECONDS);
        /*
         * TODO: Perhaps, in the future, this runnable should adapt itself to
         * the current message throughput; for example, it would schedule itself
         * to run more often if messages are coming through frequently, and
         * schedule itself to run less often if message amount slows down.
         */
        internalTasks.scheduleWithFixedDelay(new MessageDataRemover(), 1, 15,
            TimeUnit.MINUTES);
        internalTasks.scheduleWithFixedDelay(new ReadMessageRemover(), 1, 11,
            TimeUnit.MINUTES);
    }
    
    private static ConnectionHandler getConnectionForComputer(String username,
        String computer)
    {
        username = username.toLowerCase();
        computer = computer.toLowerCase();
        Map<String, ConnectionHandler> userMap = connectionsByAuth.get(username);
        if (userMap == null)
        {
            System.out.println("no user map");
            return null;
        }
        ConnectionHandler handler = userMap.get(computer);
        if (handler == null)
        {
            System.out.println("no handler");
            return null;
        }
        if (!connections.contains(handler))
        {
            System.out.println("handler not in list");
            return null;
        }
        if (!handler.isAlive())
        {
            System.out.println("handler is dead");
            return null;
        }
        return handler;
    }
    
    private static ConnectionHandler[] getConnectionsForUser(String username)
    {
        username = username.toLowerCase();
        Map<String, ConnectionHandler> userMap = connectionsByAuth.get(username);
        if (userMap == null)
        {
            System.out.println("no user map");
            return new ConnectionHandler[0];
        }
        ConnectionHandler[] consider = userMap.values().toArray(new ConnectionHandler[0]);
        ArrayList<ConnectionHandler> results = new ArrayList<ConnectionHandler>();
        for (ConnectionHandler handler : consider)
        {
            if (OpenGrooveRealmServer.connections.contains(handler) && handler.isAlive())
                results.add(handler);
        }
        return results.toArray(new ConnectionHandler[0]);
    }
    
    public static String[] tokenizeByLines(String data)
    {
        BufferedReader reader = new BufferedReader(new StringReader(data));
        ArrayList<String> tokens = new ArrayList<String>();
        String s;
        try
        {
            while ((s = reader.readLine()) != null)
                tokens.add(s);
            reader.close();
        }
        catch (IOException e)
        {
            // shouldn't happen
            throw new RuntimeException(e);
        }
        return tokens.toArray(new String[0]);
    }
    
    private static void verifyAtLeast(Object[] objects, int minLength)
    {
        if (objects.length < minLength)
            throw new FailedResponseException(Status.FAIL, "Input too short (expected "
                + minLength + ", found " + objects.length + ")");
    }
    
    private static byte[] readToBytes(InputStream input) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(input, baos);
        return baos.toByteArray();
    }
    
    private static void loadCommands()
    {
        new Command("authenticate", 512, true, true)
        {
            
            @Override
            public synchronized void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws IOException, SQLException
            {
                if (connection.username != null)
                {
                    throw new FailedResponseException("You're already authenticated");
                }
                String[] tokens = tokenizeByLines(new String(readToBytes(data)));
                verifyAtLeast(tokens, 4);
                String connectionType = tokens[0];
                String username = tokens[1];
                String computerName = tokens[2];
                String password = tokens[3];
                username = username.trim();
                computerName = computerName.trim();
                username = username.toLowerCase();
                computerName = computerName.toLowerCase();
                password = password.trim();
                connectionType = connectionType.trim();
                if (connectionType.equalsIgnoreCase("normal"))
                {
                    if ((!computerName.equals(""))
                        && getConnectionForComputer(username, computerName) != null)
                    {
                        throw new FailedResponseException(
                            "You already have a connection to this server");
                    }
                    else if (computerName.equals(""))
                    {
                        // The user isn't signing on with a computer, so we need
                        // to hand-scan the connections list for a connection
                        // with this username and no computer to verify that
                        // such a connection does not exist
                        //
                        // TODO: Is this a good idea? In retrospect, it seems
                        // that having a user signed on multiple times without a
                        // computer isn't a problem, they just should be able to
                        // see that there is another connection that currently
                        // exists
                        for (ConnectionHandler ich : new ArrayList<ConnectionHandler>(
                            connections))
                        {
                            if (ich.username != null
                                && ich.username.equalsIgnoreCase(username)
                                && (ich.computerName == null || ich.computerName.equals("")))
                            {
                                throw new FailedResponseException(
                                    "You already have a connection to this server");
                            }
                        }
                    }
                    // The user doesn't have a connection, so let's proceed with
                    // checking their password
                    String passwordHash = Hash.hash(password);
                    User confirmedAuthUser = DataStore.getUser(username, passwordHash);
                    if (confirmedAuthUser == null)
                    {
                        throw new FailedResponseException(Status.BADAUTH,
                            "Incorrect username and/or password");
                    }
                    // The user has successfully authenticated. Now we need to
                    // check and see if the computer they specified exists, if
                    // they specified a computer.
                    if (!computerName.equals(""))
                    {
                        Computer computer = DataStore.getComputer(username, computerName);
                        if (computer == null)
                        {
                            throw new FailedResponseException(Status.BADCOMPUTER,
                                "Nonexistant computer specified");
                        }
                    }
                    connection.username = username;
                    connection.computerName =
                        (computerName.equals("") ? null : computerName);
                    if (connection.computerName != null)
                    {
                        Map<String, ConnectionHandler> userMap =
                            connectionsByAuth.get(connection.username);
                        if (userMap == null)
                        {
                            userMap = new Hashtable<String, ConnectionHandler>();
                            connectionsByAuth.put(connection.username, userMap);
                        }
                        userMap.put(connection.computerName, connection);
                        Computer computerObject =
                            DataStore.getComputer(connection.username,
                                connection.computerName);
                        computerObject.setLastonline(System.currentTimeMillis());
                        DataStore.updateComputer(computerObject);
                        tasks.execute(new UserStatusNotifier(connection.username,
                            connection.computerName));
                        
                    }
                    connection.sendEncryptedPacket(packetId, "authenticate", Status.OK,
                        EMPTY);
                }
            }
        };
        new Command("ping", 8, true, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection)
            {
                connection.sendEncryptedPacket(packetId, "ping", Status.OK, new byte[0]);
            }
        };
        new Command("quit", 8, true, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                try
                {
                    connection.socket.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        };
        new Command("gettime", 8, true, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                long timeMillis = System.currentTimeMillis();
                connection.sendEncryptedPacket(packetId, "gettime", Status.OK, (""
                    + timeMillis + " " + new Date(timeMillis)).getBytes());
            }
            
        };
        new Command("createcomputer", 64, false, true)// ,boolean whenUnauth,
        // boolean
        // whenNoComputer
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenizeByLines(new String(readToBytes(data)));
                verifyAtLeast(tokens, 2);
                if (DataStore.getComputersForUser(connection.username).length >= DataStore
                    .getUserQuota(connection.username, "computers"))
                    throw new FailedResponseException(Status.QUOTAEXCEEDED,
                        "You have the maximum number of computers already, which is "
                            + DataStore.getUserQuota(connection.username, "computers"));
                // Ok, the user is authenticated and is allowed to create a
                // computer (IE they haven't exceeded their quota yet)
                String computerName = tokens[0];
                String computerType = tokens[1];
                DataStore.addComputer(connection.username, computerName, computerType);
                connection.sendEncryptedPacket(packetId, "createcomputer", Status.OK, EMPTY);
            }
            
        };
        new Command("sendimessage", 8320, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                byte[] dataBytes = readToBytes(data);
                String firstSubsection =
                    new String(dataBytes, 0, Math.min(128, dataBytes.length));
                String[] tokens = firstSubsection.split(" ", 5);
                verifyAtLeast(tokens, 4);
                String messageId = tokens[0];
                String recipientUser = tokens[1];
                String recipientComputer = tokens[2];
                recipientUser = relativeId(recipientUser);
                checkUsername(recipientUser);
                int dataIndex =
                    messageId.length() + recipientUser.length() + recipientComputer.length()
                        + 3;
                byte[] messageContents = new byte[dataBytes.length - dataIndex];
                System.arraycopy(dataBytes, dataIndex, messageContents, 0,
                    messageContents.length);
                ConnectionHandler recipientConnection =
                    getConnectionForComputer(recipientUser, recipientComputer);
                if (recipientConnection == null)
                    throw new FailedResponseException(Status.NOSUCHUSER,
                        "The recipient does not exist or is offline");
                recipientConnection.sendEncryptedPacket(generateId(), "receiveimessage",
                    Status.OK, concat(("" + messageId + " " + connection.username + " "
                        + connection.computerName + " ").getBytes(), messageContents));
                connection.sendEncryptedPacket(packetId, "sendimessage", Status.OK, EMPTY);
            }
            
        };
        new Command("setpassword", 128, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String newPassword = new String(readToBytes(data));
                if (newPassword.contains("\r") || newPassword.contains("\n")
                    || newPassword.startsWith(" ") || newPassword.endsWith(" "))
                    throw new FailedResponseException(
                        "Passwords can't start or end with a space, "
                            + "and can't contain newlines.");
                User user = DataStore.getUser(connection.username);
                user.setPassword(Hash.hash(newPassword));
                DataStore.updateUser(user);
                /*
                 * TODO: In the future, we should kick all of the user's current
                 * connections off of the server. That way, all other computers
                 * will have to provide the new password.
                 */
                connection.sendEncryptedPacket(packetId, "setpassword", Status.OK, EMPTY);
            }
        };
        new Command("getuserstatus", 128, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String dataString = new String(readToBytes(data));
                String[] tokens = tokenizeByLines(dataString);
                if (tokens.length == 1)
                    tokens = new String[] { tokens[0], "" };
                verifyAtLeast(tokens, 2);
                String username = tokens[0];
                String computerName = tokens[1];
                username = relativeId(username);
                checkUsername(username);
                boolean isOnline = false;
                String lastOnline = "";
                boolean isComputer = !computerName.equals("");
                if (isComputer)
                {
                    Computer computer = DataStore.getComputer(username, computerName);
                    if (computer == null)
                        throw new FailedResponseException(Status.NOSUCHCOMPUTER,
                            "The computer specified doesn't exist");
                    lastOnline +=
                        computer.getLastonline() + " " + new Date(computer.getLastonline());
                    isOnline = getConnectionForComputer(username, computerName) != null;
                }
                else
                {
                    if (DataStore.getUser(username) == null)
                        throw new FailedResponseException(Status.NOSUCHUSER,
                            "The user specified doesn't exist");
                    System.out.println("getting information for user " + username);
                    long lastOnlineValue = DataStore.getUserLastOnline(username);
                    lastOnline += lastOnlineValue + " " + new Date(lastOnlineValue);
                    Map userMap = connectionsByAuth.get(username);
                    isOnline = userMap != null && userMap.size() > 0;
                }
                connection.sendEncryptedPacket(packetId, "getuserstatus", Status.OK, (""
                    + isOnline + "\n" + lastOnline).getBytes());
            }
        };
        new Command("setvisibility", 10, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String s = new String(readToBytes(data));
                User user = DataStore.getUser(connection.username);
                user.setPubliclylisted(s.trim().equalsIgnoreCase("true"));
                DataStore.updateUser(user);
                connection.sendEncryptedPacket(packetId, "setvisibility", Status.OK, EMPTY);
            }
        };
        new Command("getvisibility", 10, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                User user = DataStore.getUser(connection.username);
                connection.sendEncryptedPacket(packetId, "getvisibility", Status.OK,
                    ("" + user.isPubliclylisted()).getBytes());
            }
            
        };
        new Command("searchusers", 768, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                // FIXME: needs to be redone to match the new spec for this
                // command present at the command spec on
                // www.opengroove.org/dev/protocol/commands/client---realm-server
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 4);
                String searchString = tokens[0];
                String offsetString = tokens[1];
                String limitString = tokens[2];
                String searchOtherServersString = tokens[3];
                String[] keysToSearch = new String[tokens.length - 4];
                System.arraycopy(tokens, 4, keysToSearch, 0, keysToSearch.length);
                for (String cKey : keysToSearch)
                {
                    if (cKey.trim().equals(""))
                        continue;
                    if (!cKey.startsWith("public-"))
                        throw new FailedResponseException(Status.FAIL,
                            "The user settings specified must all start with public-");
                }
                // We've got the search criteria, now it's time to do the actual
                // search
                User[] users =
                    DataStore
                        .searchUsers("*" + searchString + "*", Integer
                            .parseInt(offsetString), Integer.parseInt(limitString),
                            keysToSearch);
                int length =
                    DataStore
                        .searchUsersCount("*" + searchString + "*", Integer
                            .parseInt(offsetString), Integer.parseInt(limitString),
                            keysToSearch);
                connection.sendEncryptedPacket(packetId, command(), Status.OK, ("" + length
                    + "\n" + delimited(users, new ToString<User>()
                {
                    
                    @Override
                    public String toString(User object)
                    {
                        return object.getUsername();
                    }
                }, "\n")).getBytes());
            }
            
        };
        new Command("getusersetting", 256, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 2);
                String username = tokens[0];
                String property = tokens[1];
                username = relativeId(username);
                checkUsername(username);
                boolean isPublic = property.startsWith("public-");
                boolean isPrivate = !isPublic;
                boolean isThisUser = username.equals("");
                boolean isOtherUser = !isThisUser;
                if (isThisUser)
                    username = connection.username;
                if (isPrivate && isOtherUser)
                    throw new FailedResponseException(Status.FAIL,
                        "Only properties starting with public- "
                            + "can be read for other users.");
                UserSetting setting = DataStore.getUserSetting(username, property);
                String value = setting == null ? "" : setting.getValue();
                connection.sendEncryptedPacket(packetId, "getusersetting", Status.OK, value
                    .getBytes());
            }
            
        };
        new Command("listusersettings", 128, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 1);
                String username = tokens[0];
                username = relativeId(username);
                checkUsername(username);
                UserSetting[] settings;
                if (username.equals(""))
                    settings = DataStore.listUserSettings(connection.username);
                else
                    settings = DataStore.listPublicUserSettings(username);
                connection.sendEncryptedPacket(packetId, "listusersettings", Status.OK,
                    delimited(settings, new ToString<UserSetting>()
                    {
                        
                        @Override
                        public String toString(UserSetting object)
                        {
                            return object.getName();
                        }
                    }, "\n").getBytes());
            }
            
        };
        new Command("setusersetting", 2048, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 2);
                String name = tokens[0];
                String value = tokens[1];
                boolean delete = value.equals("");
                UserSetting existingSetting =
                    DataStore.getUserSetting(connection.username, name);
                int settingSize = DataStore.getUserSettingSize(connection.username);
                if (existingSetting != null)
                    settingSize =
                        settingSize
                            - (existingSetting.getName().length()
                                + existingSetting.getValue().length() + 10);
                settingSize += 10 + name.length() + value.length();
                if ((!delete)
                    && settingSize > DataStore.getUserQuota(connection.username,
                        "usersettingsize"))
                    throw new FailedResponseException(
                        Status.QUOTAEXCEEDED,
                        "You have "
                            + DataStore.getUserQuota(connection.username, "usersettingsize")
                            + "allowed user setting bytes, but with this new property your size would be "
                            + settingSize);
                DataStore.setUserSetting(connection.username, name, value);
                tasks.execute(new UserSettingNotifier(connection.username, null, name));
                connection.sendEncryptedPacket(packetId, "setusersetting", Status.OK, EMPTY);
            }
            
        };
        new Command("listcomputers", 128, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 1);
                String username = tokens[0];
                if (username.equals(""))
                    username = connection.username;
                username = relativeId(username);
                checkUsername(username);
                Computer[] computers = DataStore.listComputersByUser(username);
                if (computers.length == 0)
                {
                    if (DataStore.getUser(username) == null)
                        throw new FailedResponseException(Status.NOSUCHUSER,
                            "The user specified does not exist.");
                    throw new FailedResponseException(Status.NORESULTS,
                        "The user specified has not created any computers.");
                }
                connection.sendEncryptedPacket(packetId, command(), Status.OK, delimited(
                    computers, new ToString<Computer>()
                    {
                        
                        @Override
                        public String toString(Computer object)
                        {
                            return object.getComputername();
                        }
                    }, "\n").getBytes());
            }
        };
        new Command("getcomputersetting", 256, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 3);
                String username = tokens[0];
                String computerName = tokens[1];
                username = relativeId(username);
                checkUsername(username);
                if (computerName.equals(""))
                    computerName = connection.computerName;
                if (computerName == null || computerName.equals(""))
                    throw new FailedResponseException(Status.FAIL,
                        "No computer specified and not authenticated as a computer");
                String name = tokens[2];
                boolean allowPrivate = username.equals("");
                if (allowPrivate)
                    username = connection.username;
                boolean isPublic = !allowPrivate;
                boolean isIntendedPublic = name.startsWith("public-");
                boolean isIntendedPrivate = !isIntendedPublic;
                if (isPublic && isIntendedPrivate)
                {
                    throw new FailedResponseException(Status.FAIL,
                        "only properties starting with public- "
                            + "can be read from other computers");
                }
                ComputerSetting setting =
                    DataStore.getComputerSetting(username, computerName, name);
                connection.sendEncryptedPacket(packetId, command(), Status.OK,
                    (setting == null ? "" : setting.getValue()).getBytes());
            }
        };
        new Command("help", 256, true, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                connection
                    .sendEncryptedPacket(
                        packetId,
                        command(),
                        Status.OK,
                        "You're speaking to an OpenGroove Realm Server.\r\n"
                            + "Visit www.opengroove.org if you have any questions, or you can send\r\n"
                            + "an email to webmaster@opengroove.org, mentioning\r\n"
                            + "that you read this on realm \""
                            + serverRealmAddress
                            + "\" .\r\n\r\n"
                            + "Here's a list of all of the commands that this server knows:\r\n\r\n"
                            + delimited(commands.values().toArray(new Command[0]),
                                new ToString<Command>()
                                {
                                    
                                    @Override
                                    public String toString(Command object)
                                    {
                                        return object.commandName;
                                    }
                                }, "\r\n"));
            }
            
        };
        new Command("getcomputerinfo", 256, false, true)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 2);
                String username = tokens[0];
                String computername = tokens[1];
                username = relativeId(username);
                checkUsername(username);
                if (computername.equals("") && username.equals(""))
                    computername = connection.computerName;
                if (username.equals(""))
                    username = connection.username;
                Computer computer = DataStore.getComputer(username, computername);
                if (computer == null)
                    throw new FailedResponseException(Status.NOSUCHCOMPUTER,
                        "The computer specified does not exist");
                connection.sendEncryptedPacket(packetId, command(), Status.OK, (""
                    + computer.getType() + "\n" + computer.getCapabilities()).getBytes());
            }
            
        };
        new Command("setcomputersetting", 2048, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 3);
                String computerName = tokens[0];
                if (computerName.equals(""))
                    computerName = connection.computerName;
                String name = tokens[1];
                String value = tokens[2];
                boolean delete = value.equals("");
                ComputerSetting existingSetting =
                    DataStore.getComputerSetting(connection.username, computerName, name);
                int settingSize =
                    DataStore.getComputerSettingSize(connection.username, computerName);
                if (existingSetting != null)
                    settingSize =
                        settingSize
                            - (existingSetting.getName().length()
                                + existingSetting.getValue().length() + 10);
                settingSize += 10 + name.length() + value.length();
                if ((!delete)
                    && settingSize > DataStore.getUserQuota(connection.username,
                        "computersettingsize"))
                    throw new FailedResponseException(
                        Status.QUOTAEXCEEDED,
                        "You have "
                            + DataStore.getUserQuota(connection.username,
                                "computersettingsize")
                            + "allowed computer setting bytes, but with this new property your size would be "
                            + settingSize);
                DataStore.setComputerSetting(connection.username, computerName, name, value);
                tasks.execute(new UserSettingNotifier(connection.username, computerName,
                    name));
                connection.sendEncryptedPacket(packetId, command(), Status.OK, EMPTY);
            }
        };
        new Command("listcomputersettings", 128, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 2);
                String username = tokens[0];
                username = relativeId(username);
                checkUsername(username);
                String computerName = tokens[1];
                ComputerSetting[] settings;
                if (computerName.equals(""))
                    computerName = connection.computerName;
                if (username.equals(""))
                    settings =
                        DataStore.listComputerSettings(connection.username, computerName);
                else
                    settings = DataStore.listPublicComputerSettings(username, computerName);
                connection.sendEncryptedPacket(packetId, command(), Status.OK, delimited(
                    settings, new ToString<ComputerSetting>()
                    {
                        
                        @Override
                        public String toString(ComputerSetting object)
                        {
                            return object.getName();
                        }
                    }, "\n").getBytes());
            }
        };
        new Command("createsubscription", 256, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 5);
                if (DataStore.getSubscriptionCount(connection.username) >= DataStore
                    .getUserQuota(connection.username, "subscriptions"))
                    throw new FailedResponseException(Status.QUOTAEXCEEDED,
                        "You have too many active subscriptions right now.");
                String type = tokens[0];
                String onuser = tokens[1];
                onuser = relativeId(onuser);
                checkUsername(onuser);
                String oncomputer = tokens[2];
                String onsetting = tokens[3];
                boolean deletewithtarget = tokens[4].equalsIgnoreCase("true");
                Subscription subscription = new Subscription();
                subscription.setType(type);
                subscription.setUsername(connection.username);
                subscription.setOnusername(onuser);
                subscription.setOncomputername(oncomputer);
                subscription.setOnsettingname(onsetting);
                subscription.setDeletewithtarget(deletewithtarget);
                subscription.setProperties("");
                if (DataStore.getMatchingSubscriptionCount(subscription) != 0)
                    throw new FailedResponseException(Status.ALREADYEXISTS,
                        "You already have a subscription with the settings specified.");
                DataStore.insertSubscription(subscription);
                connection.sendEncryptedPacket(packetId, command(), Status.OK, EMPTY);
            }
        };
        /*
         * userexists doesn't seem to be working for some reason, so I'm not
         * using it right now. getuserstatus works as a (slightly more
         * expensive) replacement for this command, by checking to see if the
         * returned status is OK, in which case the user exists, or NOSUCHUSER,
         * in which case the user does not exist. I'll probably remove it if I
         * can't fix it.
         */
        new Command("userexists", 128, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                System.out.println("checking if user exists");
                String username = tokenize(data)[0];
                username = relativeId(username);
                checkUsername(username);
                System.out.println("resolved userid is " + username);
                if (DataStore.getUser(username) != null)
                {
                    connection.sendEncryptedPacket(packetId, command(), Status.OK, "true");
                    System.out.println("responded with true");
                }
                else
                {
                    connection.sendEncryptedPacket(packetId, command(), Status.OK, "false");
                    System.out.println("responded with false");
                }
            }
        };
        new Command("listsubscriptions", 128, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                Subscription[] subscriptions =
                    DataStore.listSubscriptionsByUser(connection.username);
                if (subscriptions.length == 0)
                    throw new FailedResponseException(Status.NORESULTS, "");
                connection.sendEncryptedPacket(packetId, command(), Status.OK, delimited(
                    subscriptions, new ToString<Subscription>()
                    {
                        
                        @Override
                        public String toString(Subscription object)
                        {
                            return object.getType() + " " + object.getOnusername() + " "
                                + object.getOncomputername() + " "
                                + object.getOnsettingname() + " "
                                + object.isDeletewithtarget();
                        }
                    }, "\n"));
            }
        };
        new Command("deletesubscription", 128, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 5);
                String type = tokens[0];
                String onuser = tokens[1];
                onuser = relativeId(onuser);
                checkUsername(onuser);
                String oncomputer = tokens[2];
                String onsetting = tokens[3];
                boolean deletewithtarget = tokens[4].equalsIgnoreCase("true");
                Subscription subscription = new Subscription();
                subscription.setType(type);
                subscription.setUsername(connection.username);
                subscription.setOnusername(onuser);
                subscription.setOncomputername(oncomputer);
                subscription.setOnsettingname(onsetting);
                subscription.setDeletewithtarget(deletewithtarget);
                subscription.setProperties("");
                if (DataStore.getMatchingSubscriptionCount(subscription) == 0)
                    throw new FailedResponseException(Status.NOSUCHSUBSCRIPTION,
                        "The subscription specified does not exist");
                DataStore.deleteSubscription(subscription);
                connection.sendEncryptedPacket(packetId, command(), Status.OK, EMPTY);
            }
        };
        new Command("createmessage", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                /*
                 * We need at least 2 tokens, one for the message's id, and one
                 * for a recipient (a message must have at least one recipient)
                 */
                verifyAtLeast(tokens, 2);
                String messageId = tokens[0];
                if (!messageId.startsWith(Userids.toUserid(serverRealmAddress,
                    connection.username)
                    + "-"))
                    throw new FailedResponseException(Status.INVALIDMESSAGEID,
                        "The message id you specified does not start with the "
                            + "user's userid followed by a hyphen.");
                if (!messageId.matches("[A-Za-z0-9\\-\\:]*"))
                    throw new FailedResponseException(Status.INVALIDMESSAGEID,
                        "The message id you specified contains characters "
                            + "other than A-Z, a-z, 0-9, -, and :");
                /*
                 * The message id is a valid id. Before we actually create the
                 * message, we need to make sure that the user hasn't created
                 * one with the same id. Since a message id should never be
                 * reused, we won't bother checking the messageData folder to
                 * see if a message is there. Not checking could have the effect
                 * that a reused message id could get deleted mid-upload, but we
                 * don't care about that since duplicate message ids aren't
                 * supposed to be used.
                 */
                Message message = DataStore.getMessage(messageId);
                if (message != null)
                {
                    throw new FailedResponseException(Status.ALREADYEXISTS,
                        "The message id specified represents a "
                            + "message that already exists");
                }
                message = new Message();
                message.setId(messageId);
                message.setSender(connection.getUserid());
                message.setComputer(connection.computerName);
                message.setSent(false);
                if (!new File(messageDataFolder, message.getFileId()).createNewFile())
                    throw new FailedResponseException(
                        Status.FAIL,
                        "Creating the message's storage file failed. This is probably "
                            + "due to a server malfunction. Contact the owner of this server (it's "
                            + "realm name is "
                            + serverRealmAddress
                            + ", so you could try going "
                            + "there in a browser and finding a contact link or something) and "
                            + "report the problem, or go to www.opengroove.org and contact us "
                            + "with information about this problem. (send an email to "
                            + "support@opengroove.org for questions.)");
                /*
                 * We've created the file for the message's data at this point.
                 * Now we'll remove any message recipients that might already
                 * exist, and add the new recipients. The main reason for this
                 * is that if the server crashes in the middle of adding
                 * recipients, we'll want to remove them when the user creates
                 * the message again. We also add the recipients before the
                 * message for the same reason, so that if the server crashes,
                 * either there is a message that is complete with recipients,
                 * or there is no message at all.
                 */
                MessageRecipient[] existingRecipients =
                    DataStore.listMessageRecipients(messageId);
                for (MessageRecipient recipient : existingRecipients)
                {
                    DataStore.deleteMessageRecipient(recipient);
                }
                for (int i = 1; i < tokens.length; i++)
                {
                    String[] recipientTokens = tokens[i].split(" ", 2);
                    if (recipientTokens.length == 1)
                        recipientTokens = new String[] { recipientTokens[0], "" };
                    String recipientUserid = resolveToUserid(recipientTokens[0]);
                    String recipientComputer = recipientTokens[1];
                    recipientComputer = recipientComputer.trim();
                    boolean hasRecipientComputer = !recipientComputer.equals("");
                    boolean recipientIsLocal =
                        Userids.toRealm(recipientUserid)
                            .equalsIgnoreCase(serverRealmAddress);
                    if (recipientIsLocal && !hasRecipientComputer)
                    {
                        /*
                         * The recipient is local and the recipient computer is
                         * not defined. We'll expand the recipient into a list
                         * of all of it's computers, and add each of them as a
                         * recipient.
                         */
                        Computer[] expandedComputers =
                            DataStore.listComputersByUser(relativeId(recipientUserid));
                        for (Computer expandedComputer : expandedComputers)
                        {
                            DataStore.addMessageRecipient(new MessageRecipient(messageId,
                                recipientUserid, expandedComputer.getComputername()));
                        }
                    }
                    else
                    {
                        /*
                         * The recipient is of a different realm or the
                         * recipient computer is defined.We'll add the
                         * recipient. If the recipient computer doesn't exist,
                         * this will be picked up when all other recipients have
                         * received and deleted the message.
                         */
                        DataStore.addMessageRecipient(new MessageRecipient(messageId,
                            recipientUserid, recipientComputer));
                    }
                }
                DataStore.addMessage(message);
                connection.sendEncryptedPacket(packetId, command(), Status.OK,
                    "The message has been successfully created.");
            }
            
        };
        new Command("setmessagesize", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                /*
                 * TODO: implement this command. The OpenGroove Client probably
                 * won't use it, so it's not top priority.
                 */
            }
            
        };
        new Command("getmessagesize", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 1);
                String messageId = tokens[0];
                Message message = DataStore.getMessage(messageId);
                if (message == null)
                    throw new FailedResponseException(Status.NOSUCHMESSAGE, "");
                verifyCanReadMessage(message, connection.username, connection.computerName);
                File messageFile = new File(messageDataFolder, message.getFileId());
                if (!messageFile.exists())
                    messageFile.createNewFile();
                long size = messageFile.length();
                connection.sendEncryptedPacket(packetId, command(), Status.OK, "" + size);
            }
            
        };
        new Command("readmessagedata", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 3);
                String messageId = tokens[0];
                int offset;
                int length;
                try
                {
                    offset = Integer.parseInt(tokens[1]);
                    length = Integer.parseInt(tokens[2]);
                }
                catch (NumberFormatException e)
                {
                    throw new FailedResponseException(Status.FAIL,
                        "The offset or the length/amount was not a number.");
                }
                if (length > 64000)
                    throw new FailedResponseException(Status.INDEXOUTOFBOUNDS,
                        "The length is too long (it can't " + "be longer than 64000 bytes)");
                Message message = DataStore.getMessage(messageId);
                verifyCanReadMessage(message, connection.username, connection.computerName);
                File messageFile = new File(messageDataFolder, message.getFileId());
                if (!messageFile.exists())
                    messageFile.createNewFile();
                RandomAccessFile in = new RandomAccessFile(messageFile, "r");
                in.seek(offset);
                byte[] fileData = new byte[length];
                try
                {
                    in.readFully(fileData);
                }
                catch (EOFException e)
                {
                    throw new FailedResponseException(Status.INDEXOUTOFBOUNDS,
                        "You're trying to read past the end of the message");
                }
                connection.sendEncryptedPacket(packetId, command(), Status.OK, fileData);
            }
            
        };
        new Command("writemessagedata", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                byte[] dataBytes = readToBytes(data);
                byte[] first128 = new byte[Math.min(dataBytes.length, 128)];
                System.arraycopy(dataBytes, 0, first128, 0, first128.length);
                String first128string = new String(first128);
                String[] tokens = first128string.split(" ");
                verifyAtLeast(tokens, 3);
                String messageId = tokens[0];
                int offset;
                int length;
                try
                {
                    offset = Integer.parseInt(tokens[1]);
                    length = Integer.parseInt(tokens[2]);
                }
                catch (NumberFormatException e)
                {
                    throw new FailedResponseException(Status.FAIL,
                        "The offset or the length/amount was not a number.");
                }
                int actualDataIndex =
                    tokens[0].length() + tokens[1].length() + tokens[2].length() + 3;
                if (length > 64000)
                    throw new FailedResponseException(Status.INDEXOUTOFBOUNDS,
                        "The length is too long (it can't " + "be longer than 64000 bytes)");
                if ((offset + length) > MAX_MESSAGE_SIZE)
                    throw new FailedResponseException(Status.INDEXOUTOFBOUNDS,
                        "Messages cannot be larger than 2GB");
                if ((length + actualDataIndex) > dataBytes.length)
                    throw new FailedResponseException(Status.INDEXOUTOFBOUNDS, "");
                Message message = DataStore.getMessage(messageId);
                verifyCanWriteMessage(message, connection.username, connection.computerName);
                File messageFile = new File(messageDataFolder, message.getFileId());
                if (!messageFile.exists())
                    messageFile.createNewFile();
                if ((messageFile.getUsableSpace() - length) < MIN_FREE_DISK_SPACE)
                    throw new FailedResponseException(Status.FAIL,
                        "The server has run out of disk space.");
                RandomAccessFile out = new RandomAccessFile(messageFile, "rw");
                out.seek(offset);
                out.write(dataBytes, actualDataIndex, length);
                out.close();
                connection.sendEncryptedPacket(packetId, command(), Status.OK, "");
            }
            
        };
        new Command("readmessagehash", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
            }
            
        };
        new Command("deletemessage", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 1);
                String messageId = tokens[0];
                Message message = DataStore.getMessage(messageId);
                verifyCanReadMessage(message, connection.username, connection.computerName);
                /*
                 * At this point, the message exists, and we are allowed to read
                 * the message. This means that if it hasn't been sent then we
                 * are the sender, and if it has then we are a recipient. All we
                 * need to do now is check to see if the message has been sent.
                 * If it has, we remove this username/computer as a recipient.
                 * If it hasn't, we remove all recipients and the message. We
                 * don't need to worry about removing the storage file.
                 */
                boolean isSender = !message.isSent();
                if (isSender)
                {
                    DataStore.deleteMessage(messageId);
                    for (MessageRecipient recipient : DataStore
                        .listMessageRecipients(messageId))
                    {
                        DataStore.deleteMessageRecipient(recipient);
                    }
                }
                else
                {
                    DataStore.deleteMessageRecipient(new MessageRecipient(messageId,
                        resolveToUserid(connection.username), connection.computerName));
                }
                connection.sendEncryptedPacket(packetId, command(), Status.OK, "");
            }
            
        };
        new Command("sendmessage", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                /*
                 * This command is somewhat more complex than it would seem. At
                 * first glance, it appears that all you have to do, besides
                 * authorization checks, is mark the message as sent in the
                 * database. You indeed have to do this, but the recipients
                 * should also be notified that they have a new message waiting
                 * for them, and the message needs to be sent to inter-realm
                 * servers for the realms of message recipients not of this
                 * realm. In addition, if this realm doesn't have OpenGroove CA
                 * signed keys and if one of the message's recipients is of a
                 * different realm, then we should send a user notification
                 * (unless we've sent one recently) to the sender of the
                 * message, telling them that this server's keys are not signed
                 * and so they can't send messages to other realms.
                 * 
                 * After the authorization checks (IE making sure that this user
                 * is the sender of the message and that the message has not
                 * been sent yet), we'll mark the message as sent. Then, we'll
                 * put a task in the task executor to notify all local
                 * recipients that the message is here. Then, we'll put a task
                 * in the task executor to handle sending the message to other
                 * realms.
                 */
                String[] tokens = tokenizeAndVerify(data, 1);
                String messageId = tokens[0];
                Message message = DataStore.getMessage(messageId);
                verifyCanWriteMessage(message, connection.username, connection.computerName);
                message.setSent(true);
                DataStore.updateMessage(message);
                tasks.execute(new MessageAvailableNotifier(messageId));
                tasks.execute(new InterRealmMessageSender(messageId));
                connection.sendEncryptedPacket(packetId, command(), Status.OK, "");
            }
        };
        new Command("listinboundmessages", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] messageIds =
                    DataStore.listInboundMessages(new MessageRecipient(null,
                        resolveToUserid(connection.username), connection.computerName));
                if (messageIds.length == 0)
                    connection
                        .sendEncryptedPacket(packetId, command(), Status.NORESULTS, "");
                else
                    connection.sendEncryptedPacket(packetId, command(), Status.OK,
                        StringUtils.delimited(messageIds, "\n"));
            }
            
        };
        new Command("listoutboundmessages", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] messageIds =
                    DataStore
                        .listOutboundMessages(new Message(null,
                            resolveToUserid(connection.username), connection.computerName,
                            false));
                if (messageIds.length == 0)
                    connection
                        .sendEncryptedPacket(packetId, command(), Status.NORESULTS, "");
                else
                    connection.sendEncryptedPacket(packetId, command(), Status.OK,
                        StringUtils.delimited(messageIds, "\n"));
            }
            
        };
        new Command("getmessageinfo", 0, false, false)
        {
            
            @Override
            public void handle(String packetId, InputStream data,
                ConnectionHandler connection) throws Exception
            {
                String[] tokens = tokenizeAndVerify(data, 1);
                String messageId = tokens[0];
                Message message = DataStore.getMessage(messageId);
                verifyCanReadMessage(message, connection.username, connection.computerName);
                connection.sendEncryptedPacket(packetId, command(), Status.OK, ""
                    + message.getSender() + "\n" + message.getComputer() + "\n"
                    + message.isSent());
            }
            
        };
        System.out.println("loaded " + commands.size() + " commands");
    }
    
    protected static String[] tokenizeAndVerify(InputStream data, int i) throws IOException
    {
        String[] tokens = tokenize(data);
        verifyAtLeast(tokens, i);
        return tokens;
    }
    
    /**
     * Essentially the same as verifyCanReadMessage, but throws an exception if
     * the message has already been sent.
     * 
     * @param message
     * @param username
     * @param computerName
     */
    protected static void verifyCanWriteMessage(Message message, String username,
        String computer)
    {
        if (message == null)
            throw new FailedResponseException(Status.NOSUCHMESSAGE, "");
        username = resolveToUserid(username);
        if (message.isSent())
        {
            throw new FailedResponseException(Status.UNAUTHORIZED,
                "The message has already been sent.");
        }
        else
        {
            verifyIsMessageCreator(message, username, computer);
        }
    }
    
    /**
     * This method checks to make sure that the userid/username specified is
     * allowed read access to the message specified. If the message has not been
     * sent, then this user must be the creator, or sender, of the message. If
     * the message has been sent, then this user must be one of the message's
     * recipients. If any of these validations fail, a FailedResponseException
     * with status UNAUTHORIZED is thrown. This method should only be called for
     * userids/usernames of this realm server; an exception will be thrown if
     * this is not the case.
     * 
     * @param message
     *            The id of the message to check
     * @param username
     *            The userid or username to check
     */
    protected static void verifyCanReadMessage(Message message, String username,
        String computer)
    {
        if (message == null)
            throw new FailedResponseException(Status.NOSUCHMESSAGE, "");
        username = resolveToUserid(username);
        if (message.isSent())
        {
            verifyIsMessageRecipient(message, username, computer);
        }
        else
        {
            verifyIsMessageCreator(message, username, computer);
        }
    }
    
    private static void verifyIsMessageCreator(Message message, String username,
        String computer)
    {
        if (message == null)
            throw new FailedResponseException(Status.NOSUCHMESSAGE, "");
        username = resolveToUserid(username);
        boolean isMessageCreator =
            username.equalsIgnoreCase(message.getSender())
                && computer.equalsIgnoreCase(message.getComputer());
        if (!isMessageCreator)
        {
            throw new FailedResponseException(Status.UNAUTHORIZED,
                "This user is not the message's creator.");
        }
    }
    
    private static void verifyIsMessageRecipient(Message message, String username,
        String computer)
    {
        if (message == null)
            throw new FailedResponseException(Status.NOSUCHMESSAGE, "");
        username = resolveToUserid(username);
        try
        {
            boolean isMessageRecipient =
                DataStore.isMessageRecipient(new MessageRecipient(message.getId(), username,
                    computer)) > 0;
            if (!isMessageRecipient)
                throw new FailedResponseException(Status.UNAUTHORIZED,
                    "This user is not one of the message's recipients.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new FailedResponseException(Status.FAIL,
                "An internal server error occured.");
        }
    }
    
    /**
     * If the input is a userid, returns it. If the input is a username, returns
     * a userid made up of this server's realm and the input.
     * 
     * @param string
     * @return
     */
    protected static String resolveToUserid(String string)
    {
        return Userids.resolveTo(string, Userids.toUserid(serverRealmAddress, ""));
    }
    
    /**
     * Throws a FailedResponseException with {@link Status#INVALIDREALM} if the
     * input specified is not a username (in othewords, userids are not
     * allowed). This is currently used to validate that all users specified in
     * arguments to commands are of this realm server, and this method will
     * likely be removed when full support for realm servers and inter-realm
     * communication is added.
     * 
     * @param recipientUser
     */
    protected static void checkUsername(String recipientUser)
    {
        if (recipientUser.equals(""))
            return;
        if (!Userids.isUsername(recipientUser))
            throw new FailedResponseException(Status.INVALIDREALM, "The input \""
                + recipientUser + "\" was supposed to be a username, but it wasn't.");
    }
    
    /**
     * If the string is a userid of this realm, converts it to a username. If
     * it's already a username, returns it. If it's a userid of a different
     * realm, returns it.
     * 
     * @param recipientUser
     * @return
     */
    protected static String relativeId(String recipientUser)
    {
        if (recipientUser.equals(""))
            return "";
        return Userids.relativeTo(recipientUser, serverRealmAddress);
    }
    
    public static <T> String delimited(T[] items, ToString<T> generator, String delimiter)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++)
        {
            if (i != 0)
                sb.append(delimiter);
            sb.append(generator.toString(items[i]));
        }
        return sb.toString();
    }
    
    /**
     * shorthand for tokenizeByLine(newString(readToBytes(data)))
     * 
     * @param data
     *            The data to read. This will be read until the end of the
     *            stream.
     * @return An array of strings, each string being a line of text taken from
     *         the input stream passed in
     * @throws IOException
     */
    public static String[] tokenize(InputStream data) throws IOException
    {
        return tokenizeByLines(new String(readToBytes(data)));
    }
    
    /**
     * concatenates a bunch of byte arrays together.
     * 
     * @param bytes
     * @return
     */
    public static byte[] concat(byte[]... bytes)
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
            System.arraycopy(cb, 0, result, pointer, cb.length);
            pointer += cb.length;
        }
        return result;
    }
    
    protected static void runLongSql(String sql, Connection con) throws SQLException
    {
        String[] statements = sql.split("\\;");
        int i = 1;
        for (String s : statements)
        {
            System.out.println("Running statement " + i++ + " of " + statements.length);
            if (!s.trim().equals(""))
            {
                PreparedStatement st = con.prepareStatement(s);
                st.execute();
                st.close();
            }
        }
    }
    
    /**
     * reads the file specified in to a string. the file must not be larger than
     * 5 MB.
     * 
     * @param file.
     * @return
     */
    public static String readFile(File file)
    {
        try
        {
            if (file.length() > (5 * 1000 * 1000))
                throw new RuntimeException("the file is " + file.length()
                    + " bytes. that is too large. it can't be larger than 5000000 bytes.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            copy(fis, baos);
            fis.close();
            baos.flush();
            baos.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void writeFile(String string, File file)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(file);
            copy(bais, fos);
            bais.close();
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
    }
    
    private static Context createServerContext(Server server, String webroot)
    {
        Context context = new Context(server, "/", Context.SESSIONS);
        context.setResourceBase(webroot);
        return context;
    }
    
    private static void finishContext(Context context)
    {
        ServletHolder jsp = new ServletHolder(new JspServlet());
        jsp.setInitParameter("classpath", "classes;lib/*");
        jsp.setInitParameter("scratchdir", "classes");
        context.addServlet(jsp, "*.jsp");
        ServletHolder resource = new ServletHolder(new DefaultServlet());
        context.addServlet(resource, "/");
    }
    
    public static void sendUserNotifications(String to, String subject, String message,
        String priority, final int dismissMinutes) throws NotificationException
    {
        final Date start = new Date();
        final Date end =
            new Date(start.getTime() + TimeUnit.MINUTES.toMillis(dismissMinutes));
        final String contents =
            "" + start.getTime() + " " + start + "\n" + end.getTime() + " " + end + "\n"
                + priority + "\n" + subject + "\n" + message;
        final ConnectionHandler[] recipientConnections;
        if (to.equalsIgnoreCase("all"))
        {
            recipientConnections = connections.toArray(new ConnectionHandler[0]);
        }
        else if (to.startsWith("user:"))
        {
            recipientConnections = getConnectionsForUser(to.substring("user:".length()));
            if (recipientConnections.length == 0)
                throw new NotificationException("The user specified is not online");
        }
        else if (to.startsWith("computer:"))
        {
            String[] tokens = to.split("\\/", 2);
            if (tokens.length < 2)
                throw new NotificationException("Invalid target user spec");
            ConnectionHandler computerConnection =
                getConnectionForComputer(tokens[0], tokens[1]);
            if (computerConnection == null)
                throw new NotificationException("The computer specified is not online");
            recipientConnections = new ConnectionHandler[] { computerConnection };
        }
        else
            throw new NotificationException("Invalid scope specified");
        tasks.execute(new Runnable()
        {
            public void run()
            {
                for (ConnectionHandler handler : recipientConnections)
                {
                    try
                    {
                        handler.sendEncryptedPacket(generateId(), "usernotification",
                            Status.OK, contents);
                    }
                    catch (Exception ex1)
                    {
                        ex1.printStackTrace();
                    }
                }
            }
        });
    }
    
    private static String getConfig(String key) throws SQLException
    {
        PreparedStatement st =
            pdb.prepareStatement("select value from " + pfix
                + "configuration where name = ?");
        st.setString(1, key);
        ResultSet rs = st.executeQuery();
        String value = null;
        if (rs.next())
            value = rs.getString("value");
        st.close();
        return value;
    }
    
    private static void setConfig(String key, String value) throws SQLException
    {
        PreparedStatement st;
        if (getConfig(key) == null)
        {
            st =
                pdb.prepareStatement("insert into " + pfix + "configuration "
                    + "(name,value) values (?,?)");
            st.setString(1, key);
            st.setString(2, value);
        }
        else
        {
            st =
                pdb.prepareStatement("update " + lfix + "configuration set value = ?"
                    + " where name = ?");
            st.setString(1, value);
            st.setString(2, key);
        }
        st.executeUpdate();
        st.close();
    }
    
    private static long nextId;
    public static X509Certificate ogcaCert;
    
    public static synchronized String generateId()
    {
        return "p" + System.currentTimeMillis() + "uid" + nextId++;
    }
    
}
