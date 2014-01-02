package net.sf.opengroove.sandbox.ssl;

import java.io.FileInputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
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
        keystore.load(
            new FileInputStream("selfsigned.jks"), pass);
        final X509Certificate certificate = (X509Certificate) keystore
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
                if (chain[chain.length - 1]
                    .equals(certificate))
                {
                    System.out.println("root-signed");
                    /*
                     * The first certificate is the CA certificate. We only
                     * allow the request through if there are only 2
                     * certificates in the chain, if the second certificate has
                     * a valid signature, and if it's signature matches the
                     * first certificate.
                     */
                    if (chain.length != 2)
                        throw new CertificateException();
                    X509Certificate initial = chain[0];
                    System.out.println("initial DN : "
                        + initial.getSubjectDN().getName());
                    System.out
                        .println("issuer DN : "
                            + chain[1].getSubjectDN()
                                .getName());
                    initial.checkValidity();
                    try
                    {
                        initial.verify(certificate
                            .getPublicKey());
                    }
                    catch (Exception e)
                    {
                        throw new CertificateException(e);
                    }
                    System.out.println("root valid");
                    /*
                     * We've validated everything about the key. Now we
                     * successfully return.
                     */
                    return;
                }
                /*
                 * If we get here, then the certificate isn't signed by the cert
                 * stored in selfsigned.jks, so we prompt the user to accept it.
                 */
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
