package jw.bznetwork.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;

import jw.bznetwork.client.AuthProvider;
import jw.bznetwork.client.ClientPermissionsProvider;
import jw.bznetwork.client.Constants;
import jw.bznetwork.client.Perms;
import jw.bznetwork.client.Settings;
import jw.bznetwork.client.Constants.TargetType;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.CheckPermission;
import jw.bznetwork.client.data.model.Action;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.IrcBot;
import jw.bznetwork.client.data.model.LogEvent;
import jw.bznetwork.client.data.model.LogRequest;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.data.model.TargetEventPair;
import jw.bznetwork.client.data.model.Trigger;
import jw.bznetwork.client.screens.LogsScreen;
import jw.bznetwork.client.x.VXVars;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XNumber;
import jw.bznetwork.client.x.lang.XString;
import jw.bznetwork.server.data.DataStore;
import jw.bznetwork.server.live.LiveServer;
import jw.bznetwork.server.live.ReadThread;
import jw.bznetwork.server.rpc.GlobalLinkImpl;
import jw.bznetwork.server.x.ServerSideTextScripter;
import jw.bznetwork.utils.StringUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * Provides methods related to the BZNetwork system as a whole.
 * 
 * @author Alexander Boyd
 * 
 */
public class BZNetworkServer implements ServletContextListener,
        HttpSessionListener
{
    private static HashMap<Integer, IrcServerBot> ircServerBotMap = new HashMap<Integer, IrcServerBot>();
    public static boolean isWindows = System.getProperty("os.name")
            .toLowerCase().contains("windows");
    /**
     * Mirrored on LogsScreen.SEARCH_IN
     */
    public static final String[] SEARCH_IN = new String[]
    {
            "event", "source", "target", "sourceteam", "targetteam",
            "ipaddress", "bzid", "email", "data"
    };
    public static final String newline = System.getProperty("line.separator");
    private static final String serverControlPluginName = isWindows ? "serverControl.dll"
            : "serverControl.so";
    private static final String bznetworkPluginName = isWindows ? "bz_iplugin_bznetwork.dll"
            : "libbz_iplugin_bznetwork.so";
    
    public static HashMap<Integer, LiveServer> getLiveServers()
    {
        return liveServers;
    }
    
    private static File mapsFolder;
    private static File configFolder;
    
    public LiveServer getLiveServer(int id)
    {
        return liveServers.get(id);
    }
    
    private static HashMap<String, AuthProvider> authProviders = new HashMap<String, AuthProvider>();
    private static HashMap<String, HttpSession> sessions = new HashMap<String, HttpSession>();
    private static HashMap<Integer, LiveServer> liveServers = new HashMap<Integer, LiveServer>();
    private static boolean isInstalled;
    public static File cacheFolder;
    private static File storeFolder;
    private static File bansFolder;
    private static File groupdbFolder;
    private static File includedPluginsFolder;
    private static File serverControlPlugin;
    private static File bznetworkPlugin;
    private static String accessLockMessage;
    
    public static HashMap<String, HttpSession> getSessionList()
    {
        return sessions;
    }
    
    /**
     * Sticks information on to the request indicating that the user has just
     * logged in. This should be called from any authentication provider when
     * that provider has determined that the user has successfully authenticated
     * with the server.
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
        /*
         * Remove duplicate role ids
         */
        ArrayList<Integer> roleIdList = new ArrayList<Integer>();
        for (int role : roles)
        {
            if (!roleIdList.contains(role))
                roleIdList.add(role);
        }
        roles = new int[roleIdList.size()];
        int index = 0;
        for (int role : roleIdList)
        {
            roles[index++] = role;
        }
        /*
         * Make sure that the user isn't already logged in
         */
        if (request.getSession().getAttribute("user") != null)
        {
            throw new LoginException("The user has already logged in.");
        }
        /*
         * Validate that this provider is enabled
         */
        Properties enabledAuthProps = loadEnabledAuthProps();
        if (!(enabledAuthProps.getProperty(provider).equals("enabled") || enabledAuthProps
                .getProperty(provider).equals("default")))
            throw new LoginException("That authentication provider ("
                    + provider + ") is disabled on this server.");
        /*
         * Validate that all of the roles specified exist
         */
        ArrayList<String> roleNames = new ArrayList<String>();
        for (int roleid : roles)
        {
            Role role = DataStore.getRoleById(roleid);
            if (role == null)
                throw new LoginException("The role " + roleid
                        + " is assigned to this user, but "
                        + "has since been deleted.");
            roleNames.add(role.getName());
        }
        /*
         * All of the roles exist. Now we go and load the AuthProvider.
         */
        AuthUser authUser = new AuthUser();
        authUser.setUsername(username);
        authUser.setProvider(provider);
        authUser.setRoles(roles);
        authUser.getRoleNames().addAll(roleNames);
        /*
         * Now we'll get all of the permissions assigned to this role.
         */
        for (int roleid : roles)
        {
            Permission[] rolePermissions = DataStore
                    .getPermissionsByRole(roleid);
            for (Permission rolePermission : rolePermissions)
            {
                authUser.getPermissions().add(
                        new CheckPermission(rolePermission.getPermission(),
                                rolePermission.getTarget()));
            }
        }
        /*
         * The auth user has been successfully set up. Now we go and stick the
         * auth user on the session.
         */
        request.getSession().setAttribute("user", authUser);
        /*
         * Now we need to add a permissions provider.
         */
        request.getSession().setAttribute("permissions-provider",
                new ClientPermissionsProvider(authUser));
        /*
         * We'll also make a note of the time at which the user logged in.
         */
        request.getSession().setAttribute("stat-logged-in",
                System.currentTimeMillis());
        /*
         * And we'll add to the action log that the user logged in. This should
         * probably be a configuration option in the future. We might also want
         * to consider logging the ip address of the user, if that's enabled in
         * the configuration.
         */
        String details = "Roles: "
                + StringUtils.delimited(roleNames.toArray(new String[0]), ", ")
                + "\nIP address: " + request.getRemoteAddr();
        GlobalLinkImpl.action("logged-in", details);
        /*
         * We're done!
         */
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
    
    public static synchronized Properties loadEnabledAuthProps()
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
        for (String provider : authProviders.keySet())
        {
            if (props.getProperty(provider) == null)
                props.setProperty(provider, "disabled");
        }
        return props;
    }
    
    public static synchronized void saveEnabledAuthProps(Properties props)
    {
        try
        {
            props.store(new FileOutputStream(new File(storeFolder,
                    "enabled-auth-providers.props")),
                    "Props updated by BZNetworkServer."
                            + "saveEnabledAuthProps(Properties)");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
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
        for (Settings s : Settings.values())
        {
            s.setAdapter(SettingsManager.singleton);
        }
        context = sce.getServletContext();
        load();
    }
    
    public static void load()
    {
        try
        {
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
            String generalDataConfig = StringUtils
                    .readStream(BZNetworkServer.class
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
            bansFolder = new File(storeFolder, "bans");
            groupdbFolder = new File(storeFolder, "groupdb");
            mapsFolder = new File(storeFolder, "maps");
            BZNetworkServer.configFolder = new File(storeFolder, "config");
            bansFolder.mkdirs();
            groupdbFolder.mkdirs();
            mapsFolder.mkdirs();
            BZNetworkServer.configFolder.mkdirs();
            /*
             * The cache folder shouldn't have any files in it at system
             * startup, and if it does, we need to delete them.
             */
            for (File f : cacheFolder.listFiles())
            {
                f.delete();
            }
            includedPluginsFolder = new File(context
                    .getRealPath("/WEB-INF/bzfs-plugins"));
            serverControlPlugin = new File(includedPluginsFolder,
                    serverControlPluginName);
            bznetworkPlugin = new File(includedPluginsFolder,
                    bznetworkPluginName);
            if (!serverControlPlugin.exists())
                throw new RuntimeException(
                        "The server control plugin is supposed to exist at "
                                + serverControlPlugin.getAbsolutePath()
                                + ", but it doesn't. Please copy it to that location.");
            if (!bznetworkPlugin.exists())
                throw new RuntimeException(
                        "The bznetwork plugin is supposed to exist at "
                                + bznetworkPlugin.getAbsolutePath()
                                + ", but it doesn't. Please copy it to that location.");
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
            notifyIrcReconnectRequested();
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
        Connection con1;
        try
        {
            con1 = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new InstallResponse(null,
                    "The database couldn't be connected to. Error message: "
                            + e.getClass().getName() + ": " + e.getMessage(),
                    false);
        }
        boolean areTablesInstalled = true;
        try
        {
            PreparedStatement st = con1
                    .prepareStatement("select * from configuration");
            ResultSet rs = st.executeQuery();
            if (!rs.next())
            {
                rs.close();
                st.close();
                throw new RuntimeException();
            }
            rs.close();
            st.close();
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
                                + "you continue, your tables will not be overwritten, "
                                + "and all of your previous installation's data "
                                + "(including usernames; the admin username that"
                                + " will be mentioned at the end of the install"
                                + " will not be created) will be retained. "
                                + "You should make sure that you're using the same "
                                + "store folder that you did with your previous "
                                + "installation. You don't, however, need to "
                                + "use the same cache folder.", true);
            }
        }
        /*
         * Now try to create the store and cache folders.
         */
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
        /*
         * Now we'll copy enabled-auth-providers.props to the store folder.
         */
        if (!areTablesInstalled)
            StringUtils
                    .writeFile(
                            StringUtils
                                    .readFile(new File(
                                            getServletContext()
                                                    .getRealPath(
                                                            "/WEB-INF/config-stub/enabled-auth-providers.props"))),
                            new File(storeFolder,
                                    "enabled-auth-providers.props"));
        /*
         * Now we'll connect to the database and insert the tables.
         */
        Connection con2 = DriverManager.getConnection(dbUrl, dbUsername,
                dbPassword);
        if (!areTablesInstalled)
        {
            String createTablesStatement = StringUtils.readFile(new File(
                    getServletContext().getRealPath("/WEB-INF/tables.sql")));
            /*
             * MySQL apparently doesn't like SQL comments, so we'll manually
             * remove them.
             */
            createTablesStatement = createTablesStatement.replaceAll(
                    "--[^\\n]*\\n", "");
            /*
             * MySQL also doesn't like multiple statements within the same JDBC
             * statement object, so we'll split up the statement for it.
             */
            String[] statements = createTablesStatement.split("\\;");
            for (String s : statements)
            {
                if (!s.trim().equals(""))
                {
                    Statement st = con2.createStatement();
                    st.executeUpdate(s);
                    st.close();
                }
            }
        }
        con2.close();
        /*
         * Write the config file
         */
        Properties props = new Properties();
        props.setProperty("db-driver", dbDriver);
        props.setProperty("db-url", dbUrl);
        props.setProperty("db-username", dbUsername);
        props.setProperty("db-password", dbPassword);
        props.setProperty("store-folder", storeFolder);
        props.setProperty("cache-folder", cacheFolder);
        StringWriter sw = new StringWriter();
        props.store(sw, "BZNetwork");
        String configContents = sw.toString();
        boolean writeConfigWorked = true;
        try
        {
            new File(getServletContext().getRealPath("/WEB-INF/config"))
                    .mkdirs();
            StringUtils.writeFile(configContents, new File(getServletContext()
                    .getRealPath("/WEB-INF/config/settings.props")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            writeConfigWorked = false;
        }
        String installedLockMessage = "You need to restart your server to "
                + "complete the installation. Then log in with username"
                + " 'admin' and password 'admin' to use " + "BZNetwork.";
        if (writeConfigWorked)
        {
            boolean loadWorked = true;
            String possibleRestartMessage = "V";
            try
            {
                load();
            }
            catch (Exception e)
            {
                loadWorked = false;
                e.printStackTrace();
                possibleRestartMessage = "Restart the web server, then v";
                lockAccess(installedLockMessage);
            }
            if (areTablesInstalled && loadWorked)
            {
                /*
                 * If the tables are already installed, add an action event. If
                 * they're not, tables.sql will add an event for us, so we don't
                 * need to worry about that here.
                 */
                Action action = new Action();
                action
                        .setDetails("BZNetwork has been installed from an already-existing database.");
                action.setEvent("setup-existing");
                action.setProvider("internal");
                action.setTarget(-1);
                action.setUsername("admin");
                action.setWhen(new Date());
                DataStore.addActionEvent(action);
            }
            return new InstallResponse(
                    null,
                    "<html><body><b>Congratulations.</b> BZNetwork has been successfully "
                            + "installed. "
                            + possibleRestartMessage
                            + "isit <a href='"
                            + request.getContextPath()
                            + "/'>your BZNetwork installation</a> to begin using it. "
                            + "<b>Use the username</b> <tt>admin</tt> and the password "
                            + "<tt>admin</tt> to log in.</body></html>", false);
        }
        else
        {
            lockAccess(installedLockMessage);
            return new InstallResponse(
                    null,
                    "<html><body><b>BZNetwork is almost installed.</b> The "
                            + "only problem is, the file "
                            + getServletContext().getRealPath(
                                    "/WEB-INF/config/settings.props")
                            + "can't be written to. So you'll need to manually "
                            + "create this file. <b>Copy the text you see below</b>"
                            + " in monospaced font into that file. Create the "
                            + "file if it doesn't exist. Then restart the web "
                            + "server, and visit <a href='"
                            + request.getContextPath()
                            + "/'>your BZNetwork installation</a> to begin using it. "
                            + "<b>Use the username</b> <tt>admin</tt> and the password "
                            + "<tt>admin</tt> to log in.</body></html>", false);
        }
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
    
    public static AuthProvider[] getAuthProviders()
    {
        return authProviders.values().toArray(new AuthProvider[0]);
    }
    
    @Override
    public void sessionCreated(HttpSessionEvent se)
    {
        sessions.put(se.getSession().getId(), se.getSession());
        se.getSession().setMaxInactiveInterval(60 * 120);
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se)
    {
        sessions.remove(se.getSession().getId());
    }
    
    /**
     * Starts the specified server.
     * 
     * @return
     */
    public static String startServer(int id, boolean synchronous)
    {
        LinkedBlockingQueue<String> readQueue = null;
        synchronized (BZNetworkServer.class)
        {
            Server server = DataStore.getServerById(id);
            if (server == null)
                throw new IllegalArgumentException(
                        "There is no server with the id " + id);
            Group group = DataStore.getGroupById(server.getGroupid());
            if (group == null)
                throw new IllegalStateException("Orphaned server");
            if (server.getPort() == 0 || server.getPort() == -1)
                throw new IllegalStateException(
                        "The port for this server hasn't been set yet.");
            LiveServer liveServer = new LiveServer();
            liveServer.setServer(server);
            liveServer.setChangingState(true);
            liveServer.setStarting(true);
            liveServer.setId(server.getServerid());
            /*
             * Now we need to compile together this server's groupdb file and
             * store it in the temp folder. This involves getting the server's
             * groupdb file, checking to see if the server inherits its group's
             * groupdb file, and, if so, getting the parent's groupdb file.
             */
            File serverGroupdb = new File(groupdbFolder, ""
                    + server.getServerid());
            String serverGroupdbContent = "";
            if (serverGroupdb.exists())
                serverGroupdbContent = StringUtils.readFile(serverGroupdb);
            String groupGroupdbContent = "";
            if (server.isInheritgroupdb())
            {
                File groupGroupdb = new File(groupdbFolder, ""
                        + server.getGroupid());
                if (groupGroupdb.exists())
                    groupGroupdbContent = StringUtils.readFile(groupGroupdb);
            }
            String newGroupdbName = generateRandomName();
            liveServer.addTempFile(newGroupdbName);
            File newGroupdbFile = new File(cacheFolder, newGroupdbName);
            StringUtils.writeFile(groupGroupdbContent
                    + System.getProperty("line.separator")
                    + serverGroupdbContent, newGroupdbFile);
            /*
             * The combined groupdb file has been written to a cache file. Now
             * we'll go figure out which banfile we're supposed to use, and
             * verify that it exists.
             */
            int banfileId = server.getBanfile();
            if (banfileId == -1)
                banfileId = group.getBanfile();
            if (banfileId == -1)
                throw new IllegalStateException(
                        "Neither the group nor the server has specified "
                                + "a banfile, so the server cannot start.");
            Banfile banfile = DataStore.getBanfileById(banfileId);
            if (banfile == null)
                throw new IllegalStateException("The referenced banfile ("
                        + banfileId + ") does not exist.");
            File banfileFile = new File(bansFolder, "" + banfileId);
            if (!banfileFile.exists())
            {
                try
                {
                    banfileFile.createNewFile();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                    throw new RuntimeException("Unable to create the ban file.");
                }
            }
            File mapFile = getMapFile(server.getServerid());
            if (!mapFile.exists())
                throw new IllegalStateException(
                        "This server doesn't yet have a map.");
            File configFile = getConfigFile(server.getServerid());
            if (!configFile.exists())
                throw new IllegalStateException(
                        "This server doesn't yet have a config.");
            String serverControlConfigName = generateRandomName();
            liveServer.addTempFile(serverControlConfigName);
            File serverControlConfig = new File(cacheFolder,
                    serverControlConfigName);
            StringUtils.writeFile("[ServerControl]" + newline + "BanFile = "
                    + banfileFile.getAbsolutePath() + newline
                    + "BanReloadMessage = Bans Updated", serverControlConfig);
            /*
             * We now have the groupdb file, the banfile, and the world file.
             * Now we can actually start the server.
             */
            String executable = Settings.executable.getString();
            Process process;
            ArrayList<String> args = new ArrayList<String>();
            args.add(executable);
            args.add("-p");
            args.add("" + server.getPort());
            if (server.isListed() || Settings.hiddenglobal.getBoolean())
            {
                args.add("-public");
                args.add(server.getName());
                String publicHost = Settings.publichostname.getString();
                if (!publicHost.trim().equals(""))
                {
                    args.add("-publicaddr");
                    args.add(publicHost + ":" + server.getPort());
                }
            }
            if (Settings.hiddenglobal.getBoolean() && !server.isListed())
            {
                args.add("-advertise");
                args.add("NONE");
            }
            args.add("-world");
            args.add(mapFile.getAbsolutePath());
            args.add("-conf");
            args.add(configFile.getAbsolutePath());
            args.add("-groupdb");
            args.add(newGroupdbFile.getAbsolutePath());
            args.add("-banfile");
            args.add(banfileFile.getAbsolutePath());
            /*
             * We'll instruct the server to send all reports to /dev/null, since
             * the plugin gets them by listening in on slash commands so we
             * don't need to do anything with the report file output.
             */
            args.add("-reportfile");
            if (isWindows)
                args.add("nul");
            else
                args.add("/dev/null");
            args.add("-loadplugin");
            args.add(serverControlPlugin.getAbsolutePath() + ","
                    + serverControlConfig.getAbsolutePath());
            args.add("-loadplugin");
            args.add(bznetworkPlugin.getAbsolutePath());
            System.out.print("Starting server with args: ");
            for (String s : args)
            {
                System.out.print(s + " ");
            }
            System.out.println();
            try
            {
                process = new ProcessBuilder(args.toArray(new String[0]))
                        .redirectErrorStream(true).start();
            }
            catch (Exception e)
            {
                throw new RuntimeException(
                        "Exception occured while starting the bzfs executable",
                        e);
            }
            /*
             * The process is running. Now we add it to the live server,
             * register the live server, and create a read thread.
             */
            liveServer.setProcess(process);
            liveServer.setOut(process.getOutputStream());
            /*
             * TODO: have excess output and error output from bzfs be sent to
             * the log
             */
            final InputStream errorIn = process.getErrorStream();
            new Thread()
            {
                public void run()
                {
                    int i;
                    try
                    {
                        while ((i = errorIn.read()) != -1)
                        {
                            System.out.write(i);
                            System.out.flush();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
            liveServers.put(liveServer.getId(), liveServer);
            /*
             * Before we add a read thread, we need to add the queue that will,
             * if we're starting the server synchronously, wait for a response
             * from the server.
             */
            readQueue = new LinkedBlockingQueue<String>(10);
            liveServer.setLoadListenerQueue(readQueue);
            /*
             * Now we can start the read thread.
             */
            ReadThread readThread = new ReadThread(liveServer);
            liveServer.setReadThread(readThread);
            readThread.start();
            /*
             * If we started the server asynchronously, then we're done. If
             * we're starting it synchronously, then we'll drop out of the
             * synchronized block and wait for a response from the read queue.
             */
            if (!synchronous)
                return null;
        }
        /*
         * If we get here, then we are starting the server synchronously, so
         * we'll go ahead and wait on the read queue.
         */
        String response;
        try
        {
            response = readQueue.poll(15, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(
                    "Interrupted while waiting for a startup "
                            + "response from the bzfs server");
        }
        if (response == null)
        {
            throw new ReadTimeoutException(
                    "No response received from the server as to whether "
                            + "or not it successfully started up within the "
                            + "15 second timeout that BZNetwork waits for the response. "
                            + "The server will continue to run, assuming it manages to start "
                            + "up successfully.");
        }
        return response;
    }
    
    public static File getConfigFile(int serverid)
    {
        return new File(configFolder, "" + serverid);
    }
    
    public static File getMapFile(int serverid)
    {
        return new File(mapsFolder, "" + serverid);
    }
    
    private static final Random randomNumberGenerator = new Random();
    
    /**
     * Generates a string of random characters that can be used as the name of a
     * file.
     * 
     * @return
     */
    public static String generateRandomName()
    {
        return "" + Integer.toHexString(randomNumberGenerator.nextInt())
                + Integer.toHexString(randomNumberGenerator.nextInt())
                + Integer.toHexString(randomNumberGenerator.nextInt())
                + Integer.toHexString(randomNumberGenerator.nextInt());
    }
    
    public static File getGroupdbFile(int itemid)
    {
        return new File(groupdbFolder, "" + itemid);
    }
    
    public static void stopServer(int serverid)
    {
        LiveServer server = liveServers.get(serverid);
        if (server == null)
        {
            /*
             * The server isn't running, so we don't need to do anything to shut
             * it down.
             */
            return;
        }
        server.requestShutdown();
    }
    
    /**
     * Kills the BZFlag server with the specified id. This causes the server to
     * be forcibly terminated. Due to the fact that the server is terminated
     * immediately, clients won't receive a "server has forced a disconnect"
     * message; instead, they will see everyone as going nr. Because of this,
     * this should generally only be used as a last resort when the server isn't
     * responding to requests to shut down normally.
     * 
     * @param serverid
     *            The id of the server to shut down
     */
    public static void killServer(int serverid)
    {
        LiveServer server = liveServers.get(serverid);
        if (server == null)
        {
            /*
             * The server isn't running, so we don't need to do anything to shut
             * it down.
             */
            return;
        }
        server.forceShutdown();
    }
    
    public static void doLogViewer(HttpServletRequest request, JspWriter out)
            throws IOException
    {
        if (request.getSession(false) == null
                || request.getSession().getAttribute("user") == null)
            throw new RuntimeException("You need to be logged in to view logs");
        String timezoneName = request.getParameter("timezone");
        TimeZone timezone;
        if (timezoneName == null)
            timezone = TimeZone.getDefault();
        else
            timezone = TimeZone.getTimeZone(timezoneName);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                Settings.datetimeformat.getString());
        dateFormat.setTimeZone(timezone);
        String startString = request.getParameter("start");
        String endString = request.getParameter("end");
        String textSearchString = request.getParameter("search");
        if (textSearchString == null)
            textSearchString = "";
        String textSearch = StringEscapeUtils.escapeSql(textSearchString);
        String textSearchLower = textSearch.toLowerCase();
        String ignoreCaseString = request.getParameter("caseignore");
        String[] textSearchInStrings = request.getParameterValues("searchin");
        if (textSearchString.equals(""))
            textSearchInStrings = null;
        String[] filterServerStrings = request.getParameterValues("server");
        // if filterServerStrings is null, then we'll show the logs of all
        // servers that this user has view-in-server-list on.
        String[] filterEvents = request.getParameterValues("event");
        if (filterEvents == null)
            filterEvents = Constants.LOG_EVENTS;
        // if filterEvents is null, then we'll show all events.
        int maxResults = 3000;
        // the maximum results number is pretty much hard-coded right now. It's
        // not intended for practical use, as restricting the interval is a much
        // more logical solution; it's simply to prevent a mis-formed request
        // from
        // tying up the server while it does something such as try to get all of
        // the logs for all time periods out of the database. The client then
        // warns
        // the user if the result count is 3000 that some results were truncated
        // and
        // that they should decrease the time interval.
        LogRequest filterObject = new LogRequest();
        filterObject.setStart(new Date(Long.parseLong(startString)));
        filterObject.setEnd(new Date(Long.parseLong(endString)));
        String filter = "";
        // filter by search string
        if (textSearchInStrings != null)
        {
            filter += " and ('1' = '2' ";
            for (String s : textSearchInStrings)
            {
                if (!StringUtils.isMemberOf(s, SEARCH_IN))
                {
                    throw new RuntimeException("Invalid searchin: " + s);
                }
                String filterColumn = s;
                String textSearchToUse = textSearch;
                if ("true".equalsIgnoreCase(ignoreCaseString))
                {
                    filterColumn = " lower(" + filterColumn + ") ";
                    textSearchToUse = textSearchLower;
                }
                filter += " or " + filterColumn + " like '%" + textSearchToUse
                        + "%'";
            }
        }
        if (textSearchInStrings != null)
            filter += " ) ";
        // filter by servers
        ArrayList<Integer> serverIds = new ArrayList<Integer>();
        if (filterServerStrings == null)
        {
            // no servers specified by the user, add them all
            Server[] allServers = DataStore.listServers();
            for (Server s : allServers)
            {
                /*
                 * We'll add all servers, including ones that this user doesn't
                 * have permission to view, since those servers will be removed
                 * further down in this method
                 */
                serverIds.add(s.getServerid());
            }
        }
        else
        {
            // parse the server ids specified by the user
            for (String s : filterServerStrings)
            {
                serverIds.add(Integer.parseInt(s));
            }
        }
        // remove servers the user doesn't have perms to view the logs of
        for (int server : new ArrayList<Integer>(serverIds))
        {
            if (!Perms.server("view-logs", server, GlobalLinkImpl
                    .getServerGroupId(server)))
                serverIds.remove((Integer) server);
        }
        // add the servers
        filter += " and ( '1' = '2' ";
        for (int server : serverIds)
        {
            filter += " or serverid = " + server + " ";
        }
        filter += " ) ";
        // filter by events
        filter += " and ( '1' = '2' ";
        for (String s : filterEvents)
        {
            filter += " or event = '" + StringEscapeUtils.escapeSql(s) + "' ";
        }
        filter += " ) ";
        // order by the date
        filter += " order by `when` asc ";
        // filter by max results
        filter += " limit " + maxResults;
        // add the filter
        filterObject.setFilter(filter);
        System.out.println("filter: " + filter);
        // We're done with the filtering. Now we run the query.
        LogEvent[] results = DataStore.searchLogs(filterObject);
        // Now we'll do the actual rendering.
        LogEventFormatter formatter = new LogEventFormatter(dateFormat);
        formatter.addColumn(LogEventColumn.when);
        formatter.addColumn(LogEventColumn.server);
        formatter.addColumn(LogEventColumn.event);
        formatter.addColumn(LogEventColumn.from);
        formatter.addColumn(LogEventColumn.to);
        formatter.addColumn(LogEventColumn.detail);
        out.println("<table border='0' cellspacing='0' cellpadding='0' ");
        out.println("style='width:100%'><tr><td>");
        out.println("Right now it's " + dateFormat.format(new Date()));
        out.println("</td><td align='right'>");
        out
                .println("Hover over an event in the color key below to see a description of it.");
        out.println("</td></tr></table>");
        out.println("<table border='0' cellspacing='1' cellpadding='1' "
                + "class='bznetwork-LogViewerTableKey'><tr>");
        /*
         * TODO: add the key for the table here, add a menu link at the bottom
         * of the search group in the logs viewer widget, action for it provided
         * by the user of the logs viewer widget
         */
        for (int i = 0; i < Constants.LOG_EVENTS.length; i++)
        {
            String event = Constants.LOG_EVENTS[i];
            out.println("<td style='text-align:center'"
                    + " class='lvtr-"
                    + event
                    + "' title=\""
                    + StringEscapeUtils
                            .escapeHtml(Constants.LOG_EVENT_DESCRIPTIONS[i])
                    + "\">" + event + "</td>");
        }
        out.println("</tr></table>");
        /*
         * We'll issue a warning if the user went over limit.
         */
        if (results.length >= maxResults)
        {
            out.println("<span style='color:#dd0000'>Event list truncated at "
                    + maxResults
                    + " events. Use a smaller interval to see the rest "
                    + "of the events.</span><br/>");
        }
        formatter.format(results, out);
        // We're done!
    }
    
    public static enum LogEventColumn
    {
        when("When", false)
        {
            
            @Override
            public String format(LogEvent event, LogEventFormatter formatter)
            {
                return formatter.formatDateTime(event.getWhen()).replace(" ",
                        "&nbsp;");
            }
        },
        from("From", false)
        {
            
            @Override
            public String format(LogEvent event, LogEventFormatter formatter)
            {
                String callsign = event.getSource();
                if (callsign == null || callsign.startsWith("+"))
                {
                    if (callsign != null && callsign.startsWith("+"))
                        return callsign.substring(1);
                    return "";
                }
                return "#" + event.getSourceid() + "&#160;<span class='lvtp-"
                        + event.getSourceteam() + "' title='Team: "
                        + event.getSourceteam() + "'>"
                        + StringEscapeUtils.escapeHtml(callsign) + "</span>";
            }
        },
        to("To", false)
        {
            
            @Override
            public String format(LogEvent event, LogEventFormatter formatter)
            {
                String callsign = event.getTarget();
                if (callsign == null || callsign.startsWith("+"))
                {
                    if (event.getTargetteam() != null)
                        return event.getTargetteam();
                    else if (callsign != null && callsign.startsWith("+"))
                        return callsign.substring(1);
                    return "";
                }
                return "#" + event.getTargetid() + "&#160;<span class='lvtp-"
                        + event.getTargetteam() + "' title='Team: "
                        + event.getTargetteam() + "'>"
                        + StringEscapeUtils.escapeHtml(callsign) + "</span>";
            }
        },
        event("Event", true)
        {
            
            @Override
            public String format(LogEvent event, LogEventFormatter formatter)
            {
                return event.getEvent();
            }
        },
        detail("Detail", true)
        {
            
            @Override
            public String format(LogEvent event, LogEventFormatter formatter)
            {
                String name = event.getEvent();
                if (name.startsWith("chat-"))
                {
                    return event.getData();
                }
                else if (name.equalsIgnoreCase("join"))
                {
                    String conditionalBzid = " BZID:" + event.getBzid();
                    if (event.getBzid() == null
                            || event.getBzid().trim().equals(""))
                        conditionalBzid = "";
                    return event.getData() + conditionalBzid + " IP:"
                            + event.getIpaddress() + " Email:"
                            + event.getEmail();
                }
                else if (name.equalsIgnoreCase("part"))
                {
                    String conditionalBzid = "BZID:" + event.getBzid() + " ";
                    if (event.getBzid() == null
                            || event.getBzid().trim().equals(""))
                        conditionalBzid = "";
                    return conditionalBzid + "IP:" + event.getIpaddress()
                            + " Reason:" + event.getData();
                }
                else if (name.equalsIgnoreCase("report"))
                {
                    return event.getData();
                }
                else if (name.equalsIgnoreCase("slashcommand"))
                {
                    return event.getData();
                }
                else if (name.equalsIgnoreCase("filtered"))
                {
                    return event.getData();
                }
                else if (name.equalsIgnoreCase("stdout"))
                {
                    return event.getData();
                }
                else if (name.equalsIgnoreCase("status"))
                {
                    return event.getData();
                }
                else
                {
                    return "UNKNOWN EVENT";
                }
            }
        },
        server("Server", true)
        {
            
            @Override
            public String format(LogEvent event, LogEventFormatter formatter)
            {
                return formatter.getServerName(event.getServerid());
            }
        };
        private String name;
        private boolean escaped;
        
        private LogEventColumn(String name, boolean escaped)
        {
            this.name = name;
            this.escaped = escaped;
        }
        
        public String getColumnName()
        {
            return name;
        }
        
        public boolean isEscaped()
        {
            return escaped;
        }
        
        public abstract String format(LogEvent event,
                LogEventFormatter formatter);
    }
    
    public static class LogEventFormatter
    {
        private HashMap<Integer, Server> serverCache = new HashMap<Integer, Server>();
        private ArrayList<LogEventColumn> columns = new ArrayList<LogEventColumn>();
        private SimpleDateFormat format;
        
        public LogEventFormatter(SimpleDateFormat format)
        {
            this.format = format;
        }
        
        public void addColumn(LogEventColumn column)
        {
            if (!columns.contains(column))
                columns.add(column);
        }
        
        public String formatDateTime(Date when)
        {
            return format.format(when);
        }
        
        public void format(LogEvent[] events, JspWriter writer)
                throws IOException
        {
            writer.println("<table border='0' cellspacing='1' cellpadding='1' "
                    + "class='bznetwork-LogViewerTable'>");
            writer.println("<tr class='bznetwork-LogViewerTable-header'>");
            for (LogEventColumn column : columns)
            {
                writer.println("<td>"
                        + StringEscapeUtils.escapeHtml(column.getColumnName())
                        + "</td>");
            }
            writer.println("</tr>");
            for (LogEvent event : events)
            {
                writer.println("<tr class='lvtr-" + event.getEvent() + "'>");
                for (LogEventColumn column : columns)
                {
                    String columnValue = column.format(event, this);
                    if (column.isEscaped())
                        columnValue = StringEscapeUtils.escapeHtml(columnValue);
                    writer.println("<td class='lvtr-" + event.getEvent() + "'>"
                            + columnValue + "</td>");
                }
                writer.println("</tr>");
            }
            writer.println("</table>");
        }
        
        public String getServerName(int serverid)
        {
            Server server = serverCache.get(serverid);
            if (server == null)
                server = DataStore.getServerById(serverid);
            if (server == null)
                return "+unknown";
            serverCache.put(serverid, server);
            return server.getName();
        }
    }
    
    /**
     * The double space between the date and time is intentional.
     */
    
    private static SimpleDateFormat dateFormat = null;
    
    public static String formatDateTime(Date when)
    {
        return dateFormat.format(when);
    }
    
    /**
     * Logs a server event and runs any triggers applicable to the event.
     * 
     * @param event
     */
    public static void logEvent(String serverName, LogEvent event)
    {
        DataStore.addLogEvent(event);
        LinkedHashMap<String, XData> vars = new LinkedHashMap<String, XData>();
        // serverid,event,source,target,sourceid,targetid,sourceteam,
        // targetteam,ipaddress,bzid,email,metadata,data
        vars.put("serverid", new XNumber(event.getServerid()));
        if (serverName != null)
            vars.put("servername", new XString(serverName));
        vars.put("event", new XString(event.getEvent()));
        vars.put("source", new XString(event.getSource()));
        vars.put("target", new XString(event.getTarget()));
        vars.put("sourceid", new XNumber(event.getSourceid()));
        vars.put("targetid", new XNumber(event.getTargetid()));
        vars.put("sourceteam", new XString(event.getSourceteam()));
        vars.put("targetteam", new XString(event.getTargetteam()));
        vars.put("ipaddress", new XString(event.getIpaddress()));
        vars.put("bzid", new XString(event.getBzid()));
        vars.put("email", new XString(event.getEmail()));
        vars.put("metadata", new XString(event.getMetadata()));
        vars.put("data", new XString(event.getData()));
        runTriggers("server:" + event.getEvent(), event.getServerid(), -1,
                TargetType.server, vars);
    }
    
    /**
     * Logs an action and runs any triggers applicable to the action.
     * 
     * @param action
     */
    public static void logAction(Action action)
    {
        DataStore.addActionEvent(action);
    }
    
    /**
     * Runs the triggers for the specified target.
     * 
     * @param event
     *            The name of the event that occured. For example, server:report
     *            or action:logged-in.
     * @param target
     *            The target's id. This is either a server id, a group id, or -1
     *            for global.
     * @param groupcache
     *            If the target is a server, then this can be the server's
     *            parent group id. If the parent group's id is not known, then
     *            this can be set to -1 and this method will ask the database
     *            for the server's parent group id. This serves mostly to make
     *            this method faster by requiring it to execute less queries
     *            against the database.
     * @param targetType
     *            The type of target.
     * @param vars
     *            Variables that should be set when the trigger's subject and
     *            message are evaluated as XSM-inline. In the future, when
     *            printf notation is supported, the list of values returned from
     *            this map will also be used as the inputs to printf.
     */
    public static void runTriggers(String event, int target, int groupcache,
            Constants.TargetType targetType, Map<String, XData> vars)
    {
        try
        {
            ArrayList<Trigger> triggersToRun = new ArrayList<Trigger>();
            triggersToRun.addAll(Arrays.asList(DataStore
                    .listTriggersByTargetAndEvent(new TargetEventPair(event,
                            target))));
            if (targetType == TargetType.server)
            {
                if (groupcache == -1)
                    groupcache = GlobalLinkImpl.getServerGroupId(target);
                targetType = TargetType.group;
                target = groupcache;
                triggersToRun.addAll(Arrays.asList(DataStore
                        .listTriggersByTargetAndEvent(new TargetEventPair(
                                event, target))));
            }
            if (targetType == TargetType.banfile
                    || targetType == TargetType.group)
            {
                targetType = TargetType.global;
                target = -1;
                triggersToRun.addAll(Arrays.asList(DataStore
                        .listTriggersByTargetAndEvent(new TargetEventPair(
                                event, target))));
            }
            ArrayList<Trigger> uniqueTriggers = new ArrayList<Trigger>();
            for (Trigger t : new ArrayList<Trigger>(triggersToRun))
            {
                if (!uniqueTriggers.contains(t))
                    uniqueTriggers.add(t);
            }
            /*
             * We have our list of triggers. Now we'll run them.
             */
            for (Trigger trigger : uniqueTriggers)
            {
                /*
                 * First, we'll evaluate the subject and the message of the
                 * trigger.
                 */
                String subject = ServerSideTextScripter.run(trigger
                        .getSubject(), vars, new VXVars());
                String message = ServerSideTextScripter.run(trigger
                        .getMessage(), vars, new VXVars());
                /*
                 * We have the subject and the message to send. Now we'll look
                 * up the recipient and send the message.
                 */
                if (trigger.getSendtype().equals("ircbot"))
                    runTriggerIrcRecipient(trigger.getRecipient(), subject,
                            message);
                else if (trigger.getSendtype().equals("emailgroup"))
                    runTriggerEmailGroupRecipient(trigger.getRecipient(),
                            subject, message);
                else
                    System.err.println("Unrecognized send type for trigger "
                            + trigger.getTriggerid() + ": "
                            + trigger.getSendtype());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static void runTriggerEmailGroupRecipient(int recipient,
            String subject, String message)
    {
        System.err.println("Email groups aren't supported yet.");
    }
    
    private static void runTriggerIrcRecipient(int recipient, String subject,
            String message)
    {
        IrcServerBot bot = ircServerBotMap.get(recipient);
        if (bot == null)
        {
            System.err.println("Recipient bot id " + recipient
                    + " has no matching live IRC bot.");
            return;
        }
        try
        {
            if (bot.isConnected())
            {
                String[] messages = message.trim().split("\n");
                for (String s : messages)
                {
                    if (!s.trim().equals(""))
                        bot.sendMessage(bot.getBot().getChannel(), s);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void notifyIrcBotDeleted(int botid)
    {
        synchronized (ircServerBotMap)
        {
            IrcServerBot bot = ircServerBotMap.get(botid);
            if (bot != null)
            {
                bot.bznDestroy();
                ircServerBotMap.remove(botid);
            }
        }
    }
    
    public static void notifyIrcReconnectRequested()
    {
        synchronized (ircServerBotMap)
        {
            /*
             * We'll iterate over a newly-constructed list here to avoid getting
             * a ConcurrentModificationException since we're going to modify the
             * list during iteration
             */
            for (Entry<Integer, IrcServerBot> entry : new ArrayList<Entry<Integer, IrcServerBot>>(
                    ircServerBotMap.entrySet()))
            {
                int botid = entry.getKey();
                IrcServerBot bot = entry.getValue();
                bot.bznDestroy();
                ircServerBotMap.remove(botid);
            }
            /*
             * Now we'll get the list of bots from the database and create new
             * bot objects for them.
             */
            IrcBot[] botList = DataStore.listIrcBots();
            for (IrcBot bot : botList)
            {
                IrcServerBot serverBot = new IrcServerBot(bot);
                ircServerBotMap.put(bot.getBotid(), serverBot);
            }
            /*
             * ...and we're done!
             */
        }
    }
    
    public static void notifyIrcBotAdded(IrcBot bot)
    {
        synchronized (ircServerBotMap)
        {
            IrcServerBot serverBot = new IrcServerBot(bot);
            ircServerBotMap.put(bot.getBotid(), serverBot);
        }
    }
    
    public static void notifyIrcBotUpdated(IrcBot oldBot, IrcBot newBot)
    {
        synchronized (ircServerBotMap)
        {
            IrcServerBot bot = ircServerBotMap.get(newBot.getBotid());
            if (bot != null)
            {
                bot.bznDestroy();
                ircServerBotMap.remove(newBot.getBotid());
            }
            bot = new IrcServerBot(newBot);
            ircServerBotMap.put(newBot.getBotid(), bot);
        }
    }
    
    public static IrcServerBot getServerBot(int botid)
    {
        synchronized (ircServerBotMap)
        {
            return ircServerBotMap.get(botid);
        }
    }
    
    public static void sendIrcBotMessage(int botid, String message)
    {
        IrcServerBot bot = getServerBot(botid);
        if (bot != null)
            bot.sendMessage(bot.getBot().getChannel(), message);
    }
}
