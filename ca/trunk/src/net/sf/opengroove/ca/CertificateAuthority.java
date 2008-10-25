package net.sf.opengroove.ca;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.x509.X509V3CertificateGenerator;

/**
 * A class for signing certificates with the OpenGroove CA Certificate. For
 * security reasons, the keystore that contains the CA certificate is not
 * included in this project.
 * 
 * @author Alexander Boyd
 * 
 */
public class CertificateAuthority
{
    
    private static final char[] pass = "pass".toCharArray();
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        if (args.length < 3)
        {
            System.out
                .println("Usage: java ... CertificateAuthority cafile srcfile dstfile");
            System.out.println();
            System.out
                .println("cafile:    The java keystore file that contains the "
                    + "OpenGroove CA Certificate and Private Key");
            System.out
                .println("srcfile:   A java keystore file that should contain "
                    + "only one entry, with an alias of \"key\". The certificate "
                    + "represented by that alias is the one that will be signed.");
            System.out
                .println("dstfile:   A file that the signed certificate will be "
                    + "written to. If this already exists, it will be "
                    + "overwritten. When this program finishes, this file "
                    + "will contain one alias, \"key\", which is a new key and "
                    + "a certificate for that key, which represent the new "
                    + "certificate that is signed by the OpenGroove CA.");
            return;
        }
        String caFile = args[0];
        String srcFile = args[1];
        String dstFile = args[2];
        KeyStore caStore = KeyStore.getInstance("JKS");
        KeyStore srcStore = KeyStore.getInstance("JKS");
        KeyStore dstStore = KeyStore.getInstance("JKS");
        caStore.load(new FileInputStream(caFile), pass);
        srcStore.load(new FileInputStream(srcFile), pass);
        dstStore.load(null, pass);
        PrivateKey signkey = (PrivateKey) caStore.getKey(
            "key", pass);
        X509Certificate signcert = (X509Certificate) caStore
            .getCertificate("key");
        X509Certificate srccert = (X509Certificate) srcStore
            .getCertificate("key");
        String shouldSign = System.console().readLine(
            "Distinguished name : "
                + srccert.getSubjectDN().getName()
                + ", sign? y or n");
        if (!shouldSign.toLowerCase().startsWith("y"))
        {
            System.out
                .println("requested not to sign, exiting");
            return;
        }
        System.out.println("signing...");
        X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
        gen.setIssuerDN(signcert.getSubjectX500Principal());
        gen.setNotBefore(new Date());
        gen.setNotAfter(new Date(System.currentTimeMillis()
            + TimeUnit.MILLISECONDS.convert(180,
                TimeUnit.DAYS)));
        gen.setSerialNumber(new BigInteger(""
            + System.currentTimeMillis()));
        gen.setSignatureAlgorithm("SHA512withRSA");
        gen.setSubjectDN(srccert.getSubjectX500Principal());
        KeyPairGenerator keygen = KeyPairGenerator
            .getInstance("RSA");
        keygen.initialize(3072);
        System.out.println("generating keys...");
        KeyPair keys = keygen.generateKeyPair();
        System.out.println("keys generated.");
        RSAPublicKey pub = (RSAPublicKey) keys.getPublic();
        RSAPrivateKey prv = (RSAPrivateKey) keys
            .getPrivate();
        gen.setPublicKey(pub);
        X509Certificate cert = gen.generate(signkey);
        dstStore.setKeyEntry("key", prv, pass,
            new Certificate[] { cert, signcert });
        dstStore.store(new FileOutputStream(dstFile), pass);
        System.out
            .println("signing completed successfully.");
    }
}
