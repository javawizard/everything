package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Length;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface TrustedCertificate
{
    @Property
    @Length(8192)
    public String getEncoded();
    
    public void setEncoded(String value);
}
