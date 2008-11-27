package net.sf.opengroove.common.utils;

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
}
