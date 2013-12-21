package jw.bznetwork.client;

public class Verify
{
    public static void global(String name)
    {
        if (!Perms.global(name))
            throw new PermissionDeniedException("global: " + name);
    }
    
    public static void group(String name, int id)
    {
        if (!Perms.group(name, id))
            throw new PermissionDeniedException("group: " + name + " on " + id);
    }
    
    public static void banfile(String name, int id)
    {
        if (!Perms.banfile(name, id))
            throw new PermissionDeniedException("banfile: " + name + " on "
                    + id);
    }
    
    public static void server(String name, int serverid, int groupid)
    {
        if (!Perms.server(name, serverid, groupid))
            throw new PermissionDeniedException("server: " + name + " on "
                    + serverid + "," + groupid);
    }
    
}
