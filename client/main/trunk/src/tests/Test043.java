package tests;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

import DE.knp.MicroCrypt.Aes256;

import net.sf.opengroove.common.security.Crypto;

public class Test043
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        for (int i = 0; i < 50000; i++)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BigInteger aesRandomNumber = new BigInteger(
                3060, new SecureRandom());
            byte[] securityKeyBytes = new byte[32];
            System.arraycopy(aesRandomNumber.toByteArray(),
                0, securityKeyBytes, 0, 32);
            final Aes256 securityKey = new Aes256(
                securityKeyBytes);
            BigInteger testRandomNumber = new BigInteger(
                3060, new SecureRandom());
            String testString = testRandomNumber
                .toString(36);
            testString = testString.substring(0,
                (int) ((Math.random() * 200) + 3));
            Crypto.enc(securityKey, testString.getBytes(),
                baos);
            ByteArrayInputStream is = new ByteArrayInputStream(
                baos.toByteArray());
            String dec = new String(Crypto.dec(securityKey,
                is, 65535));
            if (!dec.equals(testString))
                throw new RuntimeException(
                    "Invalid decryption, value is " + dec
                        + " and enc value is " + testString);
        }
    }
}
