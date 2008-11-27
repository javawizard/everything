package net.sf.opengroove.common.security;

/*************************************************************************
 *  DOWNLOADED FROM: http://www.cs.princeton.edu/introcs/78crypto/RSA.java
 *  and slightly modified to fix the 2 bugs listed below and make more usable in OpenGroove<br/><br/>
 *  
 *  If anyone knows who to credit for this file, send me an email (javawizard@opengroove.org)<br/><br/>
 *  
 *  The comments below this line were in the original file.<br/><br/>
 *  
 *  
 * 
 *  Compilation:  javac RSA.java
 *  Execution:    java RSA N
 *  
 *  Generate an N-bit public and private RSA key and use to encrypt
 *  and decrypt a random message.
 * 
 *  % java RSA 50
 *  public  = 65537
 *  private = 553699199426609
 *  modulus = 825641896390631
 *  message   = 48194775244950
 *  encrpyted = 321340212160104
 *  decrypted = 48194775244950
 *
 *  Known bugs (not addressed for simplicity)
 *  -----------------------------------------
 *  - It could be the case that the message >= modulus. To avoid, use
 *    a do-while loop to generate key until modulus happen to be exactly N bits.
 *
 *  - It's possible that gcd(phi, publicKey) != 1 in which case
 *    the key generation fails. This will only happen if phi is a
 *    multiple of 65537. To avoid, use a do-while loop to generate
 *    keys until the gcd is 1.
 *
 *************************************************************************/

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA
{
    private final static BigInteger one = new BigInteger(
        "1");
    private final static SecureRandom random = new SecureRandom();
    
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;
    
    public BigInteger getPrivateKey()
    {
        return privateKey;
    }
    
    public BigInteger getPublicKey()
    {
        return publicKey;
    }
    
    public BigInteger getModulus()
    {
        return modulus;
    }
    
    // generate an N-bit (roughly) public and private key
    public RSA(int N)
    {
        BigInteger phi;
        do
        {
            System.out.println("generating p");
            BigInteger p = BigInteger.probablePrime(N / 2,
                random);
            System.out.println("generating q");
            BigInteger q = BigInteger.probablePrime(N / 2,
                random);
            System.out.println("generating phi");
            phi = (p.subtract(one)).multiply(q
                .subtract(one));
            System.out.println("generating n");
            modulus = p.multiply(q);
            System.out.println("generating e");
            publicKey = new BigInteger("65537"); // common value in practice
            // =
            // 2^16 + 1
            System.out.println("generating d");
            privateKey = publicKey.modInverse(phi);
        }
        while ((!phi.gcd(publicKey).equals(one))
            || (modulus.bitLength() < (N - 1)));
    }
    
    public static BigInteger encrypt(BigInteger publicKey,
        BigInteger modulus, BigInteger message)
    {
        return message.modPow(publicKey, modulus);
    }
    
    public static BigInteger decrypt(BigInteger privateKey,
        BigInteger modulus, BigInteger encrypted)
    {
        return encrypted.modPow(privateKey, modulus);
    }
    
    public BigInteger encrypt(BigInteger message)
    {
        return encrypt(publicKey, modulus, message);
    }
    
    public BigInteger decrypt(BigInteger message)
    {
        return decrypt(privateKey, modulus, message);
    }
    
    public String toString()
    {
        String s = "";
        s += "public  = " + publicKey + "\n";
        s += "private = " + privateKey + "\n";
        s += "modulus = " + modulus;
        return s;
    }
    
    public static void main(String[] args)
    {
        long[] ctimes = new long[10];
        long[] etimes = new long[ctimes.length];
        long[] dtimes = new long[ctimes.length];
        for (int i = 0; i < ctimes.length; i++)
        {
            System.out.println("beginning " + (i + 1)
                + " of " + ctimes.length);
            long current = System.currentTimeMillis();
            int N = 3072;
            System.out.println("generating key");
            RSA key = new RSA(N);
            System.out.println("***mod "
                + key.modulus.bitLength());
            System.out.println(key);
            ctimes[i] = System.currentTimeMillis()
                - current;
            BigInteger message = new BigInteger(N - 15,
                random);
            current = System.currentTimeMillis();
            // create random message, encrypt and decrypt
            System.out.println("creating random message");
            // // create message by converting string to integer
            // String s = "test";
            // byte[] bytes = s.getBytes();
            // BigInteger message = new BigInteger(s);
            System.out.println("encrypting");
            BigInteger encrypt = key.encrypt(message);
            etimes[i] = System.currentTimeMillis()
                - current;
            current = System.currentTimeMillis();
            System.out.println("decrypting");
            BigInteger decrypt = key.decrypt(encrypt);
            System.out.println("message   = " + message);
            System.out.println("encrpyted = " + encrypt);
            System.out.println("decrypted = " + decrypt);
            dtimes[i] = System.currentTimeMillis()
                - current;
        }
        System.out
            .println("Here are the times (in seconds) that it took:");
        System.out.println("creation:");
        for (long l : ctimes)
        {
            System.out.println("" + l);
        }
        System.out.println("encryption:");
        for (long l : etimes)
        {
            System.out.println("" + l);
        }
        System.out.println("decryption");
        for (long l : dtimes)
        {
            System.out.println("" + l);
        }
    }
    
    /**
     * Verifies that the keys given form a keypair. This method works by
     * generating a random number of at most N - 3 bits, where N is
     * <code>mod.bitLength()</code>. It then encrypts it using the public key
     * and modulus, and decrypts it using the private key and modulus. If this
     * decrypted value matches the original random number, true is returned. If
     * not, false is returned.
     * 
     * @param pub
     *            The public exponent to test
     * @param mod
     *            The modulus to test
     * @param prv
     *            The private key exponent to test
     * @return True if the public, modulus, and private keys passed in form a
     *         valid keypair, false if they don't
     */
    public static boolean verifySet(BigInteger pub,
        BigInteger mod, BigInteger prv)
    {
        BigInteger randomNumber = new BigInteger(mod
            .bitLength() - 3, random);
        BigInteger encrypted = encrypt(pub, mod,
            randomNumber);
        BigInteger decrypted = decrypt(prv, mod, encrypted);
        return decrypted.equals(randomNumber);
    }
    
    public static byte[] encrypt(BigInteger publicKey,
        BigInteger modulus, byte[] message)
    {
        return CertificateUtils.encryptRsa(message,
            publicKey, modulus);
    }
    
    public static byte[] decrypt(BigInteger privateKey,
        BigInteger modulus, byte[] message)
    {
        return CertificateUtils.decryptRsa(message,
            privateKey, modulus);
    }
}
