package net.sf.opengroove.common.security;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.security.Certificate;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

/**
 * This class contains utilities related to X.509 certificates.<br/><br/>
 * 
 * For methods that read and write cryptographic objects (such as a certificate
 * chain or a private key), the output is typically in PEM format. The read
 * methods will throw a ClassCastException if the PEM input is not of the
 * specified type. This would happen if, for example, a certificate PEM string
 * (IE one beginning with "-----BEGIN CERTIFICATE-----") was passed into a
 * method that reads private keys.
 * 
 * @author Alexander Boyd
 * 
 */
public class CertificateUtils
{
    private static final char[] pass = "pass".toCharArray();
    
    /**
     * Reads a pair of a certificate chain and a private key
     * 
     * @param in
     * @return
     */
    public static CertPair readCertPair(String in)
    {
        try
        {
            PEMReader reader = new PEMReader(
                new StringReader(in));
            /*
             * The first entry is the private key, which is returned from the
             * reader as a KeyPair.
             */
            KeyPair pair = (KeyPair) reader.readObject();
            CertPair certpair = new CertPair();
            certpair.setKey(pair.getPrivate());
            /*
             * The rest of the entries should be certificates to add to the
             * chain. We'll read certificates until we get a null object back,
             * and add all the certificates that we read to the certpair.
             */
            ArrayList<X509Certificate> certs = new ArrayList<X509Certificate>();
            X509Certificate cert;
            while ((cert = (X509Certificate) reader
                .readObject()) != null)
            {
                certs.add(cert);
            }
            certpair.setChain(certs
                .toArray(new X509Certificate[0]));
            return certpair;
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(
                "Cryptographic exception", e);
        }
    }
    
    public static String writeCertPair(CertPair pair)
    {
        try
        {
            /*
             * First we write the private key. Then we write the certificates,
             * with the one at index 0 first.
             */
            StringWriter sw = new StringWriter();
            PEMWriter writer = new PEMWriter(sw);
            writer.writeObject(pair.getKey());
            for (X509Certificate cert : pair.getChain())
            {
                writer.writeObject(cert);
            }
            writer.flush();
            return sw.toString();
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(
                "Cryptographic exception", e);
        }
    }
    
    public X509Certificate readCert(String in)
    {
        try
        {
            PEMReader reader = new PEMReader(
                new StringReader(in));
            return (X509Certificate) reader.readObject();
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(
                "Cryptographic exception", e);
        }
        
    }
    
    public String writeCert(X509Certificate cert)
    {
        
    }
    
    public static X509Certificate[] readCertChain(String in)
    {
        
    }
    
    public static String writeCertChain(
        X509Certificate[] chain)
    {
        
    }
    
    public static Key readKey(String in)
    {
        
    }
    
    public static String writeKey(Key key)
    {
        
    }
}
