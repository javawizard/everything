package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface TrustedCertificate
{
    @Property
    public String getEncoded();
    
    public void setEncoded(String value);
}
