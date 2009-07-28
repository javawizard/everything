package jw.bznetwork.client.data.model;

import java.io.Serializable;

/**
 * A permission. Note that the equals method returns true if two permissions
 * have the same target and permission name, regardless of whether or not they
 * are from the same role.
 * 
 * @author Alexander Boyd
 * 
 */
public class Permission implements Serializable
{
    private int roleid;
    private String permission;
    private int target;
    
    public int getRoleid()
    {
        return roleid;
    }
    
    public void setRoleid(int roleid)
    {
        this.roleid = roleid;
    }
    
    public String getPermission()
    {
        return permission;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((permission == null) ? 0 : permission.hashCode());
        result = prime * result + target;
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Permission other = (Permission) obj;
        if (permission == null)
        {
            if (other.permission != null)
                return false;
        }
        else if (!permission.equals(other.permission))
            return false;
        if (target != other.target)
            return false;
        return true;
    }
    
    public void setPermission(String permission)
    {
        this.permission = permission;
    }
    
    public int getTarget()
    {
        return target;
    }
    
    public void setTarget(int target)
    {
        this.target = target;
    }
}
