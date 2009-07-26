package jw.bznetwork.client.data.model;

import java.io.Serializable;

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
