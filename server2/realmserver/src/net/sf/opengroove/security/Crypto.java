package net.sf.opengroove.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import DE.knp.MicroCrypt.Aes256;

/**
 * this class provides methods for encoding a byte array with a size of up to
 * 65535 bytes as an AES-encoded byte array. The methods on this class take care
 * of adding and removing padding as necessary.
 * 
 * @author Alexander Boyd
 * 
 */
public class Crypto
{
    private static SecureRandom random = new SecureRandom();
    
    public byte[] enc(byte[] key, byte[] message)
    {
        Aes256 c = new Aes256(key);
        int tl = message.length + 4;
        while ((tl % 16) != 0)
            tl++;
        byte[] toEnc = new byte[tl];
        System.arraycopy(intToBytes(message.length), 0,
            toEnc, 0, 4);
        System.arraycopy(message, 0, toEnc, 4,
            message.length);
        byte[] randomBytes = new byte[tl
            - (message.length + 4)];
        random.nextBytes(randomBytes);
        System.arraycopy(randomBytes, 0, toEnc,
            message.length + 4, randomBytes.length);
        // toEnc is ready for encryption, so allocate another byte array for
        // output and encrypt the data
        byte[] enc = new byte[tl];
        for (int i = 0; i < tl; i += 16)
        {
            c.encrypt(toEnc, i, enc, i);
        }
        return enc;
    }
    
    /**
     * Decodes a message from the stream specified. Multiple messages can be on
     * this stream, and can be sequentially decrypted by calling this method on
     * the same stream a bunch of times.
     * 
     * @param key
     *            the key to use for decryption
     * @param message
     *            an InputStream containing the message
     * @return the decrypted message
     */
    public byte[] dec(byte[] key, InputStream message)
    {
        
    }
    
    public byte[] dec(byte[] key, byte[] message)
    {
        
    }
    
    public byte[] intToBytes(int i)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
            4);
        DataOutputStream dos = new DataOutputStream(baos);
        try
        {
            dos.writeInt(i);
        }
        catch (IOException e)// won't ever happen
        {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    
    public int bytesToInt(byte[] bytes)
    {
        if (bytes.length < 4)
            throw new RuntimeException(
                "need at least 4 bytes to convert to an int");
        ByteArrayInputStream bais = new ByteArrayInputStream(
            bytes);
        DataInputStream dis = new DataInputStream(bais);
        try
        {
            return dis.readInt();
        }
        catch (IOException e)// won't ever happen
        {
            throw new RuntimeException(e);
        }
    }
}
