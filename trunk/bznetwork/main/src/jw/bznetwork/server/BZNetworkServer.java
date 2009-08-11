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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.sf.opengroove.common.utils.DataUtils;

import jw.bznetwork.client.AuthProvider;
import jw.bznetwork.client.ClientPermissionsProvider;
import jw.bznetwork.client.Perms;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.CheckPermission;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.server.data.DataStore;
import jw.bznetwork.server.live.LiveServer;
import jw.bznetwork.server.live.ReadThread;
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
    public static final String newline = System.getProperty("line.separator");
    
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
                    "serverControl.so");
            bznetworkPlugin = new File(includedPluginsFolder,
                    "libbz_iplugin_bznetwork.so");
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
            Statement st = con2.createStatement();
            st.executeUpdate(StringUtils.readFile(new File(getServletContext()
                    .getRealPath("/WEB-INF/tables.sql"))));
            st.close();
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
            String possibleRestartMessage = "V";
            try
            {
                load();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                possibleRestartMessage = "Restart the web server, then v";
                lockAccess(installedLockMessage);
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
            Group group = DataStore.getGroupById(server.getGroupid());
            if (server == null)
                throw new IllegalArgumentException(
                        "There is no server with the id " + id);
            if (group == null)
                throw new IllegalStateException("Orphaned server");
            if (server.getPort() == 0 || server.getPort() == -1)
                throw new IllegalStateException(
                        "The port for this server hasn't been set yet.");
            LiveServer liveServer = new LiveServer();
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
            Configuration config = DataStore.getConfiguration();
            String executable = config.getExecutable();
            Process process;
            ArrayList<String> args = new ArrayList<String>();
            args.add(executable);
            args.add("-p");
            args.add("" + server.getPort());
            if (server.isListed())
            {
                args.add("-public");
                args.add(server.getName());
            }
            args.add("-world");
            args.add(mapFile.getAbsolutePath());
            args.add("-conf");
            args.add(configFile.getAbsolutePath());
            args.add("-groupdb");
            args.add(newGroupdbFile.getAbsolutePath());
            args.add("-banfile");
            args.add(banfileFile.getAbsolutePath());
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
                        .start();
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
    private static String generateRandomName()
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
}
