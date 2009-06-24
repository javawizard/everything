package jw.bznetwork.server;

import java.io.StringReader;

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
        String generalDataConfig =
            StringUtils.readStream(getClass().getResourceAsStream("/general-sql.xml"));
        /*
         * FIXME: Some sort of configuration mechanism needs to be added for
         * this, so that when bznetwork is actually deployed, it can connect to
         * the real postgres database. For now, bznetwork will be hard-coded to
         * use an h2 database.
         */
        String dbLocation = "/home/amboyd/bznetwork-db";
        if (System.getenv("BZNETWORK_DATABASE_URL") != null)
        {
            dbLocation = System.getenv("BZNETWORK_DATABASE_URL");
            System.out.println("Using alternate data storage location " + dbLocation);
        }
        generalDataConfig = generalDataConfig.replace("$$driver$$", "org.h2.Driver");
        generalDataConfig =
            generalDataConfig.replace("$$url$$", "jdbc:h2:" + dbLocation);
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
        generalDataClient =
            SqlMapClientBuilder.buildSqlMapClient(new StringReader(generalDataConfig));
    }
    
}
