package net.sf.opengroove.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple checksum, taken from Evaluation Portal
 * (http://www.evaluationportal.com or http://www.trivergia.com/surveySite). It
 * is not designed to be secure, and it would be easy for an attacker to create
 * a file that has a predetermined checksum. It works by splitting the file into
 * blocks of {@link #POSITIVE_LENGTH} length, and XORing all of these blocks
 * together. This is called the positive checksum. It then splits the file into
 * blocks of {@link #NEGATIVE_LENGTH} length, and XORing the inverse of these
 * blocks together (IE each byte of these blocks is XORed with 0xFF or 255
 * before being XORed into the checksum). This is called the negative checksum.
 * It then returns, as the checksum, the number of bytes in the input to
 * checksum, in base 10, the character 'x', the base 16 representation of the
 * positive checksum, the character 'x', and the base 16 representation of the
 * negative checksum.
 * 
 * @author Alexander Boyd
 * 
 */
public class A7Checksum
{
    /**
     * Prints out two checksums; the first is the checksum of the string "hi",
     * and the second is the checksum of the string "hi" with a null character
     * at the end (IE 3 characters total).
     * 
     * @param args
     *            The arguments passed by the JVM
     */
    public static void main(String[] args)
    {
        System.out.println(checksum(new String(new char[] {
            'h', 'i' })));
        System.out.println(checksum(new String(new char[] {
            'h', 'i', 0 })));
    }
    
    /**
     * The length of the positive checksum
     */
    public static final int POSITIVE_LENGTH = 11;
    /**
     * The length of the negative checksum
     */
    public static final int NEGATIVE_LENGTH = 8;
    
    /**
     * Creates a checksum for the input specified.
     * 
     * @param input
     *            The input to checksum. Each byte of
     *            <code>input.getBytes()</code> will be processed and added to
     *            the checksum.
     * @return The checksum of the input specified
     */
    public static String checksum(String input)
    {
        return checksum(new ByteArrayInputStream(input
            .getBytes()));
    }
    
    /**
     * Creates a checksum of the stream specified. The stream is read until the
     * end (IE -1 is returned from the read method), and the bytes read are
     * checksummed.<br/><br/>
     * 
     * This method, as well as {@link #checksum(InputStream, int, int)}, is
     * safe for use with large files, as it does not load the entire file into
     * memory at one time; it only loads blocks of 4096 bytes of the file at a
     * time, and stores {@link #POSITIVE_LENGTH} + {@link #NEGATIVE_LENGTH}
     * bytes at a time for the actual checksum operation.
     * 
     * @param input
     *            The input to checksum
     * @return The resulting checksum
     */
    public static String checksum(InputStream input)
    {
        try
        {
            return checksum(input, POSITIVE_LENGTH,
                NEGATIVE_LENGTH);
        }
        catch (IOException e)
        {
            // TODO Feb 5, 2008 Auto-generated catch block
            throw new RuntimeException(
                "TODO auto generated on Feb 5, 2008 : "
                    + e.getClass().getName() + " - "
                    + e.getMessage(), e);
        }
    }
    
    /**
     * Creates a checksum of the input specified, exactly as
     * {@link #checksum(InputStream)} does, but using the specified positive and
     * negative checksum lengths instead of those contained in
     * {@link #POSITIVE_LENGTH} and {@link #NEGATIVE_LENGTH}.
     * 
     * @param fis2
     * @param positiveChecksumLength
     * @param negativeChecksumLength
     * @return
     * @throws IOException
     */
    public static String checksum(InputStream fis2,
        int positiveChecksumLength,
        int negativeChecksumLength) throws IOException
    {
        try
        {
            BufferedInputStream fis = new BufferedInputStream(
                fis2, 4096);
            byte[] positiveChecksum = new byte[positiveChecksumLength];
            byte[] negativeChecksum = new byte[negativeChecksumLength];
            int currentChecksumIndex = 0;
            int i;
            while ((i = fis.read()) != -1)
            {
                positiveChecksum[currentChecksumIndex
                    % positiveChecksum.length] ^= i;
                negativeChecksum[currentChecksumIndex
                    % negativeChecksum.length] ^= (i ^ 0xFF);
                currentChecksumIndex++;
            }
            String checksum = "" + currentChecksumIndex
                + "x";
            for (i = 0; i < positiveChecksum.length; i++)
            {
                checksum += Integer
                    .toHexString(positiveChecksum[i] + 128);
            }
            checksum += "x";
            for (i = 0; i < negativeChecksum.length; i++)
            {
                checksum += Integer
                    .toHexString(negativeChecksum[i] + 128);
            }
            fis.close();
            fis2.close();
            return checksum;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
