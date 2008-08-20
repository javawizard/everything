package net.sf.opengroove.client;

import java.io.Serializable;

public class ContactComputer implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5092427115370001261L;
    
    private String name;
    
    private long lastOnline;
    
    private String type;

    public String getName()
    {
        return name;
    }

    public long getLastOnline()
    {
        return lastOnline;
    }

    public String getType()
    {
        return type;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setLastOnline(long lastOnline)
    {
        this.lastOnline = lastOnline;
    }

    public void setType(String type)
    {
        this.type = type;
    }
    
}
