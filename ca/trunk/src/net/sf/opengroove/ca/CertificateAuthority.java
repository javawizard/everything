package net.sf.opengroove.ca;

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
    
    /**
     * @param args
     */
    public static void main(String[] args)
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
                    + "will contain one alias, \"key\", which is a certificate "
                    + "chain containing the user's certificate signed by "
                    + "the OpenGroove CA Certificate, and the OpenGroove CA "
                    + "Certificate itself.");
            return;
        }
    }
    
}
