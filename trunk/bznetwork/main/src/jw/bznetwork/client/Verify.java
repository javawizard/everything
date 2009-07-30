package jw.bznetwork.client;

public class Verify
{
    public static void vglobal(String name)
    {
        if (!Perms.global(name))
            throw new PermissionDeniedException("global: " + name);
    }
    
    public static void vgroup(String name, int id)
    {
        if (!Perms.group(name, id))
            throw new PermissionDeniedException("group: " + name);
    }
    
    public static void vserver(String name, int serverid, int groupid)
    {
        if (!Perms.server(name, serverid, groupid))
            throw new PermissionDeniedException("server: " + name);
    }
    
}
