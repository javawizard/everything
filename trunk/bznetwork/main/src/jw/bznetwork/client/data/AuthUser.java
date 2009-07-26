package jw.bznetwork.client.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Represents a user logged into the system.
 * 
 * @author Alexander Boyd
 * 
 */
public class AuthUser implements Serializable
{
    private String provider;
    private String username;
    private int[] roles;
    private ArrayList<String> roleNames = new ArrayList<String>();
    
    public String getProvider()
    {
        return provider;
    }
    
    public void setProvider(String provider)
    {
        this.provider = provider;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public int[] getRoles()
    {
        return roles;
    }
    
    public void setRoles(int[] roles)
    {
        this.roles = roles;
    }
    
    public ArrayList<String> getRoleNames()
    {
        return roleNames;
    }
    
    public HashSet<CheckPermission> getPermissions()
    {
        return permissions;
    }
    
    private HashSet<CheckPermission> permissions = new HashSet<CheckPermission>();
}
