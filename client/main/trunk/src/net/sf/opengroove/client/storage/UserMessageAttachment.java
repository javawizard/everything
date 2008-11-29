package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
@ProxyBean
public interface UserMessageAttachment
{
    @Property
    public String getName();
    
    public void setName(String name);
    
    @Property
    public String getType();
    
    public void setType(String type);
    
    @Property
    public boolean isSaved();
    
    public void setSaved(boolean saved);
    
    @Property
    public int getSize();
    
    public void setSize(int size);
    
}
