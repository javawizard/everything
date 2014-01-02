package net.sf.opengroove.common.security;

import java.security.cert.X509Certificate;

public interface TrustAlwaysListener
{
    
    public void trustAlways(X509Certificate endCertificate);
    
}
