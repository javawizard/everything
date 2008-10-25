package net.sf.opengroove.sandbox.ssl;

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
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
    public static final char[] pass = "pass".toCharArray();
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("example.jks"),
            pass);
        Certificate[] oChain = keystore
            .getCertificateChain("key");
        final PrivateKey key = (PrivateKey) keystore
            .getKey("key", pass);
        final X509Certificate[] chain = new X509Certificate[oChain.length];
        System.arraycopy(oChain, 0, chain, 0, chain.length);
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
                return chain;
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
                return key;
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
