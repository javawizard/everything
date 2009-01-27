package net.sf.opengroove.sandbox.misc;

import java.math.BigInteger;
import java.security.Security;
import java.util.Arrays;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.security.RSA;
import net.sf.opengroove.common.utils.StringUtils;

public class RSAShrinkTester
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Security.addProvider(new BouncyCastleProvider());
        System.out.println("generating keypair");
        RSA rsa = new RSA(1536);
        System.out.println("keypair generated");
        int wereEqual = 0;
        int wereSameSize = 0;
        for (int i = 0; i < 100; i++)
        {
            byte[] key = CertificateUtils
                .generateSymmetricKey().getEncoded();
            byte[] enc = CertificateUtils.encryptRsa(key,
                rsa.getPublicKey(), rsa.getModulus());
            byte[] keyDecBytes = CertificateUtils
                .decryptRsa(enc, rsa.getPrivateKey(), rsa
                    .getModulus());
            if (!Arrays.equals(key, keyDecBytes))
                System.out.println(keyDecBytes.length);
        }
        System.out.println("" + wereEqual + " were equal.");
        System.out.println("" + wereSameSize
            + " were the same size.");
    }
}
