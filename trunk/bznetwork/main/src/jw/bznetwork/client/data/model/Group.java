package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Group implements Serializable
{
    private int groupid;
    private String name;
    public int getGroupid()
    {
        return groupid;
    }
    public void setGroupid(int groupid)
    {
        this.groupid = groupid;
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
