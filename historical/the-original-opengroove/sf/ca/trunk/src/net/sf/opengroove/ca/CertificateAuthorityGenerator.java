package net.sf.opengroove.ca;

import java.io.File;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import net.sf.opengroove.common.security.CertPair;
import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.utils.StringUtils;

/**
 * Generates a self-signed certificate (using RSA-3072 as the key algorithm and
 * SHA1withRSA as the signature algorithm) for use as the OpenGroove CA
 * Certificate, along with it's private key, and places a file holding the
 * cert/key combo in C:/ogcerts/private.pem and a file holding only the cert in
 * C:/ogcerts/cert.pem
 * 
 * @author Alexander Boyd
 * 
 */
public class CertificateAuthorityGenerator
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("generating keypair");
        KeyPair keypair = CertificateUtils.createKeyPair(
            "RSA", 3072);
        System.out.println("generating certificate");
        X509Certificate cert = CertificateUtils.createCert(
            new X500Principal(
                "CN=OpenGroove Certificate Authority"),
            new X500Principal(
                "CN=OpenGroove Certificate Authority"),
            360 * 20, null, keypair.getPublic(), keypair
                .getPrivate());
        System.out.println("storing certificate");
        StringUtils.writeFile(CertificateUtils
            .writeCert(cert), new File(
            "C:/ogcerts/cert-temp.pem"));
        StringUtils.writeFile(CertificateUtils
            .writeCertPair(new CertPair(cert, keypair
                .getPrivate())), new File(
            "C:/ogcerts/private-temp.pem"));
        System.out.println("done");
    }
}
