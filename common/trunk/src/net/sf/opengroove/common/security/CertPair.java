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
