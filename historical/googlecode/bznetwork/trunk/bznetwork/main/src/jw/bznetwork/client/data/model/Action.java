package jw.bznetwork.client.data.model;

import java.io.Serializable;
import java.util.Date;

public class Action implements Serializable
{
    private String provider;
    private String username;
    private Date when;
    private String event;
    private String details;
    private int target;
    
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
    
    public Date getWhen()
    {
        return when;
    }
    
    public void setWhen(Date when)
    {
        this.when = when;
    }
    
    public String getEvent()
    {
        return event;
    }
    
    public void setEvent(String event)
    {
        this.event = event;
    }
    
    public String getDetails()
    {
        return details;
    }
    
    public void setDetails(String details)
    {
        this.details = details;
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
