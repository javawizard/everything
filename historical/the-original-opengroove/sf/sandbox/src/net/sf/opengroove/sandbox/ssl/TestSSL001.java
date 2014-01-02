package net.sf.opengroove.sandbox.ssl;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public class TestSSL001
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        SSLContext context = SSLContext.getInstance("TLS");
        SSLServerSocketFactory ssf = context
            .getServerSocketFactory();
        ServerSocket ss = ssf.createServerSocket(54545);
        ss.accept();
    }
    
}
