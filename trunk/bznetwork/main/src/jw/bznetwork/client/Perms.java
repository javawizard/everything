package jw.bznetwork.client;

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
    
    /**
     * Returns true if the currently-authenticated user has the specified
     * permission on the specified target.
     * 
     * @param perm
     *            The permission to check for
     * @param target
     *            The target on which to check for the permission. -1 represents
     *            the global target.
     * @return True if the target or any of its parents have the specified
     *         permission for the specified user, false if they do not
     */
    public boolean has(String perm, int target)
    {
        if (provider == null)
            throw new IllegalStateException(
                    "A provider has not yet been installed into the Perms class");
        return provider.has(perm, target);
    }
}
