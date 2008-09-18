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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BigInteger aesRandomNumber = new BigInteger(3060,
            new SecureRandom());
        byte[] securityKeyBytes = new byte[32];
        System.arraycopy(aesRandomNumber.toByteArray(), 0,
            securityKeyBytes, 0, 32);
        final Aes256 securityKey = new Aes256(
            securityKeyBytes);
        Crypto.enc(securityKey, "Hello world!".getBytes(),
            baos);
        ByteArrayInputStream is = new ByteArrayInputStream(
            baos.toByteArray());
        System.out.println(new String(Crypto.dec(
            securityKey, is, 65535)));
    }
}
