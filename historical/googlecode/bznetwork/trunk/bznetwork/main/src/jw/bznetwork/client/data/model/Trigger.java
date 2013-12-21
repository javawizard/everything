package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Trigger implements Serializable
{
    @Override
    public int hashCode()
    {
        return triggerid;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Trigger))
            return false;
        Trigger other = (Trigger) obj;
        if (triggerid != other.triggerid)
            return false;
        return true;
    }
    
    private int triggerid;
    /*
     * TODO: perhaps have some way to differentiate types of events. Maybe
     * prefix with "server:", "action:", and "internal:" or something. This
     * causes complications, though, when selecting a target. Maybe just have
     * the same levels thing as permissions do, where a particular event has a
     * particular level that it should apply to.
     */
    private String event;
    private int target;
    private String sendtype;
    private int recipient;
    private String subject;
    private String message;
    
    public int getTriggerid()
    {
        return triggerid;
    }
    
    public void setTriggerid(int triggerid)
    {
        this.triggerid = triggerid;
    }
    
    public String getEvent()
    {
        return event;
    }
    
    public void setEvent(String event)
    {
        this.event = event;
    }
    
    public int getTarget()
    {
        return target;
    }
    
    public void setTarget(int target)
    {
        this.target = target;
    }
    
    public String getSendtype()
    {
        return sendtype;
    }
    
    public void setSendtype(String sendtype)
    {
        this.sendtype = sendtype;
    }
    
    public int getRecipient()
    {
        return recipient;
    }
    
    public void setRecipient(int recipient)
    {
        this.recipient = recipient;
    }
    
    public String getSubject()
    {
        return subject;
    }
    
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
}
