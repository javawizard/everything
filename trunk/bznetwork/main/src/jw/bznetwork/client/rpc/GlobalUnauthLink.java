package jw.bznetwork.client.rpc;

import jw.bznetwork.client.AuthProvider;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.model.Configuration;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("unauth-link")
public interface GlobalUnauthLink extends RemoteService
{
    /**
     * Returns the user that is currently logged in, or null if the user is not
     * logged in.
     * 
     * @return
     */
    public AuthUser getThisUser();
    
    /**
     * Gets a list of all enabled auth providers.
     * 
     * @return
     */
    public AuthProvider[] listEnabledAuthProviders();
    
    /**
     * Returns configuration options which should be publicly available.
     * Currently, this is just the site name.
     * 
     * @return
     */
    public Configuration getPublicConfiguration();
}
