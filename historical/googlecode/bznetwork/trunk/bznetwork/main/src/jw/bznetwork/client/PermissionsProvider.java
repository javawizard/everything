package jw.bznetwork.client;

/**
 * An interface that defines methods for checking whether a particular user is
 * authorized to perform a particular action. An instance of this interface must
 * be installed in {@link Perms} before <tt>Perms</tt> will function properly,
 * and it will delegate to the instance provided to it.
 * 
 * @author Alexander Boyd
 * 
 */
public interface PermissionsProvider
{
    public boolean hasPermissionOnGroup(String permission, int group);
    
    public boolean hasPermissionOnServer(String permission, int group,
            int server);
    
    public boolean hasGlobalPermission(String permission);
    
    public boolean hasPermissionOnBanfile(String name, int id);
}
