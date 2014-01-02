package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface OutboundMessageRecipient
{
    @Property
    public String getUserid();
    
    public void setUserid(String userid);
    
    @Property
    public String getComputer();
    
    public void setComputer(String computer);
}
