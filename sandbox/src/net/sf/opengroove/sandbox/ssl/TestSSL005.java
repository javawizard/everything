package net.sf.opengroove.sandbox.ssl;

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TestSSL005
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
        X509Certificate certificate = (X509Certificate) keystore
            .getCertificate("key");
        X509TrustManager manager = new X509TrustManager()
        {
            
            @Override
            public void checkClientTrusted(
                X509Certificate[] chain, String authType)
                throws CertificateException
            {
                System.out.println("checkClientTrusted");
            }
            
            @Override
            public void checkServerTrusted(
                X509Certificate[] chain, String authType)
                throws CertificateException
            {
                System.out.println("checkServerTrusted");
            }
            
            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(new KeyManager[] {},
            new TrustManager[] { manager },
            new SecureRandom());
        SSLSocketFactory factory = context
            .getSocketFactory();
        Socket s = factory.createSocket("localhost", 24680);
        System.out.println("writing");
        s.getOutputStream().write(65);
    }
    
}
