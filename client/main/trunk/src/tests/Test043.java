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
        BigInteger aesRandomNumber = new BigInteger(3060,
            new SecureRandom());
        byte[] securityKeyBytes = new byte[32];
        System.arraycopy(aesRandomNumber.toByteArray(), 0,
            securityKeyBytes, 0, 32);
        final Aes256 key = new Aes256(securityKeyBytes);
        final byte[] toEnc = ("Hello, world! How are you today? This "
            + "is some very long text. Good-bye!")
            .getBytes();
        File testfile = new File("C:\\testfile.ogvts");
        if (testfile.exists())
            testfile.delete();
        final FileOutputStream fos = new FileOutputStream(
            testfile);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread()
            {
                public void run()
                {
                    for (int i = 0; i < 10000; i++)
                    {
                        try
                        {
                            Crypto.enc(key, toEnc, fos);
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
        }
        for (Thread thread : threads)
        {
            thread.start();
        }
        System.out
            .println("waiting for writes to complete...");
        for (Thread thread : threads)
        {
            thread.join();
        }
        System.out
            .println("done, about to decrypt and validate...");
    }
}
