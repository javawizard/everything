package net.sf.opengroove.common.utils;

import java.io.File;

import base64.Base64Coder;

/**
 * A collection of utilities for manipulating data, particularly byte arrays.
 * 
 * @author Alexander Boyd
 * 
 */
public class DataUtils
{
    /**
     * Base64 encodes the bytes specified.
     * 
     * @param bytes
     *            the bytes to encode
     * @return the encoded string
     */
    public static String encode(byte[] bytes)
    {
        return new String(Base64Coder.encode(bytes));
    }
    
    /**
     * Base64 decodes the string specified.
     * 
     * @param data
     *            the data to decode
     * @return the decoded data
     */
    public static byte[] decode(String data)
    {
        return Base64Coder.decode(data);
    }
    
    public static long recursiveSizeScan(File file)
    {
        if (!file.exists())
            return 0;
        if (file.isFile())
            return file.length();
        if (file.isDirectory())
        {
            int totalSize = 0;
            for (File f : file.listFiles())
            {
                totalSize += recursiveSizeScan(f);
            }
            return totalSize;
        }
        throw new RuntimeException(
            "unercognized file type for file "
                + file.getAbsolutePath());
    }
}
