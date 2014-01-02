package org.opengroove.g4.common.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.opengroove.g4.common.TemporaryFileStore;

public class FileBlockBuilder implements DataBlockBuilder
{
    private File f;
    
    public FileBlockBuilder()
    {
        f = TemporaryFileStore.createFile();
    }
    
    public DataBlock finish()
    {
        return new FileBlock(f);
    }
    
    public OutputStream getStream()
    {
        try
        {
            return new FileOutputStream(f);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
    }
    
}
