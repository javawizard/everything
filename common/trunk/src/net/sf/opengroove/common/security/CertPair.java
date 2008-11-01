package net.sf.opengroove.common.security;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class CertPair
{
    private X509Certificate[] chain;
    private PrivateKey key;
    
    X509Certificate[] getChain()
    {
        return chain;
    }
    
    /**
     * Returns the first element in the chain (IE the element at index 0), or
     * null if the chain is null or has a size of 0
     * 
     * @return
     */
    public X509Certificate getCert()
    {
        if (chain == null)
            return null;
        if (chain.length < 1)
            return null;
        return chain[0];
    }
    
    PrivateKey getKey()
    {
        return key;
    }
    
    void setChain(X509Certificate[] chain)
    {
        this.chain = chain;
    }
    
    void setKey(PrivateKey key)
    {
        this.key = key;
    }
}
