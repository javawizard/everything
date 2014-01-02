package org.opengroove.g4.client.dynamics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.opengroove.g4.common.data.ByteBlockBuilder;
import org.opengroove.g4.common.data.DataBlock;
import org.opengroove.g4.common.data.DataBlockBuilder;
import org.opengroove.g4.common.data.FileBlockBuilder;

import net.sf.opengroove.common.utils.StringUtils;


/**
 * A command to an engine. This stores the command's name and the command's
 * data.<br/>
 * <br/>
 * 
 * Command names can only contain alphanumeric characters (of any case).
 * Including other characters, such as symbols, might cause problems.
 * 
 * @author Alexander Boyd
 * 
 */
public class Command
{
    /**
     * The maximum size of data that Command will put into a ByteBlock. This is
     * used in the encode method. If the data to be encoded is larger than this,
     * then Command will create a FileBlock instead.
     */
    private static final int MAX_BYTE_BLOCK_SIZE = 1024;
    private String name;
    private DataBlock data;
    
    public Command(String name, DataBlock data)
    {
        super();
        this.name = name;
        this.data = data;
    }
    
    public String getName()
    {
        return name;
    }
    
    public DataBlock getData()
    {
        return data;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Command()
    {
        super();
    }
    
    public void setData(DataBlock data)
    {
        this.data = data;
    }
    
    /**
     * Encodes this command into a newly-created data block. This new data block
     * will include both the command's name and the command's data.
     * 
     * @return
     */
    public DataBlock encode()
    {
        try
        {
            String prefix = name + ':';
            byte[] prefixBytes = prefix.getBytes();
            int totalLength = prefixBytes.length + data.getSize();
            DataBlockBuilder builder;
            if (totalLength > MAX_BYTE_BLOCK_SIZE)
                builder = new FileBlockBuilder();
            else
                builder = new ByteBlockBuilder();
            OutputStream out = builder.getStream();
            out.write(prefixBytes);
            InputStream in = data.getStream();
            StringUtils.copy(in, out);
            in.close();
            out.flush();
            out.close();
            return builder.finish();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
    }
    
    public static Command decode(DataBlock block)
    {
        try
        {
            InputStream in = block.getStream();
            StringBuffer nameBuffer = new StringBuffer();
            int i;
            while ((i = in.read()) != -1 && i != ':')
            {
                nameBuffer.append((char) i);
            }
            String name = nameBuffer.toString();
            DataBlockBuilder builder;
            if (block.getSize() > MAX_BYTE_BLOCK_SIZE)
                builder = new FileBlockBuilder();
            else
                builder = new ByteBlockBuilder();
            OutputStream out = builder.getStream();
            StringUtils.copy(in, out);
            in.close();
            out.flush();
            out.close();
            return new Command(name, builder.finish());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
    }
}
