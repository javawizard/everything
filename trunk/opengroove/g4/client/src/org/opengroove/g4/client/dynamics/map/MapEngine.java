package org.opengroove.g4.client.dynamics.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;

import org.opengroove.g4.client.dynamics.ByteBlock;
import org.opengroove.g4.client.dynamics.Command;
import org.opengroove.g4.client.dynamics.DataBlock;
import org.opengroove.g4.client.dynamics.Engine;
import org.opengroove.g4.client.dynamics.EngineReader;
import org.opengroove.g4.client.dynamics.EngineWriter;


/**
 * An engine that provides a conceptual map (in the java.util.Map sense),
 * mapping keys to values.
 * 
 * @author Alexander Boyd
 * 
 */
public class MapEngine implements Engine
{
    Properties props = new Properties();
    
    private File mainFile;
    
    private File backupFile;
    
    public void init(File storage)
    {
        try
        {
            mainFile = new File(storage, "values.props");
            backupFile = new File(storage, "backup.props");
            /*
             * Possible combinations: if backup exists but values do not, rename
             * backup to values. If values exists but backup does not (which
             * will be the case almost all of the time), then do nothing. If
             * both exist, delete backup. If neither exist, create values as an
             * empty file.
             */
            if (backupFile.exists() && !mainFile.exists())
                backupFile.renameTo(mainFile);
            else if (mainFile.exists() && !backupFile.exists())
                ;
            else if (mainFile.exists() && backupFile.exists())
                if (!backupFile.delete())
                    throw new RuntimeException("Couldn't delete backup");
            if (!mainFile.exists())
                mainFile.createNewFile();
            props.load(new FileInputStream(mainFile));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public synchronized DataBlock[] applyCommands(DataBlock[] reverts,
        Command[] commands)
    {
        /*
         * Revert format is the first char being "s" or "r", and then the data,
         * in the exact same form that a command would expect.
         */
        for (DataBlock block : reverts)
        {
            String blockData = block.getString();
            if (blockData.equals(""))
                // occurs when this is a revert for a remove command where the
                // property to remove didn't exist anyway
                continue;
            /*
             * We don't care about the return value of applySingleCommand here,
             * since you can revert a revert by just re-running the
             * corresponding command again
             */
            if (blockData.startsWith("s"))
                applySingleCommand(new Command("SET", new ByteBlock(blockData
                    .substring(1))));
            else if (blockData.startsWith("r"))
                applySingleCommand(new Command("REMOVE", new ByteBlock(blockData
                    .substring(1))));
            else
                throw new RuntimeException();
        }
        /**
         * Everything's reverted. Now run the new commands.
         */
        DataBlock[] newReverts = new DataBlock[commands.length];
        for (int i = 0; i < commands.length; i++)
        {
            newReverts[i] = applySingleCommand(commands[i]);
        }
        /*
         * Commands are applied. Now save changes to disk and return reverts.
         */
        try
        {
            props.store(new FileOutputStream(backupFile), "MapEngine values");
            mainFile.delete();
            backupFile.renameTo(mainFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
        return newReverts;
    }
    
    /**
     * Applies a single command. This just applies it to <tt>props</tt>, it
     * doesn't save it to disk or anything. It also generates a revert string
     * based on the current properties.
     * 
     * @param command
     */
    private DataBlock applySingleCommand(Command command)
    {
        String commandData = command.getData().getString();
        String revertData;
        if (command.getName().equals("SET"))
        {
            String[] tokens = commandData.split("\\=");
            String key = URLDecoder.decode(tokens[0]);
            String value = URLDecoder.decode(tokens[1]);
            if (props.getProperty(key) != null)
                revertData =
                    "s" + URLEncoder.encode(key) + "="
                        + URLEncoder.encode(props.getProperty(key));
            else
                revertData = "r" + URLEncoder.encode(key);
            props.setProperty(key, value);
        }
        else if (command.getName().equals("REMOVE"))
        {
            String key = URLDecoder.decode(commandData);
            if (props.getProperty(key) != null)
                revertData =
                    "s" + URLEncoder.encode(key) + "="
                        + URLEncoder.encode(props.getProperty(key));
            else
                revertData = "";
            props.remove(key);
        }
        else
            throw new RuntimeException();
        return new ByteBlock(revertData);
    }
    
    public MapReader createReader()
    {
        return new MapReader(this);
    }
    
    public MapWriter createWriter()
    {
        return new MapWriter();
    }
    
}
