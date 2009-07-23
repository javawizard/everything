package jw.bznetwork.server;

import java.io.BufferedReader;
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
    
    /**
     * Sticks information on to the request indicating that the user has just
     * logged in.
     * 
     * @param request
     * @param provider
     * @param username
     * @param roles
     */
    public static void login(HttpServletRequest request, String provider,
            String username, String[] roles)
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
        try
        {
            props
                    .load(context
                            .getResourceAsStream("/WEB-INF/config/enabled-auth-providers.props"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return props;
    }
    
    public static AuthProvider[] getEnabledAuthProviders()
    {
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
            String generalDataConfig = StringUtils.readStream(getClass()
                    .getResourceAsStream("/general-sql.xml"));
            /*
             * FIXME: Some sort of configuration mechanism needs to be added for
             * this, so that when bznetwork is actually deployed, it can connect
             * to the real postgres database. For now, bznetwork will be
             * hard-coded to use an h2 database.
             */
            String dbLocation = "/home/amboyd/bznetwork-db";
            if (System.getenv("BZNETWORK_DATABASE_URL") != null)
            {
                dbLocation = System.getenv("BZNETWORK_DATABASE_URL");
                System.out.println("Using alternate data storage location "
                        + dbLocation);
            }
            generalDataConfig = generalDataConfig.replace("$$driver$$",
                    "org.h2.Driver");
            generalDataConfig = generalDataConfig.replace("$$url$$", "jdbc:h2:"
                    + dbLocation);
            generalDataConfig = generalDataConfig.replace("$$username$$", "sa");
            generalDataConfig = generalDataConfig.replace("$$password$$", "");
            try
            {
                Class.forName("org.h2.Driver");
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            generalDataClient = SqlMapClientBuilder
                    .buildSqlMapClient(new StringReader(generalDataConfig));
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
    
}
