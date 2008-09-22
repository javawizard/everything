package net.sf.opengroove.client.storage;

import java.io.Serializable;

public class ContactStatus implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1097562916263112136L;
    private boolean isOnline;
    private boolean isActive;
    private boolean isNonexistant;
    private boolean isKnown;
    private long idleTime;
    
    public boolean isOnline()
    {
        return isOnline;
    }
    
    public boolean isActive()
    {
        return isActive;
    }
    
    public boolean isNonexistant()
    {
        return isNonexistant;
    }
    
    /**
     * Returns the time, measured in the server's time, that the contact last
     * moved their mouse or was otherwise active at their computer.
     * 
     * @return
     */
    public long getIdleTime()
    {
        return idleTime;
    }
    
    public void setOnline(boolean isOnline)
    {
        System.out.println("setting contact online to "
            + isOnline);
        this.isOnline = isOnline;
    }
    
    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }
    
    public void setNonexistant(boolean isNonexistant)
    {
        this.isNonexistant = isNonexistant;
    }
    
    public void setIdleTime(long idleTime)
    {
        this.idleTime = idleTime;
    }
    
    public boolean isKnown()
    {
        return isKnown;
    }
    
    public void setKnown(boolean isKnown)
    {
        this.isKnown = isKnown;
    }
    
}
