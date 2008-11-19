package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Length;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface MessageProperty
{
    @Property
    @Length(1024)
    public String getName();
    
    public void setName(String name);
    
    @Property
    @Length(8192)
    public String getValue();
    
    public void setValue(String value);
}
