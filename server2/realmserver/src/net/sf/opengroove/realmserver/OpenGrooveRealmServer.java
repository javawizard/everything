package net.sf.opengroove.realmserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

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

import DE.knp.MicroCrypt.Sha512;

import nanohttpd.NanoHTTPD;
import nanohttpd.NanoHTTPD.Response;
import net.sf.opengroove.realmserver.web.LoginFilter;
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
            "classes/largesqlmap.txt"));
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
        Server server = new Server(Integer.parseInt(config
            .getProperty("webport")));
        Context context = createServerContext(server, "web");
        context.addFilter(new FilterHolder(
            new LoginFilter()), "/*", Context.ALL);
        finishContext(context);
        server.start();
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
        context.setErrorHandler(new DefaultErrorHandler());
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
