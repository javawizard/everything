package org.opengroove.g4.common.data;

import java.io.OutputStream;


/**
 * A block creator. Implementations of this class can take data annd create a
 * block from it.
 * 
 * @author Alexander Boyd
 * 
 */
public interface DataBlockBuilder
{
    /**
     * Gets a stream that can be used to write the block's data. This should
     * only be called once, and the results are undefined if it is called
     * multiple times.
     * 
     * @return
     */
    public OutputStream getStream();
    
    /**
     * Finishes creating the block. If the stream obtained from getStream() has
     * not been closed at this point, the results are undefined.
     * 
     * @return
     */
    public DataBlock finish();
}
