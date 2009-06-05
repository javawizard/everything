package org.opengroove.g4.common.data;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class ByteBlockBuilder implements DataBlockBuilder
{
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    public DataBlock finish()
    {
        return new ByteBlock(out.toByteArray());
    }
    
    public OutputStream getStream()
    {
        return out;
    }
    
}
