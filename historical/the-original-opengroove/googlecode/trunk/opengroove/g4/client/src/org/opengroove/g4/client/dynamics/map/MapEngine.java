package org.opengroove.g4.client.dynamics.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.opengroove.g4.client.dynamics.Command;
import org.opengroove.g4.client.dynamics.Engine;
import org.opengroove.g4.client.dynamics.EngineReader;
import org.opengroove.g4.client.dynamics.EngineWriter;
import org.opengroove.g4.common.data.ByteBlock;

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
    
    ReentrantReadWriteLock lock;
    
    public void init(File storage)
    {
        try
        {
            lock = new ReentrantReadWriteLock();
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
    
    /**
     * Applies a single command. This just applies it to <tt>props</tt>, it
     * doesn't save it to disk or anything. It also generates a revert string
     * based on the current properties.
     * 
     * @param command
     */
    public Command applyCommand(Command command)
    {
        String commandData = command.getData().getString();
        String revertData;
        String revertCommand;
        if (command.getName().equals("SET"))
        {
            String[] tokens = commandData.split("\\=");
            String key = URLDecoder.decode(tokens[0]);
            String value = URLDecoder.decode(tokens[1]);
            if (props.getProperty(key) != null)
            {
                revertCommand = "SET";
                revertData =
                    URLEncoder.encode(key) + "="
                        + URLEncoder.encode(props.getProperty(key));
            }
            else
            {
                revertCommand = "REMOVE";
                revertData = URLEncoder.encode(key);
            }
            props.setProperty(key, value);
        }
        else if (command.getName().equals("REMOVE"))
        {
            String key = URLDecoder.decode(commandData);
            if (props.getProperty(key) != null)
            {
                revertCommand = "SET";
                revertData =
                    URLEncoder.encode(key) + "="
                        + URLEncoder.encode(props.getProperty(key));
            }
            else
            {
                revertCommand = "NOP";
                revertData = "";
            }
            props.remove(key);
        }
        else if (command.getName().equals("NOP"))
        {
            revertCommand = "NOP";
            revertData = "";
        }
        else
            throw new RuntimeException();
        return new Command(revertCommand, new ByteBlock(revertData));
    }
    
    public MapReader createReader()
    {
        return new MapReader(this);
    }
    
    public MapWriter createWriter()
    {
        return new MapWriter();
    }
    
    public void lock()
    {
        lock.writeLock().lock();
    }
    
    public void unlock()
    {
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
        finally
        {
            lock.writeLock().unlock();
        }
    }
    
}
