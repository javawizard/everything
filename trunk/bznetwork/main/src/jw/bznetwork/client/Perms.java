package jw.bznetwork.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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
    /**
     * A string array of all supported permissions. After each permission's name
     * is a comma and then a number. The number represents what the permission
     * can be applied to. 1 is global, 2 is group, and 3 is server.
     */
    private static String[] allPermissionsArray = new String[]
    {
            "manage-users,1", "manage-roles,1", "manage-auth,1", "view-logs,3",
            "view-user,1", "cross-reference-user,1", "edit-map,3",
            "edit-groupdb,3", "inherit-parent-groupdb,3",
            "edit-group-groupdb,2", "edit-server-settings,3",
            "manage-callsign-auth,1", "start-stop-server,3", "create-server,2",
            "delete-server,2", "add-group-ipban,2", "add-group-idban,2",
            "add-group-hostban,2", "add-long-ban,2", "delete-group-ban,2",
            "delete-self-group-ban,2", "view-reports,3", "say,3",
            "hidden-say,3", "view-in-server-list,3", "edit-server-notes,3",
            "edit-group-notes,2", "view-in-group-list,2", "view-action-log,1",
            "clear-action-log,1", "edit-configuration,1", "all,3"
    
    };
    
    private static HashSet<String> allPermissions = new HashSet<String>();
    
    private static HashMap<String, Integer> allPermissionLevels = new HashMap<String, Integer>();
    
    static
    {
        for (String s : allPermissionsArray)
        {
            String[] tokens = s.split("\\,");
            allPermissions.add(tokens[0]);
            allPermissionLevels.put(tokens[0], Integer.parseInt(tokens[1]));
        }
    }
    
    /**
     * Returns an alphabetized list of all supported permissions.
     * 
     * @return
     */
    public static String[] getSortedPermissionsList()
    {
        String[] result = allPermissions.toArray(new String[0]);
        Arrays.sort(result);
        return result;
    }
    
    public boolean isPermissionValid(String permission)
    {
        return allPermissions.contains(permission);
    }
    
    public int getPermissionLevel(String permission)
    {
        return allPermissionLevels.get(permission);
    }
    
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
