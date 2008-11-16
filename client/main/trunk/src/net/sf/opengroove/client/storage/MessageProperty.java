package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface MessageProperty
{
    @Property
    public String getName();
    
    public void setName(String name);
    
    @Property
    public String getValue();
    
    public void setValue(String value);
}
