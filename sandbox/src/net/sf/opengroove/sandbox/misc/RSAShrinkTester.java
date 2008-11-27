package net.sf.opengroove.sandbox.misc;

import java.math.BigInteger;
import java.util.Arrays;

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
        System.out.println("generating keypair");
        RSA rsa = new RSA(1024);
        System.out.println("keypair generated");
        int wereEqual = 0;
        int wereSameSize = 0;
        for (int i = 0; i < 100; i++)
        {
            byte[] key = CertificateUtils
                .generateSymmetricKey().getEncoded();
            BigInteger keyInt = new BigInteger(1, key);
            BigInteger keyEnc = rsa.encrypt(keyInt);
            BigInteger keyDec = rsa.decrypt(keyEnc);
            byte[] keyDecBytes = StringUtils.exactLength(
                keyDec.toByteArray(), 32);
            if (!Arrays.equals(key, keyDecBytes))
                System.out.println(keyDecBytes.length);
        }
        System.out.println("" + wereEqual + " were equal.");
        System.out.println("" + wereSameSize
            + " were the same size.");
    }
    
}
