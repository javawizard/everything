package jw.bznetwork.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import jw.bznetwork.client.AuthProvider;
import jw.bznetwork.client.Perms;
import jw.bznetwork.utils.StringUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * Provides methods related to the BZNetwork system as a whole.
 * 
 * @author Alexander Boyd
 * 
 */
public class BZNetworkServer implements ServletContextListener
{
    private static HashMap<String, AuthProvider> authProviders = new HashMap<String, AuthProvider>();
    private static boolean isInstalled;
    private static File cacheFolder;
    private static File storeFolder;
    private static String accessLockMessage;
    
    /**
     * Sticks information on to the request indicating that the user has just
     * logged in.
     * 
     * @param request
     *            The request that is being made to log in. The login
     *            information will be added to this request's session.
     * @param provider
     *            The provider being used to log in
     * @param username
     *            The username being used to log in
     * @param roles
     *            The integer ids of the roles that should be applied
     */
    public static void login(HttpServletRequest request, String provider,
            String username, int[] roles)
    {
        
    }
    
    /**
     * Gets the id of the default authentication provider. Null is returned if
     * there is no default.
     * 
     * @return
     */
    public static String getDefaultAuthProvider()
    {
        Properties props = loadEnabledAuthProps();
        for (String key : props.keySet().toArray(new String[0]))
        {
            if (props.get(key).equals("default"))
                return key;
        }
        return null;
    }
    
    private static Properties loadEnabledAuthProps()
    {
        Properties props = new Properties();
        if (!isInstalled())
            return props;
        try
        {
            props.load(new FileInputStream(new File(storeFolder,
                    "enabled-auth-providers.props")));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return props;
    }
    
    public static AuthProvider[] getEnabledAuthProviders()
    {
        if (!isInstalled())
            return new AuthProvider[0];
        Properties props = loadEnabledAuthProps();
        ArrayList<AuthProvider> enabledProviders = new ArrayList<AuthProvider>();
        for (AuthProvider provider : authProviders.values())
        {
            if (!props.get(provider.getId()).equals("disabled"))
                enabledProviders.add(provider);
        }
        return enabledProviders.toArray(new AuthProvider[0]);
    }
    
    private static SqlMapClient generalDataClient;
    
    private static ServletContext context;
    
    public static ServletContext getServletContext()
    {
        return context;
    }
    
    public static SqlMapClient getGeneralDataClient()
    {
        return generalDataClient;
    }
    
    public void contextDestroyed(ServletContextEvent sce)
    {
        
    }
    
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            context = sce.getServletContext();
            File configFolder = new File(context.getRealPath("/WEB-INF/config"));
            if (!configFolder.exists())
            {
                isInstalled = false;
                Perms.installProvider(ServerPermissionsProvider.NULL_PROVIDER);
                return;
            }
            isInstalled = true;
            Properties settingsProps = new Properties();
            settingsProps.load(new FileInputStream(new File(configFolder,
                    "settings.props")));
            String generalDataConfig = StringUtils.readStream(getClass()
                    .getResourceAsStream("/general-sql.xml"));
            generalDataConfig = generalDataConfig.replace("$$driver$$",
                    settingsProps.getProperty("db-driver"));
            generalDataConfig = generalDataConfig.replace("$$url$$",
                    settingsProps.getProperty("db-url"));
            generalDataConfig = generalDataConfig.replace("$$username$$",
                    settingsProps.getProperty("db-username"));
            generalDataConfig = generalDataConfig.replace("$$password$$",
                    settingsProps.getProperty("db-password"));
            try
            {
                Class.forName(settingsProps.getProperty("db-driver"));
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            generalDataClient = SqlMapClientBuilder
                    .buildSqlMapClient(new StringReader(generalDataConfig));
            storeFolder = new File(settingsProps.getProperty("store-folder"));
            cacheFolder = new File(settingsProps.getProperty("cache-folder"));
            /*
             * Now we'll load the authentication providers. We're just loading
             * the providers here, not the list of which ones are enabled, since
             * that can change during the lifetime of the vm.
             */
            BufferedReader authProviderReader = new BufferedReader(
                    new InputStreamReader(context
                            .getResourceAsStream("/WEB-INF/server/auth.txt")));
            String line;
            while ((line = authProviderReader.readLine()) != null)
            {
                if (!line.trim().equals(""))
                {
                    AuthProvider providerObject = new AuthProvider(line);
                    authProviders.put(providerObject.getId(), providerObject);
                }
            }
            Perms.installProvider(new ServerPermissionsProvider());
        }
        catch (Exception e)
        {
            System.err
                    .println("A fatal exception occured while starting BZNetwork: ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static boolean isInstalled()
    {
        return isInstalled;
    }
    
    public static InstallResponse doInstall(HttpServletRequest request)
            throws Exception
    {
        if (isInstalled)
            return new InstallResponse(null,
                    "BZNetwork is already installed on this server.", false);
        String dbDriver = request.getParameter("db-driver");
        String dbUrl = request.getParameter("db-url");
        String dbUsername = request.getParameter("db-username");
        String dbPassword = request.getParameter("db-password");
        String storeFolder = request.getParameter("store-folder");
        String cacheFolder = request.getParameter("cache-folder");
        /*
         * First, we'll connect to the database and select from the
         * configuration table. If we get an exception, or if there are no rows,
         * then we know that we're working with a clean database. If we don't,
         * then we issue a warning to the user.
         */
        try
        {
            Class.forName(dbDriver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new InstallResponse(null,
                    "You specified an invalid driver name.", false);
        }
        Connection con1 = DriverManager.getConnection(dbUrl, dbUsername,
                dbPassword);
        boolean areTablesInstalled = true;
        try
        {
            PreparedStatement st = con1
                    .prepareStatement("select * from configuration");
            ResultSet rs = st.executeQuery();
            if (!rs.next())
                throw new RuntimeException();
        }
        catch (Exception e)
        {
            areTablesInstalled = false;
        }
        con1.close();
        if (areTablesInstalled)
        {
            if (request.getParameter("supress-existence-warning") == null)
            {
                return new InstallResponse(
                        "supress-existence-warning",
                        "BZNetwork is already installed in that database. If "
                                + "you continue, your tables will not be overwritten. "
                                + "You should make sure that you're using the same "
                                + "store folder that you did with your previous installation.",
                        true);
            }
        }
        File storeFolderFile = new File(storeFolder);
        File cacheFolderFile = new File(cacheFolder);
        if (!storeFolderFile.exists())
        {
            if (!storeFolderFile.mkdirs())
                return new InstallResponse(null,
                        "The store folder you specified cannot be created.",
                        false);
        }
        if (!cacheFolderFile.exists())
        {
            if (!cacheFolderFile.mkdirs())
                return new InstallResponse(null,
                        "The cache folder you specified cannot be created.",
                        false);
        }
        if (storeFolderFile.exists())
        {
            if (!storeFolderFile.isDirectory())
            {
                return new InstallResponse(
                        null,
                        "The store folder you specified exists but is not a folder.",
                        false);
            }
            if (!storeFolderFile.canRead())
            {
                return new InstallResponse(
                        null,
                        "The store folder you specified exists but cannot be read.",
                        false);
            }
            if (!storeFolderFile.canWrite())
            {
                return new InstallResponse(
                        null,
                        "The store folder you specified exists but cannot be written.",
                        false);
            }
        }
        if (cacheFolderFile.exists())
        {
            if (!cacheFolderFile.isDirectory())
            {
                return new InstallResponse(
                        null,
                        "The cache folder you specified exists but is not a folder.",
                        false);
            }
            if (!cacheFolderFile.canRead())
            {
                return new InstallResponse(
                        null,
                        "The cache folder you specified exists but cannot be read.",
                        false);
            }
            if (!cacheFolderFile.canWrite())
            {
                return new InstallResponse(
                        null,
                        "The cache folder you specified exists but cannot be written.",
                        false);
            }
        }
        lockAccess("You need to restart your server to "
                + "complete the installation. Then log in with username"
                + " 'admin' and password 'admin' to use " + "BZNetwork.");
        return new InstallResponse(
                null,
                "<html><body><b>Congratulations.</b> BZNetwork has been successfully "
                        + "installed. Restart the web server, then visit <a href='"
                        + request.getContextPath()
                        + "/'>your BZNetwork installation</a> to begin using it. "
                        + "<b>Use the username</b> <tt>admin</tt> and the password "
                        + "<tt>admin</tt> to log in.</body></html>", false);
    }
    
    public static void lockAccess(String message)
    {
        accessLockMessage = message;
    }
    
    public static boolean isAccessLocked()
    {
        return accessLockMessage != null;
    }
    
    public static String getAccessLockMessage()
    {
        return accessLockMessage;
    }
    
}
