package org.opengroove.g4.common;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.sound.midi.Sequence;

import net.sf.opengroove.common.utils.DataUtils;
import net.sf.opengroove.common.utils.StringUtils;

/**
 * A store that can be used to create temporary files. I created this class
 * since File.createTempFile() has a habit of throwing an IOException every once
 * in a while, and because if I use my own class I can delete all temporary
 * files when the VM is first loaded, since they aren't ever used across
 * multiple VM invocations.<br/>
 * <br/>
 * 
 * Temporary files created by this class will be marked as deleteOnExit.
 * Temporary folders created by this class won't have this property; they will,
 * however, be deleted the next time the VM starts up and the file store is
 * used.<br/>
 * <br/>
 * 
 * The first time (in a particular VM invocation) this class is used, it will
 * attempt to delete all files that are in the store, to get rid of any files
 * that might not have been deleted by the VM when it exited. It will also do
 * this for directories. Thereafter, files will not be deleted until the next VM
 * restart or shutdown.<br/>
 * <br/>
 * 
 * This class does not register a shutdown hook to delete files, so they could
 * persist after VM shutdown.<br/>
 * <br/>
 * 
 * If this class is not initialized with a file store to use before it is
 * accessed for the first time, then it will use the store
 * "storage/tmp/filestore". <br/>
 * <br/>
 * 
 * The current user must have write permissions to the parent folder of the file
 * store, not just write permissions to the file store itself.
 * 
 * @author Alexander Boyd
 * 
 */
public class TemporaryFileStore
{
    private static AtomicLong sequence = new AtomicLong();
    
    private static File store;
    
    public static synchronized void init(File store)
    {
        if (TemporaryFileStore.store != null)
            throw new IllegalStateException("The store is already initialized");
        TemporaryFileStore.store = store;
        DataUtils.recursiveDelete(store);
        store.mkdirs();
    }
    
    public static synchronized File createFile()
    {
        if (store == null)
            initDefault();
        File f =
            new File(store, "file-" + System.currentTimeMillis() + "-"
                + sequence.getAndIncrement());
        try
        {
            f.createNewFile();
            f.deleteOnExit();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Exception while creating a temporary file: "
                + e.getClass().getName() + ": " + e.getMessage(), e);
        }
        return f;
    }
    
    /**
     * Creates a new temporary file. This file, however, will not be deleted on
     * VM exit. It will be deleted on VM startup if it is not moved elsewhere,
     * though.
     * 
     * @return
     */
    public static synchronized File createPersistentFile()
    {
        if (store == null)
            initDefault();
        File f =
            new File(store, "file-" + System.currentTimeMillis() + "-"
                + sequence.getAndIncrement());
        try
        {
            f.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Exception while creating a temporary file: "
                + e.getClass().getName() + ": " + e.getMessage(), e);
        }
        return f;
    }
    
    public static synchronized File createFolder()
    {
        if (store == null)
            initDefault();
        File f =
            new File(store, "file-" + System.currentTimeMillis() + "-"
                + sequence.getAndIncrement());
        if (!f.mkdir())
            throw new RuntimeException("Couldn't create temp dir");
        return f;
    }
    
    private static void initDefault()
    {
        init(new File("storage/tmp/filestore"));
    }
}
