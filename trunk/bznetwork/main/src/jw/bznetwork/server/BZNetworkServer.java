package jw.bznetwork.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

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
    
}
