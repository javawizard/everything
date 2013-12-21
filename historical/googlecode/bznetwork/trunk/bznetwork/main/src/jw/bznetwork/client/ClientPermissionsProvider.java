package jw.bznetwork.client;

import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.CheckPermission;

/**
 * A permissions provier that acts on an AuthUser object supplied to it on
 * construction. Despite the name, an instance of ClientPermissionsProvider is
 * also used on the server, but on a per-session basis, with a
 * ServerPermissionsProvider that wraps it and looks up the user's session (the
 * user's ClientPermissionsProvier is stored as the permissions-provider
 * attribute) on a thread-local basis.
 * 
 * @author Alexander Boyd
 * 
 */
public class ClientPermissionsProvider implements PermissionsProvider
{
    private AuthUser user;
    
    public ClientPermissionsProvider(AuthUser user)
    {
        this.user = user;
    }
    
    @Override
    public boolean hasGlobalPermission(String permission)
    {
        if (user.getPermissions().contains(new CheckPermission(permission, -1)))
            return true;
        if (user.getPermissions().contains(new CheckPermission("all", -1)))
            return true;
        return false;
    }
    
    @Override
    public boolean hasPermissionOnGroup(String permission, int group)
    {
        if (user.getPermissions().contains(
                new CheckPermission(permission, group)))
            return true;
        if (user.getPermissions().contains(new CheckPermission("all", group)))
            return true;
        return hasGlobalPermission(permission);
    }
    
    @Override
    public boolean hasPermissionOnBanfile(String permission, int banfile)
    {
        if (user.getPermissions().contains(
                new CheckPermission(permission, banfile)))
            return true;
        if (user.getPermissions().contains(new CheckPermission("all", banfile)))
            return true;
        return hasGlobalPermission(permission);
    }
    
    @Override
    public boolean hasPermissionOnServer(String permission, int group,
            int server)
    {
        if (user.getPermissions().contains(
                new CheckPermission(permission, server)))
            return true;
        if (user.getPermissions().contains(new CheckPermission("all", server)))
            return true;
        return hasPermissionOnGroup(permission, group);
    }
    
}
