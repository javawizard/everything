package jw.bznetwork.server.rpc;

import javax.servlet.http.HttpSession;

import jw.bznetwork.client.AuthProvider;
import jw.bznetwork.client.Settings;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.data.model.User;
import jw.bznetwork.client.rpc.GlobalUnauthLink;
import jw.bznetwork.server.BZNetworkServer;
import jw.bznetwork.server.LoginException;
import jw.bznetwork.server.RequestTrackerFilter;
import jw.bznetwork.server.data.DataStore;
import net.sf.opengroove.common.security.Hash;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GlobalUnauthLinkImpl extends RemoteServiceServlet implements
        GlobalUnauthLink
{
    
    @Override
    public AuthUser getThisUser()
    {
        HttpSession session = getThreadLocalRequest().getSession(false);
        if (session == null)
            return null;
        return (AuthUser) session.getAttribute("user");
    }
    
    @Override
    public Configuration getPublicConfiguration()
    {
        if (!BZNetworkServer.isInstalled())
            return null;
        Configuration publicConfig = new Configuration();
        publicConfig
                .setString(Settings.sitename, Settings.sitename.getString());
        if (getThisUser() != null)
        {
            for (Settings s : Settings.values())
            {
                publicConfig.setString(s, s.getString());
            }
        }
        return publicConfig;
    }
    
    @Override
    public AuthProvider[] listEnabledAuthProviders()
    {
        return BZNetworkServer.getEnabledAuthProviders();
    }
    
    @Override
    public String login(String username, String password)
    {
        String incorrectAuth = "Incorrect username or password.";
        User user = DataStore.getUserByUsername(username);
        if (user == null)
        /*
         * Incorrect username
         */
        {
            return incorrectAuth;
        }
        String enteredPasswordEnc = Hash.hash(password);
        if (!enteredPasswordEnc.equals(user.getPassword()))
        /*
         * Incorrect password
         */
        {
            return incorrectAuth;
        }
        try
        {
            BZNetworkServer.login(RequestTrackerFilter.getCurrentRequest(),
                    "internal", username, new int[]
                    {
                        user.getRole()
                    });
        }
        catch (LoginException e)
        {
            return e.getMessage();
        }
        return null;
    }
    
}
