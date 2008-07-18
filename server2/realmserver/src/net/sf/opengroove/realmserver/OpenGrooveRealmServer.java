package net.sf.opengroove.realmserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.servlet.JspServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import DE.knp.MicroCrypt.Aes256;
import DE.knp.MicroCrypt.Sha512;

import nanohttpd.NanoHTTPD;
import nanohttpd.NanoHTTPD.Response;
import net.sf.opengroove.realmserver.data.model.Computer;
import net.sf.opengroove.realmserver.data.model.User;
import net.sf.opengroove.realmserver.data.model.UserSetting;
import net.sf.opengroove.realmserver.web.LoginFilter;
import net.sf.opengroove.realmserver.web.RendererServlet;
import net.sf.opengroove.security.Crypto;
import net.sf.opengroove.security.Hash;
import net.sf.opengroove.security.RSA;
import nl.captcha.servlet.DefaultCaptchaIml;

public class OpenGrooveRealmServer
{
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
    // TODO:
    /**
     * This class is a Runnable designed to be added to the task thread queue.
     * It sends notifications to all users that have subscriptions for this
     * user's status (or this computer's status), indicating that the user's (or
     * computer's) status has changed.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class UserStatusNotifier implements
        Runnable
    {
        private String username;
        private String computer;
        
        public UserStatusNotifier(String username,
            String computer)
        {
            this.username = username;
            this.computer = computer;
        }
        
        @Override
        public void run()
        {
            
        }
        
    }
    
    public static final SecureRandom random = new SecureRandom();
    
    public static abstract class Command
    {
        private int mps;
        private boolean whenUnauth;
        private boolean whenNoComputer;
        private String commandName;
        
        public Command(String commandName,
            int maxPacketSize, boolean whenUnauth,
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
        
        public abstract void handle(String packetId,
            InputStream data, ConnectionHandler connection)
            throws Exception;
    }
    
    private static Map<String, Map<String, ConnectionHandler>> connectionsByAuth = new Hashtable<String, Map<String, ConnectionHandler>>();
    
    protected static final File HTTPD_RES_FOLDER = new File(
        "httpdres");
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
    
    private static final File configFile = new File(
        "config.properties");
    
    private static boolean setupStillRunning = true;
    
    private static boolean setupStillAllowed = true;
    
    private static Properties config = new Properties();
    
    protected static boolean doneSettingUp = false;
    public static ServerSocket serverSocket;
    private static ArrayList<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();
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
        for (ConnectionHandler c : new ArrayList<ConnectionHandler>(
            connections))
        {
            if (c.username != null)
                i++;
        }
        return i;
    }
    
    public static int getNumComputerAuthConnections()
    {
        int i = 0;
        for (ConnectionHandler c : new ArrayList<ConnectionHandler>(
            connections))
        {
            if (c.username != null
                && c.computerName != null)
                i++;
        }
        return i;
    }
    
    public static final ScheduledThreadPoolExecutor internalTasks = new ScheduledThreadPoolExecutor(
        15);
    
    public static final ThreadPoolExecutor tasks = new ThreadPoolExecutor(
        100, 400, 20, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(3000));
    
    protected static final byte[] EMPTY = new byte[0];
    
    private static final int MAX_CONNECTIONS = 300;
    
    public static class TimedInputStream extends
        FilterInputStream
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
        public int read(byte[] b, int off, int len)
            throws IOException
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
    
    public static class TimedOutputStream extends
        FilterOutputStream
    {
        @Override
        public void write(byte[] b, int off, int len)
            throws IOException
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
    
    public static class ConnectionHandler extends Thread
    {
        private Socket socket;
        private InputStream internalInputStream;
        private TimedInputStream in;
        private OutputStream internalOutputStream;
        private TimedOutputStream out;
        private PacketSpooler spooler;
        public int allowedIdleMilliseconds = 10000;
        private byte[] securityKeyBytes;
        public Aes256 securityKey;
        private boolean completedHandshake;
        public String username;
        public String computerName;
        
        public long getLastInTime()
        {
            return in.getLastTime();
        }
        
        public long getLastOutTime()
        {
            return out.getLastTime();
        }
        
        public ConnectionHandler(Socket socket)
            throws IOException
        {
            this.socket = socket;
            internalInputStream = socket.getInputStream();
            in = new TimedInputStream(internalInputStream);
            internalOutputStream = socket.getOutputStream();
            out = new TimedOutputStream(
                internalOutputStream);
        }
        
        /**
         * sends a packet to this connection's client. If a security handshake
         * has not occured yet, an IllegalStateException is thrown.
         * 
         * @throws IOException
         */
        public boolean sendPacketTo(Packet packet)
            throws IOException
        {
            if (!completedHandshake)
                throw new IllegalStateException(
                    "The handshake has not yet completed.");
            return spooler.send(packet);
        }
        
        public boolean sendEncryptedPacket(String id,
            String command, String responseStatus,
            byte[] message)
        {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(id.getBytes());
                baos.write(' ');
                baos.write(command.getBytes());
                baos.write(' ');
                baos.write(responseStatus.getBytes());
                baos.write(' ');
                baos.write(message);
                return sendEncryptedPacket(baos
                    .toByteArray());
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
                Crypto.enc(securityKey, packet, baos);
                return sendPacketTo(new Packet(
                    new ByteArrayInputStream(baos
                        .toByteArray())));
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
                for (int i = 0; i < 30; i++)
                {
                    int read = in.read();
                    s += (char) read;
                    if ((read == '\r' || read == '\n')
                        && i != 0)
                        break;
                    if (i == 29)
                        throw new ProtocolMismatchException(
                            "too much initialization data sent by the client");
                }
                s = s.trim();
                if (!s.equalsIgnoreCase("OpenGroove"))
                {
                    // Incorrect connection, probably an HTTP client or
                    // something
                    throw new ProtocolMismatchException(
                        "Invalid header string sent by client");
                }
                // Ok, correct header, now we send back our header
                out
                    .write("OpenGrooveServer\r\n"
                        .getBytes());
                out.flush();
                // The next thing coming from the client should be the hexcoded
                // security key (possibly preceded by a newline, since the code
                // that
                // read the header doesn't check for a newline if it receives a
                // carriage return)
                s = "";
                for (int i = 0; i < 1024; i++)
                {
                    int read = in.read();
                    s += (char) read;
                    if ((read == '\r' || read == '\n')
                        && i != 0)
                        break;
                    if (i == 1023)
                        throw new ProtocolMismatchException(
                            "too much initialization data sent by the client");
                }
                s = s.trim();
                // this should be the aes-256 key to use, encoded using rsa.
                // Only the first 32 bytes of the decoded key are significant
                // and the rest are just random garbage used as secure padding.
                BigInteger keyEncPub = new BigInteger(s, 16);
                BigInteger keyDecrypted = RSA.decrypt(
                    rsaEncryptionPrivateKey,
                    rsaEncryptionModulus, keyEncPub);
                byte[] keyWithPadding = keyDecrypted
                    .toByteArray();
                securityKeyBytes = new byte[32];
                System.arraycopy(keyWithPadding, 0,
                    securityKeyBytes, 0, 32);
                System.out.println("using aes key "
                    + Hash.hexcode(securityKeyBytes));
                securityKey = new Aes256(securityKeyBytes);
                // we now have the aes-256 security key. Now the client will
                // send us a random number encrypted with the server's rsa
                // public key, which we must decrypt using our private key and
                // send back encrypted with aes.
                s = "";
                for (int i = 0; i < 1024; i++)
                {
                    int read = in.read();
                    s += (char) read;
                    if ((read == '\r' || read == '\n')
                        && i != 0)
                        break;
                    if (i == 1023)
                        throw new ProtocolMismatchException(
                            "too much initialization data sent by the client");
                }
                s = s.trim();
                BigInteger challengeRandomEncInteger = new BigInteger(
                    s, 16);
                System.out
                    .println("challengerandomencinteger "
                        + challengeRandomEncInteger
                            .toString(16));
                BigInteger challengeRandomInteger = RSA
                    .decrypt(rsaEncryptionPrivateKey,
                        rsaEncryptionModulus,
                        challengeRandomEncInteger);
                System.out
                    .println("challengerandominteger "
                        + challengeRandomInteger
                            .toString(16));
                byte[] challengeRandomBytes = new byte[16];
                System.arraycopy(challengeRandomInteger
                    .toByteArray(), 0,
                    challengeRandomBytes, 0, 16);
                System.out
                    .println("received as check bytes "
                        + Hash
                            .hexcode(challengeRandomBytes));
                byte[] challengeRandomAes = new byte[16];
                securityKey.encrypt(challengeRandomBytes,
                    0, challengeRandomAes, 0);
                BigInteger challengeRandomAesInteger = new BigInteger(
                    challengeRandomAes);
                System.out
                    .println("sending as check response "
                        + challengeRandomAesInteger
                            .toString(16));
                out.write((""
                    + challengeRandomAesInteger
                        .toString(16) + "\r\n").getBytes());
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
                // Now we send a random number, encrypted using aes-256, to the
                // client, using the Crypto packet notation instead of hexcoded
                // newline-separated notation. The client should reply with the
                // number, but hashed, and encoded with aes-256. This is used to
                // prevent against replay attacks.
                byte[] randomBytes = new byte[16];
                random.nextBytes(randomBytes);
                Crypto.enc(securityKey, randomBytes, out);
                System.out.println("sent antireplay "
                    + Hash.hexcode(randomBytes));
                byte[] randomHashBytes = Crypto.dec(
                    securityKey, in, 200);
                String randomHashString = Hash
                    .hash(randomBytes);
                byte[] randomHash = randomHashString
                    .getBytes();
                System.out.println("hash received "
                    + Hash.hexcode(randomHashBytes)
                    + " and correct "
                    + Hash.hexcode(randomHash));
                if (!Arrays.equals(randomHashBytes,
                    randomHash))
                {
                    throw new ProtocolMismatchException(
                        "Incorrect random hash received");
                }
                // The handshake is now complete. Now we start listening for
                // packets (using Crypto.dec), and deal with them accordingly.
                // Keep attempting to receive packets until an exception is
                // thrown. (when the quit command is called, the command class
                // closes the connection, so an exception will be thrown on the
                // next read)
                completedHandshake = true;
                while ((!socket.isClosed())
                    && (!socket.isInputShutdown())
                    && (!socket.isOutputShutdown()))
                {
                    byte[] packet = Crypto.dec(securityKey,
                        in, 65535);
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
                System.err
                    .println("Connection handler closing due to exception, "
                        + "stack trace follows");
                e.printStackTrace();
            }
            finally
            {
                connections.remove(this);
                try
                {
                    if (username != null
                        && computerName != null)
                    {
                        Map userMap = connectionsByAuth
                            .get(username);
                        if (userMap != null)
                        {
                            userMap.remove(computerName);
                            if (userMap.size() == 0)
                                connectionsByAuth
                                    .remove(username);
                        }
                        tasks
                            .execute(new UserStatusNotifier(
                                this.username,
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
        
        public void processIncomingPacket(byte[] packet)
            throws Exception
        {
            System.out
                .println("dealing with packet of length "
                    + packet.length);
            String packetId = "UNKNOWN";
            String commandName = "UNKNOWN";
            try
            {
                byte[] first128bytes = new byte[Math.min(
                    128, packet.length)];
                System.arraycopy(packet, 0, first128bytes,
                    0, first128bytes.length);
                String first128 = new String(first128bytes);
                String[] first128split = first128.split(
                    "\\ ", 3);
                if (first128split.length < 3)
                    throw new FailedResponseException(
                        "FAIL",
                        "no command input (packets should be of"
                            + " the form packetId commandName arguments)");
                packetId = first128split[0];
                commandName = first128split[1];
                int startDataIndex = packetId.length()
                    + commandName.length() + 2;
                ByteArrayInputStream data = new ByteArrayInputStream(
                    packet, startDataIndex, packet.length
                        - (startDataIndex));
                // In the future, the packet could be cached to the file system
                // if
                // it's larger than, say, 2048 bytes, to avoid memory errors
                Command command = commands.get(commandName
                    .toLowerCase());
                if (command == null)
                    throw new FailedResponseException(
                        "The command specified is not a valid command");
                if (username == null
                    && !command.whenUnauth())
                    throw new FailedResponseException(
                        "You must run the authenticate command before this one.");
                if (computerName == null
                    && !command.whenNoComputer())
                    throw new FailedResponseException(
                        "This command can only be run when authenticated as a computer.");
                command.handle(packetId, data, this);
            }
            catch (FailedResponseException e)
            {
                sendEncryptedPacket(
                    packetId,
                    commandName,
                    e.getStatus(),
                    e.getMessage() == null ? "An error occured while processing this command."
                        .getBytes()
                        : e.getMessage().getBytes());
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
            this.queue = new LinkedBlockingQueue<Packet>(
                queueSize);
        }
        
        public synchronized boolean send(Packet packet)
        {
            return queue.offer(packet);
        }
        
        private boolean closed = false;
        
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
                    synchronized (this)
                    {
                        copy(packet.getStream(), out);
                    }
                }
            }
            catch (Exception e)
            {
                if (closed)
                {
                    System.out
                        .println("Closed packet spooler with exception");
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
        System.out.println("OpenGroove Realm Server");
        System.out.println("www.opengroove.org");
        System.out.println("Initializing...");
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
            final Context context = createServerContext(
                server, "webinit");
            context.addFilter(new FilterHolder(new Filter()
            {
                
                @Override
                public void destroy()
                {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public synchronized void doFilter(
                    ServletRequest sRequest,
                    ServletResponse sResponse,
                    FilterChain chain) throws IOException,
                    ServletException
                {
                    HttpServletRequest request = (HttpServletRequest) sRequest;
                    HttpServletResponse response = (HttpServletResponse) sResponse;
                    if (request.getRequestURI().startsWith(
                        "/bypass/"))
                    {
                        chain.doFilter(request, response);
                        return;
                    }
                    else if (doneSettingUp)
                    {
                        response
                            .sendRedirect("/bypass/done.jsp");
                        return;
                    }
                    else if (request.getRequestURI()
                        .equals("/setup"))
                    {
                        // build url containing all of the parameters in case
                        // the user mis-entered something
                        String redoUrl = "/bypass/start.jsp?";
                        for (String param : (Collection<String>) Collections
                            .list(request
                                .getParameterNames()))
                        {
                            for (String value : request
                                .getParameterValues(param))
                            {
                                redoUrl += ""
                                    + URLEncoder
                                        .encode(param)
                                    + "="
                                    + URLEncoder
                                        .encode(value)
                                    + "&";
                            }
                        }
                        redoUrl += "errormessage=";
                        // load parameters into variables
                        String username = request
                            .getParameter("username");
                        String password = request
                            .getParameter("password");
                        String passwordagain = request
                            .getParameter("passwordagain");
                        String pdbclass = request
                            .getParameter("pdbclass");
                        config.setProperty("pdbclass",
                            pdbclass);
                        String pdburl = request
                            .getParameter("pdburl");
                        config
                            .setProperty("pdburl", pdburl);
                        String pdbprefix = request
                            .getParameter("pdbprefix");
                        config.setProperty("pdbprefix",
                            pdbprefix);
                        String pdbusername = request
                            .getParameter("pdbusername");
                        config.setProperty("pdbusername",
                            pdbusername);
                        String pdbpassword = request
                            .getParameter("pdbpassword");
                        config.setProperty("pdbpassword",
                            pdbpassword);
                        String ldbclass = request
                            .getParameter("ldbclass");
                        config.setProperty("ldbclass",
                            ldbclass);
                        String ldburl = request
                            .getParameter("ldburl");
                        config
                            .setProperty("ldburl", ldburl);
                        String ldbprefix = request
                            .getParameter("ldbprefix");
                        config.setProperty("ldbprefix",
                            ldbprefix);
                        String ldbusername = request
                            .getParameter("ldbusername");
                        config.setProperty("ldbusername",
                            ldbusername);
                        String ldbpassword = request
                            .getParameter("ldbpassword");
                        config.setProperty("ldbpassword",
                            ldbpassword);
                        String serverport = request
                            .getParameter("serverport");
                        String webport = request
                            .getParameter("webport");
                        context.setAttribute("serverport",
                            webport);
                        String serverhostname = request
                            .getParameter("serverhostname");
                        boolean forceEncryption = request
                            .getParameter("forceencryption") != null;
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
                            setuperror(
                                redoUrl,
                                response,
                                "The password you entered for your "
                                    + "web administration password "
                                    + "isn't long enough. The password needs to be "
                                    + "at least 5 characters long.");
                            return;
                        }
                        if (username.length() < 1)
                        {
                            setuperror(
                                redoUrl,
                                response,
                                "The username you entered for your"
                                    + "web administration username "
                                    + "isn't long enough. The username needs to be"
                                    + " at least 1 character long.");
                            return;
                        }
                        // create connections to the persistant and large
                        // databases, and test them out
                        System.out
                            .println("connecting to persistant database...");
                        pfix = pdbprefix;
                        lfix = ldbprefix;
                        try
                        {
                            Class.forName(pdbclass);
                            pdb = DriverManager
                                .getConnection(pdburl,
                                    pdbusername,
                                    pdbpassword);
                        }
                        catch (Exception e)
                        {
                            StringWriter sw = new StringWriter();
                            e
                                .printStackTrace(new PrintWriter(
                                    sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured when trying to initialize"
                                    + " the persistant database. Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString()
                                    + "</pre>");
                            return;
                        }
                        System.out
                            .println("connecting to large database...");
                        try
                        {
                            Class.forName(ldbclass);
                            ldb = DriverManager
                                .getConnection(ldburl,
                                    ldbusername,
                                    ldbpassword);
                        }
                        catch (Exception e)
                        {
                            StringWriter sw = new StringWriter();
                            e
                                .printStackTrace(new PrintWriter(
                                    sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured when trying to initialize"
                                    + " the persistant database. Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString()
                                    + "</pre>");
                            return;
                        }
                        System.out
                            .println("loading sql files for table creation...");
                        // create the tables
                        String psql = readFile(new File(
                            "pinit.sql"));
                        String lsql = readFile(new File(
                            "linit.sql"));
                        psql = psql.replace("$$prefix$$",
                            pfix);
                        lsql = lsql.replace("$$prefix$$",
                            lfix);
                        System.out
                            .println("creating persistant tables...");
                        try
                        {
                            runLongSql(psql, pdb);
                        }
                        catch (SQLException e)
                        {
                            StringWriter sw = new StringWriter();
                            e
                                .printStackTrace(new PrintWriter(
                                    sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured when trying to initialize"
                                    + " the persistant database. Some data may have"
                                    + "already been inserted into the database. "
                                    + "Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString()
                                    + "</pre>");
                            return;
                        }
                        // TODO: what if it fails in the middle of creating
                        // tables? should we try to roll back and delete those
                        // tables? perhaps put the table creates all within one
                        // transaction?
                        System.out
                            .println("creating large tables...");
                        try
                        {
                            runLongSql(lsql, ldb);
                        }
                        catch (SQLException e)
                        {
                            StringWriter sw = new StringWriter();
                            e
                                .printStackTrace(new PrintWriter(
                                    sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured when trying to initialize"
                                    + " the large database. Some data may have"
                                    + "already been inserted into the database. "
                                    + "Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString()
                                    + "</pre>");
                            return;
                        }
                        // store the configuration settings in the tables
                        System.out
                            .println("setting configuration settings...");
                        try
                        {
                            setConfig("serverport",
                                serverport);
                            setConfig("webport", webport);
                            setConfig("serverhostname",
                                serverhostname);
                            setConfig("forceencryption",
                                forceEncryption ? "true"
                                    : "false");
                            PreparedStatement st = pdb
                                .prepareStatement("insert into "
                                    + pfix
                                    + "webusers (username,role,password)"
                                    + " values (?,?,?)");
                            st.setString(1, username);
                            st.setString(2, "admin");
                            st.setString(3, Hash
                                .hash(password));
                            st.executeUpdate();
                            st.close();
                        }
                        catch (SQLException e)
                        {
                            StringWriter sw = new StringWriter();
                            e
                                .printStackTrace(new PrintWriter(
                                    sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured while setting up the server's initial"
                                    + "configuration. Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString()
                                    + "</pre>");
                            return;
                        }
                        // generate the RSA keys for the server
                        try
                        {
                            System.out
                                .println("generating rsa security keys for encryption...");
                            RSA rsaEnc = new RSA(3072);
                            setConfig("rsa-enc-pub", rsaEnc
                                .getPublicKey()
                                .toString(16));
                            setConfig("rsa-enc-prv", rsaEnc
                                .getPrivateKey().toString(
                                    16));
                            setConfig("rsa-enc-mod", rsaEnc
                                .getModulus().toString(16));
                            System.out
                                .println("generating rsa security keys for signing...");
                            RSA rsaSgn = new RSA(3072);
                            setConfig("rsa-sgn-pub", rsaSgn
                                .getPublicKey()
                                .toString(16));
                            setConfig("rsa-sgn-prv", rsaSgn
                                .getPrivateKey().toString(
                                    16));
                            setConfig("rsa-sgn-mod", rsaSgn
                                .getModulus().toString(16));
                        }
                        catch (Exception e)
                        {
                            StringWriter sw = new StringWriter();
                            e
                                .printStackTrace(new PrintWriter(
                                    sw));
                            setuperror(
                                redoUrl,
                                response,
                                "An error occured while generating RSA security keys"
                                    + "for the server. Here's the stack trace:<br/><br/><pre>"
                                    + sw.toString()
                                    + "</pre>");
                            return;
                        }
                        config.store(new FileOutputStream(
                            configFile), "");
                        // We're done!
                        System.out
                            .println("Server configuration complete.");
                        doneSettingUp = true;
                        response.sendRedirect("/");
                        return;
                    }
                    else
                    {
                        response
                            .sendRedirect("/bypass/start.jsp?pdbclass=org.h2.Driver&"
                                + "pdburl="
                                + URLEncoder
                                    .encode("jdbc:h2:appdata/dbp/persistant")
                                + "&"
                                + "pdbprefix=opengroove_&pdbusername=sa"
                                + "&ldbclass=org.h2.Driver&"
                                + "ldburl="
                                + URLEncoder
                                    .encode("jdbc:h2:appdata/dbl/large")
                                + "&"
                                + "ldbprefix=opengroove_&ldbusername=sa"
                                + "&serverport=63745&webport=34567");
                        return;
                    }
                }
                
                private void setuperror(String redoUrl,
                    HttpServletResponse response,
                    String string) throws IOException
                {
                    response.sendRedirect(redoUrl
                        + URLEncoder.encode(string));
                }
                
                @Override
                public void init(FilterConfig filterConfig)
                    throws ServletException
                {
                    // TODO Auto-generated method stub
                    
                }
            }), "/*", Context.ALL);
            finishContext(context);
            server.start();
            Thread.sleep(200);
            System.out
                .println(""
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
        System.out
            .println("loading configuration files...");
        config.load(new FileInputStream(configFile));
        String pdbclass = config.getProperty("pdbclass");
        String pdburl = config.getProperty("pdburl");
        String pdbprefix = config.getProperty("pdbprefix");
        String pdbusername = config
            .getProperty("pdbusername");
        String pdbpassword = config
            .getProperty("pdbpassword");
        String ldbclass = config.getProperty("ldbclass");
        String ldburl = config.getProperty("ldburl");
        String ldbprefix = config.getProperty("ldbprefix");
        String ldbusername = config
            .getProperty("ldbusername");
        String ldbpassword = config
            .getProperty("ldbpassword");
        pfix = pdbprefix;
        lfix = ldbprefix;
        System.out
            .println("loading database template files...");
        // copy persistantsqlmap.xml and largesqlmap.xml to the classes folder
        // with $$prefix$$ replaced as necessary
        String psqlmaptext = readFile(new File(
            "persistantsqlmap.xml"));
        String lsqlmaptext = readFile(new File(
            "largesqlmap.xml"));
        psqlmaptext = psqlmaptext.replace("$$prefix$$",
            pfix);
        lsqlmaptext = lsqlmaptext.replace("$$prefix$$",
            lfix);
        writeFile(psqlmaptext, new File(
            "classes/persistantsqlmap.xml"));
        writeFile(lsqlmaptext, new File(
            "classes/largesqlmap.xml"));
        System.out
            .println("connecting to persistant database...");
        Class.forName(pdbclass);
        pfix = pdbprefix;
        pdb = DriverManager.getConnection(pdburl,
            pdbusername, pdbpassword);
        String psqlconfigtext = readFile(new File(
            "persistantsql.xml"));
        psqlconfigtext = psqlconfigtext.replace(
            "$$driver$$", pdbclass);
        psqlconfigtext = psqlconfigtext.replace("$$url$$",
            pdburl);
        psqlconfigtext = psqlconfigtext.replace(
            "$$username$$", pdbusername);
        psqlconfigtext = psqlconfigtext.replace(
            "$$password$$", pdbpassword);
        pdbclient = SqlMapClientBuilder
            .buildSqlMapClient(new StringReader(
                psqlconfigtext));
        System.out
            .println("connecting to large database...");
        Class.forName(ldbclass);
        lfix = ldbprefix;
        ldb = DriverManager.getConnection(ldburl,
            ldbusername, ldbpassword);
        String lsqlconfigtext = readFile(new File(
            "largesql.xml"));
        lsqlconfigtext = lsqlconfigtext.replace(
            "$$driver$$", ldbclass);
        lsqlconfigtext = lsqlconfigtext.replace("$$url$$",
            ldburl);
        lsqlconfigtext = lsqlconfigtext.replace(
            "$$username$$", ldbusername);
        lsqlconfigtext = lsqlconfigtext.replace(
            "$$password$$", ldbpassword);
        ldbclient = SqlMapClientBuilder
            .buildSqlMapClient(new StringReader(
                lsqlconfigtext));
        System.out.println("loading web server...");
        Server server = new Server(Integer
            .parseInt(getConfig("webport")));
        Context context = createServerContext(server, "web");
        context.addFilter(new FilterHolder(
            new LoginFilter()), "/*", Context.ALL);
        context.addServlet(new ServletHolder(
            new RendererServlet(readFile(new File(
                "webconfig/layout.properties")))),
            "/layout/*");
        context.addServlet(new ServletHolder(
            new HttpServlet()
            {
                
                @Override
                protected void service(
                    HttpServletRequest req,
                    HttpServletResponse resp)
                    throws ServletException, IOException
                {
                    resp.setHeader("Content-type",
                        "application/octet-stream");
                    req.getRequestDispatcher(
                        "/keydownload.jsp").forward(req,
                        resp);
                }
            }), "/serverkey.ogvs");
        finishContext(context);
        server.start();
        Thread.sleep(300);// so that stdout and stderr don't get mixed up
        System.out.println("loading periodic tasks...");
        loadPeriodicTasks();
        System.out
            .println("loading OpenGroove server on port "
                + getConfig("serverport") + "...");
        rsaEncryptionPublicKey = new BigInteger(
            getConfig("rsa-enc-pub"), 16);
        rsaEncryptionModulus = new BigInteger(
            getConfig("rsa-enc-mod"), 16);
        rsaEncryptionPrivateKey = new BigInteger(
            getConfig("rsa-enc-prv"), 16);
        rsaSignaturePublicKey = new BigInteger(
            getConfig("rsa-sgn-pub"), 16);
        rsaSignatureModulus = new BigInteger(
            getConfig("rsa-sgn-mod"), 16);
        rsaSignaturePrivateKey = new BigInteger(
            getConfig("rsa-sgn-prv"), 16);
        serverSocket = new ServerSocket(Integer
            .parseInt(getConfig("serverport")));
        loadCommands();
        tasks.prestartAllCoreThreads();
        internalTasks.prestartAllCoreThreads();
        System.out
            .println("OpenGroove Realm Server is up and running.");
        while (!serverSocket.isClosed())
        {
            try
            {
                Socket socket = serverSocket.accept();
                if (connections.size() > MAX_CONNECTIONS)
                {
                    socket.close();
                    continue;
                }
                ConnectionHandler c = new ConnectionHandler(
                    socket);
                connections.add(c);
                c.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Thread.sleep(20);
            }
        }
        System.out
            .println("OpenGroove Realm Server is shutting down...");
        System.out.println("Shutting down task pool...");
        tasks.shutdown();
        for (int i = 0; i < 6; i++)
        {
            if (tasks.awaitTermination(5, TimeUnit.SECONDS))
            {
                break;
            }
            System.out.println("waited " + ((i + 1) * 5)
                + " seconds, waiting "
                + ((6 * 5) - ((i + 1) * 5))
                + " more seconds");
            if (i == 5)
                System.out
                    .println("Forcing shutdown of task pool...");
        }
        tasks.shutdownNow();
        System.out.println("Disconnecting all users...");
        System.out
            .println("Shutting down internal pool...");
        internalTasks.shutdown();
        for (int i = 0; i < 6; i++)
        {
            if (internalTasks.awaitTermination(5,
                TimeUnit.SECONDS))
            {
                break;
            }
            System.out.println("waited " + ((i + 1) * 5)
                + " seconds, waiting "
                + ((6 * 5) - ((i + 1) * 5))
                + " more seconds");
            if (i == 5)
                System.out
                    .println("Forcing shutdown of internal pool...");
        }
        internalTasks.shutdownNow();
        System.out
            .println("Closing connection to persistant database...");
        pdb.close();
        System.out
            .println("Closing connection to large database...");
        ldb.close();
        System.out
            .println("OpenGroove Realm Server has successfully shut down.");
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
                        if (handler.socket.isClosed()
                            || handler.socket
                                .isInputShutdown()
                            || handler.socket
                                .isOutputShutdown()
                            || (!handler.socket
                                .isConnected()))
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
        }, 30, 300, TimeUnit.SECONDS);
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
    }
    
    private static ConnectionHandler getConnectionForComputer(
        String username, String computer)
    {
        username = username.toLowerCase();
        computer = computer.toLowerCase();
        Map<String, ConnectionHandler> userMap = connectionsByAuth
            .get(username);
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
        return handler;
    }
    
    public static String[] tokenizeByLines(String data)
    {
        BufferedReader reader = new BufferedReader(
            new StringReader(data));
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
    
    private static void verifyAtLeast(Object[] objects,
        int minLength)
    {
        if (objects.length < minLength)
            throw new FailedResponseException("FAIL",
                "Input too short (expected " + minLength
                    + ", found " + objects.length + ")");
    }
    
    private static byte[] readToBytes(InputStream input)
        throws IOException
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
            public synchronized void handle(
                String packetId, InputStream data,
                ConnectionHandler connection)
                throws IOException, SQLException
            {
                if (connection.username != null)
                {
                    throw new FailedResponseException(
                        "You're already authenticated");
                }
                String[] tokens = tokenizeByLines(new String(
                    readToBytes(data)));
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
                if (connectionType
                    .equalsIgnoreCase("normal"))
                {
                    if ((!computerName.equals(""))
                        && getConnectionForComputer(
                            username, computerName) != null)
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
                        for (ConnectionHandler ich : new ArrayList<ConnectionHandler>(
                            connections))
                        {
                            if (ich.username != null
                                && ich.username
                                    .equalsIgnoreCase(username)
                                && (ich.computerName == null || ich.computerName
                                    .equals("")))
                            {
                                throw new FailedResponseException(
                                    "You already have a connection to this server");
                            }
                        }
                    }
                    // The user doesn't have a connection, so let's proceed with
                    // checking their password
                    String passwordHash = Hash
                        .hash(password);
                    User confirmedAuthUser = DataStore
                        .getUser(username, passwordHash);
                    if (confirmedAuthUser == null)
                    {
                        throw new FailedResponseException(
                            "BADAUTH",
                            "Incorrect username and/or password");
                    }
                    // The user has successfully authenticated. Now we need to
                    // check and see if the computer they specified exists, if
                    // they specified a computer.
                    if (!computerName.equals(""))
                    {
                        Computer computer = DataStore
                            .getComputer(username,
                                computerName);
                        if (computer == null)
                        {
                            throw new FailedResponseException(
                                "BACOMPUTER",
                                "Nonexistant computer specified");
                        }
                    }
                    connection.username = username;
                    connection.computerName = (computerName
                        .equals("") ? null : computerName);
                    if (connection.computerName != null)
                    {
                        Map<String, ConnectionHandler> userMap = connectionsByAuth
                            .get(connection.username);
                        if (userMap == null)
                        {
                            userMap = new Hashtable<String, ConnectionHandler>();
                            connectionsByAuth.put(
                                connection.username,
                                userMap);
                        }
                        userMap.put(
                            connection.computerName,
                            connection);
                        Computer computerObject = DataStore
                            .getComputer(
                                connection.username,
                                connection.computerName);
                        computerObject.setLastonline(System
                            .currentTimeMillis());
                        DataStore
                            .updateComputer(computerObject);
                        tasks
                            .execute(new UserStatusNotifier(
                                connection.username,
                                connection.computerName));
                        
                    }
                    connection.sendEncryptedPacket(
                        packetId, "authenticate", "OK",
                        EMPTY);
                }
            }
        };
        new Command("ping", 8, true, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
            {
                connection.sendEncryptedPacket(packetId,
                    "ping", "OK", new byte[0]);
            }
        };
        new Command("quit", 8, true, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
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
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                long timeMillis = System
                    .currentTimeMillis();
                connection.sendEncryptedPacket(packetId,
                    "gettime", "OK",
                    ("" + timeMillis + " " + new Date(
                        timeMillis)).getBytes());
            }
            
        };
        new Command("createcomputer", 64, false, true)// ,boolean whenUnauth,
        // boolean
        // whenNoComputer
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                String[] tokens = tokenizeByLines(new String(
                    readToBytes(data)));
                verifyAtLeast(tokens, 2);
                if (DataStore
                    .getComputersForUser(connection.username).length >= DataStore
                    .getUserQuota(connection.username,
                        "computers"))
                    throw new FailedResponseException(
                        "QUOTAEXCEEDED",
                        "You have the maximum number of computers already, which is "
                            + DataStore.getUserQuota(
                                connection.username,
                                "computers"));
                // Ok, the user is authenticated and is allowed to create a
                // computer (IE they haven't exceeded their quota yet)
                String computerName = tokens[0];
                String computerType = tokens[1];
                DataStore.addComputer(connection.username,
                    computerName, computerType);
                connection.sendEncryptedPacket(packetId,
                    "createcomputer", "OK", EMPTY);
            }
            
        };
        new Command("sendimessage", 8320, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                byte[] dataBytes = readToBytes(data);
                String firstSubsection = new String(
                    dataBytes);
                String[] tokens = tokenizeByLines(firstSubsection);
                verifyAtLeast(tokens, 4);
                String messageId = tokens[0];
                String recipientUser = tokens[1];
                String recipientComputer = tokens[2];
                String messageContents = tokens[3];
                ConnectionHandler recipientConnection = getConnectionForComputer(
                    recipientUser, recipientComputer);
                if (recipientConnection == null)
                    throw new FailedResponseException(
                        "NOSUCHRECIPIENT",
                        "The recipient does not exist or is offline");
                recipientConnection
                    .sendEncryptedPacket(generateId(),
                        "receiveimessage", "OK", (""
                            + messageId + "\n"
                            + connection.username + "\n"
                            + connection.computerName
                            + "\n" + messageContents)
                            .getBytes());
                connection.sendEncryptedPacket(packetId,
                    "sendimessage", "OK", EMPTY);
            }
            
        };
        new Command("setpassword", 128, false, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                String newPassword = new String(
                    readToBytes(data));
                if (newPassword.contains("\r")
                    || newPassword.contains("\n")
                    || newPassword.startsWith(" ")
                    || newPassword.endsWith(" "))
                    throw new FailedResponseException(
                        "Passwords can't start or end with a space, "
                            + "and can't contain newlines.");
                User user = DataStore
                    .getUser(connection.username);
                user.setPassword(Hash.hash(newPassword));
                DataStore.updateUser(user);
                connection.sendEncryptedPacket(packetId,
                    "setpassword", "OK", EMPTY);
            }
        };
        new Command("getuserstatus", 128, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                String dataString = new String(
                    readToBytes(data));
                String[] tokens = tokenizeByLines(dataString);
                if (tokens.length == 1)
                    tokens = new String[] { tokens[0], "" };
                verifyAtLeast(tokens, 2);
                String username = tokens[0];
                String computerName = tokens[1];
                boolean isOnline = false;
                String lastOnline = "";
                boolean isComputer = !computerName
                    .equals("");
                if (isComputer)
                {
                    Computer computer = DataStore
                        .getComputer(username, computerName);
                    if (computer == null)
                        throw new FailedResponseException(
                            "NOSUCHCOMPUTER",
                            "The computer specified doesn't exist");
                    lastOnline += computer.getLastonline()
                        + " "
                        + new Date(computer.getLastonline());
                    isOnline = getConnectionForComputer(
                        username, computerName) != null;
                }
                else
                {
                    if (DataStore.getUser(username) == null)
                        throw new FailedResponseException(
                            "NOSUCHUSER",
                            "The user specified doesn't exist");
                    long lastOnlineValue = DataStore
                        .getUserLastOnline(username);
                    lastOnline += lastOnlineValue + " "
                        + new Date(lastOnlineValue);
                    Map userMap = connectionsByAuth
                        .get(username);
                    isOnline = userMap != null
                        && userMap.size() > 0;
                }
                connection.sendEncryptedPacket(packetId,
                    "getuserstatus", "OK", ("" + isOnline
                        + "\n" + lastOnline).getBytes());
            }
        };
        new Command("setvisibility", 10, false, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                String s = new String(readToBytes(data));
                User user = DataStore
                    .getUser(connection.username);
                user.setPubliclylisted(s.trim()
                    .equalsIgnoreCase("true"));
                DataStore.updateUser(user);
                connection.sendEncryptedPacket(packetId,
                    "setvisibility", "OK", EMPTY);
            }
        };
        new Command("getvisibility", 10, false, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                User user = DataStore
                    .getUser(connection.username);
                connection.sendEncryptedPacket(packetId,
                    "getvisibility", "OK", ("" + user
                        .isPubliclylisted()).getBytes());
            }
            
        };
        new Command("searchusers", 768, false, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 4);
                String searchString = tokens[0];
                String offsetString = tokens[1];
                String limitString = tokens[2];
                String searchOtherServersString = tokens[3];
                String[] keysToSearch = new String[tokens.length - 4];
                System.arraycopy(tokens, 4, keysToSearch,
                    0, keysToSearch.length);
                for (String cKey : keysToSearch)
                {
                    if (!cKey.startsWith("public-"))
                        throw new FailedResponseException(
                            "FAIL",
                            "The user settings specified must all start with public-");
                }
                // We've got the search criteria, now it's time to do the actual
                // search
                User[] users = DataStore.searchUsers("*"
                    + searchString + "*", Integer
                    .parseInt(offsetString), Integer
                    .parseInt(limitString), keysToSearch);
                int length = DataStore.searchUsersCount("*"
                    + searchString + "*", Integer
                    .parseInt(offsetString), Integer
                    .parseInt(limitString), keysToSearch);
                connection.sendEncryptedPacket(packetId,
                    command(), "OK",
                    ("" + length + "\n" + delimited(users,
                        new ToString<User>()
                        {
                            
                            @Override
                            public String toString(
                                User object)
                            {
                                return object.getUsername();
                            }
                        }, "\n")).getBytes());
            }
            
        };
        new Command("getusersetting", 256, false, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 2);
                String username = tokens[0];
                String property = tokens[1];
                boolean isPublic = property
                    .startsWith("public-");
                boolean isPrivate = !isPublic;
                boolean isThisUser = username.equals("");
                boolean isOtherUser = !isThisUser;
                if (isThisUser)
                    username = connection.username;
                if (isPrivate && isOtherUser)
                    throw new FailedResponseException(
                        "FAIL",
                        "Only properties starting with public- "
                            + "can be read for other users.");
                UserSetting setting = DataStore
                    .getUserSetting(username, property);
                String value = setting == null ? ""
                    : setting.getValue();
                connection.sendEncryptedPacket(packetId,
                    "getusersetting", "OK", value
                        .getBytes());
            }
            
        };
        new Command("listusersettings", 128, false, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 1);
                String username = tokens[0];
                UserSetting[] settings;
                if (username.equals(""))
                    settings = DataStore
                        .listUserSettings(connection.username);
                else
                    settings = DataStore
                        .listPublicUserSettings(username);
                connection.sendEncryptedPacket(packetId,
                    "listusersettings", "OK", delimited(
                        settings,
                        new ToString<UserSetting>()
                        {
                            
                            @Override
                            public String toString(
                                UserSetting object)
                            {
                                return object.getName();
                            }
                        }, "\n").getBytes());
            }
            
        };
        new Command("setusersetting", 2048, false, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                String[] tokens = tokenize(data);
                verifyAtLeast(tokens, 2);
                String name = tokens[0];
                String value = tokens[1];
                boolean delete = value.equals("");
                UserSetting existingSetting = DataStore
                    .getUserSetting(connection.username,
                        name);
                int settingSize = DataStore
                    .getUserSettingSize(connection.username);
                if (existingSetting != null)
                    settingSize = settingSize
                        - (existingSetting.getName()
                            .length()
                            + existingSetting.getValue()
                                .length() + 10);
                settingSize += 10 + name.length()
                    + value.length();
                if ((!delete)
                    && settingSize > DataStore
                        .getUserQuota(connection.username,
                            "usersettingsize"))
                    throw new FailedResponseException(
                        "QUOTAEXCEEDED",
                        "You have "
                            + DataStore.getUserQuota(
                                connection.username,
                                "usersettingsize")
                            + "allowed user setting bytes, but with this new property your size would be "
                            + settingSize);
                DataStore.setUserSetting(
                    connection.username, name, value);
                connection.sendEncryptedPacket(packetId,
                    "setusersetting", "OK", EMPTY);
            }
            
        };
        new Command("listcomputers", 128, false, true)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("getcomputersetting", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("setcomputersetting", 2048, false,
            false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("listcomputersettings", 128, false,
            false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("createsubscription", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("listsubscriptions", 128, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("deletesubscription", 128, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("createmessage", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("setmessagesetting", 2048, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("deletemessagesetting", 256, false,
            false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("getmessagesetting", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("listmessagesettings", 256, false,
            false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("setmessagedata", 65535, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("sendmessage", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("getmessagelifecycle", 256, false,
            false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("listapprovedmessages", 256, false,
            false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("listoutboundmessages", 256, false,
            false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("listunapprovedmessages", 256, false,
            false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("approvemessage", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("deletemessage", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("softdeletemessage", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("getmessageinfo", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("listmessagedata", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("getmessagedata", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        new Command("getmessagedatahash", 256, false, false)
        {
            
            @Override
            public void handle(String packetId,
                InputStream data,
                ConnectionHandler connection)
                throws Exception
            {
                // TODO Auto-generated method stub
                
            }
        };
        System.out.println("loaded " + commands.size()
            + " commands");
    }
    
    public static <T> String delimited(T[] items,
        ToString<T> generator, String delimiter)
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
    public static String[] tokenize(InputStream data)
        throws IOException
    {
        return tokenizeByLines(new String(readToBytes(data)));
    }
    
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
            System.arraycopy(cb, 0, result, pointer,
                cb.length);
            pointer += cb.length;
        }
        return result;
    }
    
    protected static void runLongSql(String sql,
        Connection con) throws SQLException
    {
        String[] statements = sql.split("\\;");
        int i = 1;
        for (String s : statements)
        {
            System.out.println("Running statement " + i++
                + " of " + statements.length);
            if (!s.trim().equals(""))
            {
                PreparedStatement st = con
                    .prepareStatement(s);
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
                throw new RuntimeException(
                    "the file is "
                        + file.length()
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
            ByteArrayInputStream bais = new ByteArrayInputStream(
                string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(
                file);
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
    
    public static void copy(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
    }
    
    private static Context createServerContext(
        Server server, String webroot)
    {
        Context context = new Context(server, "/",
            Context.SESSIONS);
        context.setResourceBase(webroot);
        return context;
    }
    
    private static void finishContext(Context context)
    {
        ServletHolder jsp = new ServletHolder(
            new JspServlet());
        jsp.setInitParameter("classpath", "classes;lib/*");
        jsp.setInitParameter("scratchdir", "classes");
        context.addServlet(jsp, "*.jsp");
        ServletHolder resource = new ServletHolder(
            new DefaultServlet());
        context.addServlet(resource, "/");
    }
    
    private static String getConfig(String key)
        throws SQLException
    {
        PreparedStatement st = pdb
            .prepareStatement("select value from " + pfix
                + "configuration where name = ?");
        st.setString(1, key);
        ResultSet rs = st.executeQuery();
        String value = null;
        if (rs.next())
            value = rs.getString("value");
        st.close();
        return value;
    }
    
    private static void setConfig(String key, String value)
        throws SQLException
    {
        PreparedStatement st;
        if (getConfig(key) == null)
        {
            st = pdb.prepareStatement("insert into " + pfix
                + "configuration "
                + "(name,value) values (?,?)");
            st.setString(1, key);
            st.setString(2, value);
        }
        else
        {
            st = pdb.prepareStatement("update " + lfix
                + "configuration set value = ?"
                + " where name = ?");
            st.setString(1, value);
            st.setString(2, key);
        }
        st.executeUpdate();
        st.close();
    }
    
    private static long nextId;
    
    public static synchronized String generateId()
    {
        return "p" + System.currentTimeMillis() + "uid"
            + nextId++;
    }
    
}
