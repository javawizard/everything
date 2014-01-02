package net.sf.opengroove.common.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Random;

import DE.knp.MicroCrypt.Aes256;

/**
 * this class provides methods for encoding a byte array with a size of up to
 * 65535 bytes as an AES-encoded byte array. The methods on this class take care
 * of adding and removing padding as necessary.
 * 
 * This class was previously used to handle socket encryption. It is being
 * phased out in favor of SSL, and will be removed in the future.
 * 
 * @deprecated
 * @author Alexander Boyd
 * 
 */
@Deprecated
public class Crypto
{
    private static SecureRandom random = new SecureRandom();
    
    private static final Object decryptionLock = new Object();
    
    /**
     * Encrypts the message specified, using the aes key specified, to the
     * stream specified, with some header information that allows the length of
     * the packet to be determined.
     * 
     * @param key
     * @param message
     * @param stream
     * @throws IOException
     */
    public static synchronized void enc(Aes256 c,
        byte[] message, OutputStream stream)
        throws IOException
    {
        DataOutputStream out = new DataOutputStream(stream);
        out.writeInt(message.length);
        int tl = message.length;
        while ((tl % 16) != 0)
            tl++;
        byte[] toEnc = new byte[tl];
        System.arraycopy(message, 0, toEnc, 0,
            message.length);
        byte[] randomBytes = new byte[tl - message.length];
        /*
         * FIXME: un-comment the following line. I commented it out to see if it
         * helped with the corrupted packet stuff that was happening, but it's
         * not a good idea as it makes the last 16 bytes of a packet subject to
         * a known-plaintext attack.
         */
        // random.nextBytes(randomBytes);
        System.arraycopy(randomBytes, 0, toEnc,
            message.length, randomBytes.length);
        // we have our bytes now, time to encrypt them
        byte[] enc = new byte[toEnc.length];
        for (int i = 0; i < tl; i += 16)
        {
            c.encrypt(toEnc, i, enc, i);
        }
        // everything's been encrypted, now we need to write it to the
        // stream
        out.write(enc);
        out.flush();
        // System.out.println("encrypted with length "
        // + message.length);
    }
    
    /**
     * Decodes a message from the stream specified. Multiple messages can be on
     * this stream, and can be sequentially decrypted by calling this method on
     * the same stream a bunch of times. If the message received is larger than
     * <code>limit</code>, an exception is thrown. This is because, since the
     * message length is not encrypted, an attacker could alter the message
     * length to be some astronomically large value, thereby causing an
     * OutOfMemoryError when a byte[] of that length is constructed.
     * 
     * @param key
     *            the key to use for decryption
     * @param message
     *            an InputStream containing the message
     * @return the decrypted message
     * @throws IOException
     */
    public static byte[] dec(Aes256 c, InputStream message,
        int limit) throws IOException
    {
        synchronized (decryptionLock)
        {
            DataInputStream in = new DataInputStream(
                message);
            int length = in.readInt();
            int tl = length;
            while ((tl % 16) != 0)
                tl++;
            // length is the length of the actual message, tl is the number of
            // bytes
            // to read, tl will always be greater or equal to length, and the
            // remaining bytes are just random padding
            if (length > limit)
                throw new RuntimeException(
                    "The length of the message received ("
                        + length
                        + ") was larger than the limit ("
                        + limit + ")");
            byte[] toDec = new byte[tl];
            int pointer = 0;
            while (pointer < tl)
            {
                int px;
                pointer += (px = in.read(toDec, pointer, tl
                    - pointer));
                if (px == -1)
                    throw new EOFException();
            }
            // toDec is filled with data to be decrypted, now we'll decrypt it.
            byte[] dec = new byte[tl];
            for (int i = 0; i < length; i += 16)
            {
                c.decrypt(toDec, i, dec, i);
            }
            byte[] returnArray = new byte[length];
            System
                .arraycopy(dec, 0, returnArray, 0, length);
            return returnArray;
        }
    }
    
    public static byte[] intToBytes(int i)
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
    
    public static int bytesToInt(byte[] bytes)
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
