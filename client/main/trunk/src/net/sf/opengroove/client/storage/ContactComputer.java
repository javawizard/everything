package net.sf.opengroove.client.storage;

import java.io.Serializable;

import net.sf.opengroove.common.proxystorage.*;

/**
 * This class stores information about a contact's computer.
 * 
 * @author Alexander Boyd
 * 
 */
@ProxyBean
public interface ContactComputer
{
    @Property
    public long getLag();
    
    public void setLag(long lag);
    
    @Property
    public String getName();
    
    @Property
    @Required
    public ContactStatus getStatus();
    
    @Property
    public String getType();
    
    public void setName(String name);
    
    public void setType(String type);
}
