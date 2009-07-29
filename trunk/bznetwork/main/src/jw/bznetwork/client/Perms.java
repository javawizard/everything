package jw.bznetwork.client;

import jw.bznetwork.client.data.model.Server;

/**
 * Holds information about what permissions the currently-authenticated user
 * has. This is the class typically queried by client-side code to see if a user
 * has a particular permission on a particular target. Server-side code uses
 * methods on DataStore.<br/><br/>
 * 
 * This class properly resolves hierarchical nesting of permissions so that, for
 * example, a permission granted on a particular group will cause that
 * permission to appear on all servers within that group according to this
 * class. Permissions that are scoped to a particular level can still be seen in
 * this manner from entities below this level. This means that if a user has
 * create-server on a particular group, and this class is asked if that user has
 * create-server on a server within that group, it will return true, even though
 * create-server is a group-level permission.
 * 
 * @author Alexander Boyd
 * 
 */
public class Perms
{
    private static PermissionsProvider provider;
    
    public static void installProvider(PermissionsProvider provider)
    {
        Perms.provider = provider;
    }
    
    public static boolean global(String name)
    {
        if (provider == null)
            throw new IllegalStateException("No provider installed");
        return provider.hasGlobalPermission(name);
    }
    
    public static boolean group(String name, int id)
    {
        if (provider == null)
            throw new IllegalStateException("No provider installed");
        return provider.hasPermissionOnGroup(name, id);
    }
    
    public static boolean server(String name, Server server)
    {
        return server(name, server.getServerid(), server.getGroupid());
    }
    
    public static boolean server(String name, int serverid, int groupid)
    {
        if (provider == null)
            throw new IllegalStateException("No provider installed");
        return provider.hasPermissionOnServer(name, groupid, serverid);
    }
}
