package net.sf.opengroove.sandbox.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

/**
 * A class for creating a server socket that serves the certificate chain stored
 * in example.jks
 * 
 * @author Alexander Boyd
 * 
 */
public class TestSSL004
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        X509KeyManager manager = new X509KeyManager()
        {
            
            @Override
            public String chooseClientAlias(
                String[] keyType, Principal[] issuers,
                Socket socket)
            {
                return "key";
            }
            
            @Override
            public String chooseServerAlias(String keyType,
                Principal[] issuers, Socket socket)
            {
                return "key";
            }
            
            @Override
            public X509Certificate[] getCertificateChain(
                String alias)
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String[] getClientAliases(
                String keyType, Principal[] issuers)
            {
                return null;
            }
            
            @Override
            public PrivateKey getPrivateKey(String alias)
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String[] getServerAliases(
                String keyType, Principal[] issuers)
            {
                return null;
            }
        };
    }
    
}
