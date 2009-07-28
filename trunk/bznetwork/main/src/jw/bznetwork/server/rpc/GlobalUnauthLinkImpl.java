package jw.bznetwork.server.rpc;

import javax.servlet.http.HttpSession;

import jw.bznetwork.client.AuthProvider;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.rpc.GlobalUnauthLink;
import jw.bznetwork.server.BZNetworkServer;
import jw.bznetwork.server.data.DataStore;

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
        Configuration allConfig = DataStore.getConfiguration();
        Configuration publicConfig = new Configuration();
        publicConfig.setSitename(allConfig.getSitename());
        return publicConfig;
    }
    
    @Override
    public AuthProvider[] listEnabledAuthProviders()
    {
        return BZNetworkServer.getEnabledAuthProviders();
    }
}
