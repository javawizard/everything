package net.sf.opengroove.common.security;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.Certificate;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

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
        try
        {
            StringWriter sw = new StringWriter();
            PEMWriter writer = new PEMWriter(sw);
            writer.writeObject(cert);
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
    
    public static X509Certificate[] readCertChain(String in)
    {
        try
        {
            PEMReader reader = new PEMReader(
                new StringReader(in));
            ArrayList<X509Certificate> certs = new ArrayList<X509Certificate>();
            X509Certificate cert;
            while ((cert = (X509Certificate) reader
                .readObject()) != null)
            {
                certs.add(cert);
            }
            return certs.toArray(new X509Certificate[0]);
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(
                "Cryptographic exception", e);
        }
    }
    
    public static String writeCertChain(
        X509Certificate[] chain)
    {
        try
        {
            StringWriter sw = new StringWriter();
            PEMWriter writer = new PEMWriter(sw);
            for (X509Certificate cert : chain)
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
    
    public static PrivateKey readPrivateKey(String in)
    {
        try
        {
            PEMReader reader = new PEMReader(
                new StringReader(in));
            return ((KeyPair) reader.readObject())
                .getPrivate();
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(
                "Cryptographic exception", e);
        }
    }
    
    public static String writePrivateKey(PrivateKey key)
    {
        try
        {
            StringWriter sw = new StringWriter();
            PEMWriter writer = new PEMWriter(sw);
            writer.writeObject(key);
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
    
    /**
     * Same as
     * {@link #createCert(X500Principal, X500Principal, Date, Date, String, PublicKey, PrivateKey)},
     * but generates a certificate that is valid starting now and for the number
     * of days specified.
     * 
     * @param subject
     * @param issuer
     * @param days
     *            The number of days that this certificate is valid for
     * @param sigalg
     * @param subjectKey
     * @param issuerKey
     * @return
     */
    public static X509Certificate createCert(
        X500Principal subject, X500Principal issuer,
        int days, String sigalg, PublicKey subjectKey,
        PrivateKey issuerKey)
    {
        return createCert(subject, issuer, new Date(),
            new Date(System.currentTimeMillis()
                + TimeUnit.MILLISECONDS.convert(days,
                    TimeUnit.DAYS)), sigalg, subjectKey,
            issuerKey);
    }
    
    /**
     * Generates a new X.509 certificate, signed by the authority specified.
     * 
     * @param subject
     *            The subject that this certificate is being issued to
     * @param issuer
     *            The issuer of this certificate
     * @param start
     *            The start date at which the certificate is valid
     * @param end
     *            The end date after which the certificate is not valid
     * @param sigalg
     *            The signature algorithm, which should correspond with the
     *            issuer's key. If this is null, "SHA512withRSA" is used, which
     *            requires issuerKey to be an RSAPrivateKey.
     * @param subjectKey
     *            The public key of this certificate's subject
     * @param issuerKey
     *            The private key of this certificate's issuer
     * @return A new certificate for the subject, signed by the issuer
     */
    public static X509Certificate createCert(
        X500Principal subject, X500Principal issuer,
        Date start, Date end, String sigalg,
        PublicKey subjectKey, PrivateKey issuerKey)
    {
        try
        {
            X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
            gen.setIssuerDN(issuer);
            gen.setNotBefore(start);
            gen.setNotAfter(end);
            gen.setSerialNumber(new BigInteger(""
                + System.currentTimeMillis()));
            /*
             * Recommended is SHA512withRSA
             */
            if (sigalg == null)
                sigalg = "SHA512withRSA";
            gen.setSignatureAlgorithm(sigalg);
            gen.setSubjectDN(subject);
            gen.setPublicKey(subjectKey);
            X509Certificate cert = gen.generate(issuerKey);
            return cert;
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(
                "Cryptographic exception", e);
        }
    }
}
