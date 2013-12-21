package jw.bznetwork.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jw.bznetwork.client.PermissionsProvider;

/**
 * A permissions provider that looks up permissions on another
 * PermissionsProvider object (which is usually a ClientPermissionsProvier)
 * stored on the user's session. Permissions methods throw an exception if they
 * are not called within a request (they access the request by using the
 * RequestTrackerFilter), and deny all permissions if the request has no session
 * or if the session does not have a permissions provider associated with it
 * (via the permissions-provider session attribute).
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerPermissionsProvider implements PermissionsProvider
{
    /**
     * A permissions provider that denies all permissions.
     */
    static final PermissionsProvider NULL_PROVIDER = new PermissionsProvider()
    {
        
        @Override
        public boolean hasGlobalPermission(String permission)
        {
            return false;
        }
        
        @Override
        public boolean hasPermissionOnGroup(String permission, int group)
        {
            return false;
        }
        
        @Override
        public boolean hasPermissionOnServer(String permission, int group,
                int server)
        {
            return false;
        }
        
        @Override
        public boolean hasPermissionOnBanfile(String name, int id)
        {
            return false;
        }
    };
    
    @Override
    public boolean hasGlobalPermission(String permission)
    {
        return getUserProvider().hasGlobalPermission(permission);
    }
    
    @Override
    public boolean hasPermissionOnGroup(String permission, int group)
    {
        return getUserProvider().hasPermissionOnGroup(permission, group);
    }
    
    @Override
    public boolean hasPermissionOnBanfile(String permission, int banfile)
    {
        return getUserProvider().hasPermissionOnBanfile(permission, banfile);
    }
    
    @Override
    public boolean hasPermissionOnServer(String permission, int group,
            int server)
    {
        return getUserProvider().hasPermissionOnServer(permission, group,
                server);
    }
    
    /**
     * Returns the user's provider, or returns {@link #NULL_PROVIDER} if the
     * user does not have a permissions provider on their session or if the user
     * does not have a session.
     * 
     * @return
     */
    private PermissionsProvider getUserProvider()
    {
        HttpServletRequest request = RequestTrackerFilter.getCurrentRequest();
        if (request == null)
            throw new IllegalStateException("Not within a request");
        HttpSession session = request.getSession(false);
        if (session == null)
            return NULL_PROVIDER;
        PermissionsProvider provider = (PermissionsProvider) session
                .getAttribute("permissions-provider");
        if (provider == null)
            return NULL_PROVIDER;
        return provider;
    }
    
}
