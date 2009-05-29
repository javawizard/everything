package com.googlecode.opengroove.g4.client.dynamics;

import java.io.InputStream;

public interface DataBlock
{
    /**
     * Returns a string representation of this data block.
     * 
     * @return
     */
    public String getString();
    
    /**
     * Returns this data block as a new stream that can be used to read the
     * block's data.
     * 
     * @return
     */
    public InputStream getStream();
    
    /**
     * Returns this data block as an array of bytes. Caution should be used when
     * calling this method, as getting the contents of an extremely large block
     * as a byte array might cause an OutOfMemoryError.<br/>
     * <br/>
     * 
     * This byte array may or may not be tied to the actual data, so the effects
     * of modifying it are unspecified. Therefore, it should not be modified.
     * Currently, ByteBlock's byte array is the actual array it uses to store
     * its data, so modifying it would change the data, whereas FileBlock's byte
     * array is re-created each time it is requested.
     * 
     * @return
     */
    public byte[] getBytes();
    
    /**
     * Returns the size, in bytes, of the data represented by this block.
     * 
     * @return
     */
    public int getSize();
}
