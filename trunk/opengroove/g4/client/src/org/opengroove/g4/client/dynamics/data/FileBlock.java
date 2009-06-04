package org.opengroove.g4.client.dynamics.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.opengroove.g4.client.TemporaryFileStore;

import net.sf.opengroove.common.utils.StringUtils;

/**
 * A DataBlock that implements its storage in the form of a file. This should be
 * used for data blocks that might be large.
 * 
 * @author Alexander Boyd
 * 
 */
public class FileBlock implements DataBlock
{
    private transient File file;
    
    /**
     * Creates a new FileBlock from the file specified. This marks the file for
     * deletion from the VM when it shuts down, so important files should not be
     * included here. When this block is released, the file will be deleted.
     * 
     * @param file
     *            The file to use as the data for this block
     */
    public FileBlock(File file)
    {
        file.deleteOnExit();
        this.file = file;
    }
    
    protected void finalize() throws Throwable
    {
        System.out.println("Finalizing a FileBlock");
        file.delete();
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
    
    public void release()
    {
        file.delete();
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
        out.writeInt(getSize());
        InputStream stream = getStream();
        StringUtils.copy(stream, out);
        stream.close();
    }
    
    private void readObject(ObjectInputStream in) throws IOException,
        ClassNotFoundException
    {
        in.defaultReadObject();
        file = TemporaryFileStore.createFile();
        // FIXME: finish this up, read data from the stream and write it to the
        // file, deleteOnExit() the file
    }
    
}
