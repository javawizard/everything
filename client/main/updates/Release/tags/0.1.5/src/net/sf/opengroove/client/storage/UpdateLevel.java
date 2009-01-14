package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface UpdateLevel
{
    @Property
    public String getName();
    public void setName(String name);
    
    @Property
    public String getDescription();
    public void setDescription(String description);
}
