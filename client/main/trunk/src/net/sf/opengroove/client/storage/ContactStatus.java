package net.sf.opengroove.client.storage;

import java.io.Serializable;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface ContactStatus
{
    @Property
    public boolean isOnline();
    
    @Property
    public boolean isActive();
    
    @Property
    public boolean isNonexistant();
    
    /**
     * Returns the time, measured in the server's time, that the contact last
     * moved their mouse or was otherwise active at their computer.
     * 
     * @return
     */
    @Property
    public long getIdleTime();
    
    public void setOnline(boolean isOnline);
    
    public void setActive(boolean isActive);
    
    public void setNonexistant(boolean isNonexistant);
    
    public void setIdleTime(long idleTime);
    
    @Property
    public boolean isKnown();
    
    public void setKnown(boolean isKnown);
}
