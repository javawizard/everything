package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Role implements Serializable
{
    public Role()
    {
        super();
    }
    
    private int roleid;
    private String name;
    
    public int getRoleid()
    {
        return roleid;
    }
    
    public void setRoleid(int roleid)
    {
        this.roleid = roleid;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
