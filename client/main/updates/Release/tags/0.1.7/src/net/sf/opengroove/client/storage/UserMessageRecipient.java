package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface UserMessageRecipient
{
    @Property
    public String getUserid();
    
    public void setUserid(String userid);
}
