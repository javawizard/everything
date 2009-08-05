package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Banfile implements Serializable
{
    private int banfileid;
    private String name;
    
    public int getBanfileid()
    {
        return banfileid;
    }
    
    public void setBanfileid(int banfileid)
    {
        this.banfileid = banfileid;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Banfile()
    {
    }
    
}
