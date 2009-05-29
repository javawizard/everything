package com.googlecode.opengroove.g4.client.dynamics;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * A DataBlock that implements its storage in the form of a file. This should be
 * used for data blocks that might be large.
 * 
 * @author Alexander Boyd
 * 
 */
public class FileBlock implements DataBlock
{
    private File file;
    
    /**
     * Creates a new FileBlock from the file specified. This does not mark the
     * file for deletion at VM termination; it's strongly recommended that you
     * call File.deleteOnExit on the passed-in file before you create this
     * FileBlock.
     * 
     * @param file
     */
    public FileBlock(File file)
    {
        this.file = file;
    }
    
    public byte[] getBytes()
    {
        try
        {
            InputStream stream = getStream();
            byte[] bytes = new byte[getSize()];
            int i;
            int offset = 0;
            while ((i = stream.read(bytes, offset, bytes.length - offset)) > 0)
                offset += i;
            stream.close();
            System.out.println("" + offset + " bytes read of " + bytes.length);
            return bytes;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public int getSize()
    {
        return (int) file.length();
    }
    
    public InputStream getStream()
    {
        try
        {
            return new FileInputStream(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public String getString()
    {
        return new String(getBytes());
    }
    
}
