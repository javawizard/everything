package org.opengroove.g4.common.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * A DataBlock that implements its storage in the form of a byte array. This
 * should be used for data that is more on the smaller size.
 * 
 * @author Alexander Boyd
 * 
 */
public class ByteBlock implements DataBlock
{
    private byte[] bytes;
    
    /**
     * Creates a new ByteBlock containing the specified data.
     * 
     * @param bytes
     */
    public ByteBlock(byte[] bytes)
    {
        this.bytes = bytes;
    }
    
    /**
     * Creates a new ByteBlock containing the string specified. This is
     * equivalent to <tt>new ByteBlock(data.getBytes())</tt>.
     * 
     * @param data
     */
    public ByteBlock(String data)
    {
        this(data.getBytes());
    }
    
    public byte[] getBytes()
    {
        return bytes;
    }
    
    public InputStream getStream()
    {
        return new ByteArrayInputStream(bytes);
    }
    
    public String getString()
    {
        return new String(bytes);
    }
    
    public int getSize()
    {
        return bytes.length;
    }
    
    public void release()
    {
        bytes = null;
    }
    
}
