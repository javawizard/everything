package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Group implements Serializable
{
    private int groupid;
    private String name;
    private int banfile;
    public int getBanfile()
    {
        return banfile;
    }
    public void setBanfile(int banfile)
    {
        this.banfile = banfile;
    }
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
