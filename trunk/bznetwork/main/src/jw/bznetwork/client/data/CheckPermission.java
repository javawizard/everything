package jw.bznetwork.client.data;

import java.io.Serializable;

public class CheckPermission implements Serializable
{
    private String permission;
    
    public CheckPermission()
    {
        super();
    }
    
    private int target;
    
    public String getPermission()
    {
        return permission;
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
    
    @Override
    public int hashCode()
    {
        return (permission.hashCode() + target) % 0xFFFFFF;
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
        CheckPermission other = (CheckPermission) obj;
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
    
    public CheckPermission(String permission, int target)
    {
        super();
        this.permission = permission;
        this.target = target;
    }
}
