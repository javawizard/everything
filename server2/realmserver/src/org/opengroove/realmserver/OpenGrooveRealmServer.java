package org.opengroove.realmserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.opengroove.util.Hash;

import DE.knp.MicroCrypt.Sha512;

import nanohttpd.NanoHTTPD;
import nanohttpd.NanoHTTPD.Response;
import nl.captcha.servlet.DefaultCaptchaIml;

public class OpenGrooveRealmServer
{
    protected static final File HTTPD_RES_FOLDER = new File(
        "httpdres");
    
    private static Connection db;
    
    private static File dbFolder = new File("appdata/db");
    
    private static boolean setupStillRunning = true;
    
    private static boolean setupStillAllowed = true;
    
    private static HandlerServer webserver;
    
    /**
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args)
        throws IOException, InterruptedException
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
        if (!dbFolder.exists())
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
            new NanoHTTPD(53828)
            {
                
                @Override
                public synchronized Response serve(
                    String uri, String method,
                    Properties header, Properties parms)
                {
                    if (!setupStillAllowed)
                    {
                        return serveFile(
                            "/setup/done.html",
                            new Properties(),
                            HTTPD_RES_FOLDER, false);
                    }
                    if (uri
                        .equalsIgnoreCase("/setup/start.html")
                        || uri
                            .equalsIgnoreCase("/setup/badpassword.html")
                        || uri
                            .equalsIgnoreCase("/setup/shortpassword.html")
                        || uri
                            .equalsIgnoreCase("/setup/captcha.png"))
                    {
                        return serveFile(uri,
                            new Properties(),
                            HTTPD_RES_FOLDER, false);
                    }
                    else if (uri
                        .equalsIgnoreCase("/setup/s2.html"))
                    {
                        String password = parms
                            .getProperty("password");
                        String passwordAgain = parms
                            .getProperty("passwordagain");
                        String username = parms
                            .getProperty("username");
                        // check for matching passwords
                        if (!password.equals(passwordAgain))
                        {
                            return redirect("/setup/badpassword.html");
                        }
                        else if (password.length() < 5)
                        {
                            return redirect("/setup/shortpassword.html");
                        }
                        try
                        {
                            Class
                                .forName("smallsql.database.SSDriver");
                            db = DriverManager
                                .getConnection("jdbc:smallsql:"
                                    + dbFolder.getPath()
                                    + "?create=true");
                            runLongSql(readFile(new File(
                                "init.sql")));
                            runLongSql("insert into webusers values ('"
                                + username
                                + "','admin', '"
                                + Hash.hash(password
                                    .getBytes()) + "')");
                            db.close();
                        }
                        catch (Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                        setupStillAllowed = false;
                        return redirect("/setup/done.html");
                    }
                    else
                    {
                        return redirect("/setup/start.html");
                    }
                }
            };
            System.out
                .println("This is the first time you've run OpenGroove Realm Server, so you'll "
                    + "need to provide some information so that the server can be "
                    + "configured. Open a browser and go to http://localhost:53828 to"
                    + " get OpenGroove Realm Server up and running.");
            while (setupStillRunning)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return;
        }
        // If we get here then OpenGroove has been set up, so get everything up
        // and running
        System.out.println("loading web server");
        webserver = new HandlerServer(53828);
        while (true)
            Thread.sleep(500);
    }
    
    protected static void runLongSql(String sql)
        throws SQLException
    {
        String[] statements = sql.split("\\;");
        int i = 1;
        for (String s : statements)
        {
            System.out.println("Running statement " + i++
                + " of " + statements.length);
            if (!s.trim().equals(""))
            {
                PreparedStatement st = db
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
    
}
