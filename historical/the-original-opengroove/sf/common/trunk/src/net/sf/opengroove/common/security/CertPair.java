package net.sf.opengroove.common.security;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class CertPair
{
    private X509Certificate[] chain;
    private PrivateKey key;
    
    public X509Certificate[] getChain()
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
    
    public PrivateKey getKey()
    {
        return key;
    }
    
    public CertPair()
    {
        super();
    }
    
    public CertPair(X509Certificate cert, PrivateKey key)
    {
        this(new X509Certificate[] { cert }, key);
    }
    
    public CertPair(X509Certificate[] chain, PrivateKey key)
    {
        super();
        this.chain = chain;
        this.key = key;
    }
    
    public void setChain(X509Certificate[] chain)
    {
        this.chain = chain;
    }
    
    public void setKey(PrivateKey key)
    {
        this.key = key;
    }
}
