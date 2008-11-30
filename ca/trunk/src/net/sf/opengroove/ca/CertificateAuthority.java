package net.sf.opengroove.ca;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import net.sf.opengroove.common.security.CertPair;
import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.utils.StringUtils;

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
                .println("cafile:    The file that contains the OpenGroove CA "
                    + "Certificate and private key");
            System.out
                .println("srcfile:   The file that contains the certificate to "
                    + "be signed");
            System.out
                .println("dstfile:   A file that the signed certificate will be "
                    + "written to. If this already exists, it will be "
                    + "overwritten. When this program finishes, this file "
                    + "will contain the new certificate chain to use "
                    + "(which is the OpenGroove CA Certificate, and "
                    + "the newly-signed source certificate).");
            return;
        }
        String caFile = args[0];
        String srcFile = args[1];
        String dstFile = args[2];
        CertPair signpair = CertificateUtils
            .readCertPair(StringUtils.readFile(new File(
                caFile)));
        PrivateKey signkey = signpair.getKey();
        X509Certificate signcert = signpair.getCert();
        X509Certificate srccert = CertificateUtils
            .readCert(StringUtils
                .readFile(new File(srcFile)));
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
        X509Certificate cert = CertificateUtils.createCert(
            srccert.getSubjectX500Principal(), signcert
                .getSubjectX500Principal(), 180,
            "SHA512withRSA", srccert.getPublicKey(),
            signkey);
        StringUtils.writeFile(CertificateUtils
            .writeCertChain(new X509Certificate[] { cert,
                signcert }), new File(dstFile));
    }
}
