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
     * is a comma and then the lowest level that the permission can be applied
     * to. Permission level inheritance is specified in levelInheritance
     */
    private static String[] allPermissionsArray = new String[]
    {
            "manage-users,global", "manage-roles,global", "manage-auth,global",
            "view-logs,server", "view-user,global",
            "cross-reference-user,global", "edit-map,server",
            "edit-groupdb,server", "inherit-parent-groupdb,server",
            "edit-group-groupdb,server", "edit-server-settings,server",
            "manage-callsign-auth,global", "start-stop-server,server",
            "create-server,server", "delete-server,server",
            "manage-banfiles,global", "view-bans,banfile", "add-ipban,banfile",
            "add-idban,banfile", "add-hostban,banfile", "add-long-ban,banfile",
            "delete-ban,banfile", "delete-self-ban,banfile",
            "edit-server-banfile,server", "edit-group-banfile,group",
            "say,server", "hidden-say,server", "view-in-server-list,server",
            "edit-server-notes,server", "edit-group-notes,group",
            "view-in-group-list,group", "view-action-log,global",
            "clear-action-log,global", "edit-configuration,global",
            "all,server", "view-sessions,global", "create-group,global",
            "rename-group,group", "manage-irc,global",
            "manage-triggers,global", "manage-email-groups,global"
    
    };
    
    private static String[] levelInheritance = new String[]
    {
            "global", "group,global", "server,group,global", "banfile,global"
    };
    
    private static HashSet<String> allPermissions = new HashSet<String>();
    
    private static HashMap<String, String[]> allPermissionLevels = new HashMap<String, String[]>();
    
    static
    {
        HashMap<String, String[]> levelInheritanceMap = new HashMap<String, String[]>();
        for (String s : levelInheritance)
        {
            String[] tokens = s.split("\\,");
            levelInheritanceMap.put(tokens[0], tokens);
        }
        for (String s : allPermissionsArray)
        {
            try
            {
                String[] tokens = s.split("\\,");
                allPermissions.add(tokens[0]);
                allPermissionLevels.put(tokens[0], levelInheritanceMap
                        .get(tokens[1]));
            }
            catch (Exception e)
            {
                throw new RuntimeException("Exception processing perm line "
                        + s);
            }
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
    
    public static boolean isPermissionValid(String permission)
    {
        return allPermissions.contains(permission);
    }
    
    public static String[] getPermissionLevels(String permission)
    {
        return allPermissionLevels.get(permission);
    }
    
    public static boolean isPermissionLevelValid(String permission, String level)
    {
        String[] levels = getPermissionLevels(permission);
        for (String l : levels)
        {
            if (l.equals(level))
                return true;
        }
        return false;
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
    
    public static boolean banfile(String name, int id)
    {
        if (provider == null)
            throw new IllegalStateException();
        return provider.hasPermissionOnBanfile(name, id);
    }
    
    public static boolean server(String name, int serverid, int groupid)
    {
        if (provider == null)
            throw new IllegalStateException("No provider installed");
        return provider.hasPermissionOnServer(name, groupid, serverid);
    }
}
