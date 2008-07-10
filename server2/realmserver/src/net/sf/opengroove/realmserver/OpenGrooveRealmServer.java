package net.sf.opengroove.realmserver;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import net.sf.opengroove.realmserver.web.LoginFilter;
import net.sf.opengroove.realmserver.web.RendererServlet;
import net.sf.opengroove.security.Hash;
import net.sf.opengroove.security.RSA;
import nl.captcha.servlet.DefaultCaptchaIml;

public class OpenGrooveRealmServer
{
    
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
    private static BigInteger rsaEncryptionPublicKey;
    private static BigInteger rsaEncryptionModulus;
    private static BigInteger rsaEncryptionPrivateKey;
    private static BigInteger rsaSignaturePublicKey;
    private static BigInteger rsaSignatureModulus;
    private static BigInteger rsaSignaturePrivateKey;
    
    public static final ScheduledThreadPoolExecutor internalTasks = new ScheduledThreadPoolExecutor(
        15);
    
    public static final ThreadPoolExecutor tasks = new ThreadPoolExecutor(
        100, 300, 20, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(10000));
    /**
     * The first thing sent by all clients when they connect, followed by a
     * newline. This is so that an HTTP server could run on the same port as an
     * OpenGroove server and the server would be able to distinguish between
     * HTTP connections and OpenGroove connections.
     */
    public static final String CONNECTION_HEADER_TEXT = "OpenGroove";
    
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
    
    public static class ConnectionHandler extends Thread
    {
        private Socket socket;
        private InputStream internalInputStream;
        private TimedInputStream in;
        private OutputStream internalOutputStream;
        private TimedOutputStream out;
        private PacketSpooler spooler;
        private int allowedIdleMilliseconds = 10000;
        private byte[] securityKeyBytes;
        private Aes256 securityKey;
        private boolean completedHandshake;
        
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
                }
                s = s.trim();
                if (!s.equalsIgnoreCase("OpenGroove"))
                {
                    // Incorrect connection, probably an HTTP client or
                    // something
                    in.close();
                    out.close();
                    connections.remove(this);
                    return;
                }
                // Ok, correct header, now we send back our header
                out.write("OpenGrooveServer\n".getBytes());
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
                }
                s = s.trim();
                BigInteger challengeRandomEncInteger = new BigInteger(
                    s, 16);
                BigInteger challengeRandomInteger = RSA
                    .decrypt(rsaEncryptionPrivateKey,
                        rsaEncryptionModulus,
                        challengeRandomEncInteger);
                byte[] challengeRandomBytes = new byte[16];
                System.arraycopy(challengeRandomInteger
                    .toByteArray(), 0,
                    challengeRandomBytes, 0, 16);
                byte[] challengeRandomAes = new byte[16];
                securityKey.encrypt(challengeRandomBytes,
                    0, challengeRandomAes, 0);
                BigInteger challengeRandomAesInteger = new BigInteger(
                    challengeRandomAes);
                out.write((""
                    + challengeRandomAesInteger
                        .toString(16) + "\n").getBytes());
                out.flush();
                // The handshake is now complete. Now we start listening for
                // packets (using Crypto.dec), and deal with them accordingly.
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
                    spooler.close();
                    socket.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
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
        finishContext(context);
        server.start();
        Thread.sleep(300);// so that stdout and stderr don't get mixed up
        System.out.println("loading OpenGroove server...");
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
        tasks.prestartAllCoreThreads();
        internalTasks.prestartAllCoreThreads();
        System.out
            .println("OpenGroove Realm Server is up and running.");
        while (!serverSocket.isClosed())
        {
            try
            {
                Socket socket = serverSocket.accept();
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
    
}
