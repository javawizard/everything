package jw.bznetwork.server.live;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import jw.bznetwork.server.BZNetworkServer;

/**
 * This class represents a bzfs server that is currently running. It holds
 * information about the server's in-database id, the server's cached combined
 * groupdb file, the server's process object, the threads that are reading from
 * and writing to the process, the server's current user list, and such.
 * 
 * @author Alexander Boyd
 * 
 */
public class LiveServer
{
    private ReadThread readThread;
    
    public ReadThread getReadThread()
    {
        return readThread;
    }
    
    public void setReadThread(ReadThread readThread)
    {
        this.readThread = readThread;
    }
    
    /**
     * The process that represents the actual bzfs program.
     */
    private Process process;
    /**
     * This server's id, as stored in the servers database table
     */
    private int id;
    /**
     * The list of cache files associated with this server. These should be
     * deleted when the server shuts down.
     */
    private ArrayList<String> tempFiles = new ArrayList<String>();
    /**
     * A queue that, if present, will be notified when either the bznetwork
     * server plugin successfully loads or the bznetwork plugin fails to load.
     */
    private Queue<String> loadListenerQueue;
    
    public Queue<String> getLoadListenerQueue()
    {
        return loadListenerQueue;
    }
    
    public void setLoadListenerQueue(Queue<String> loadListenerQueue)
    {
        this.loadListenerQueue = loadListenerQueue;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    private ArrayList<LivePlayer> players = new ArrayList<LivePlayer>();
    
    public static final int ALL = -1;
    public static final int SERVER = -2;
    public static final int NONE = -3;
    
    private HashMap<String, LivePlayer> callsignsToPlayers = new HashMap<String, LivePlayer>();
    
    private HashMap<Integer, LivePlayer> idsToPlayers = new HashMap<Integer, LivePlayer>();
    
    public HashMap<String, LivePlayer> getCallsignsToPlayers()
    {
        return callsignsToPlayers;
    }
    
    public HashMap<Integer, LivePlayer> getIdsToPlayers()
    {
        return idsToPlayers;
    }
    
    public Process getProcess()
    {
        return process;
    }
    
    public void setProcess(Process process)
    {
        this.process = process;
    }
    
    public ArrayList<LivePlayer> getPlayers()
    {
        return players;
    }
    
    public ArrayList<String> getTempFiles()
    {
        return tempFiles;
    }
    
    public void addTempFile(String file)
    {
        tempFiles.add(file);
    }
    
    /**
     * Called by the read thread to indicate that the read stream has been
     * closed, which currently only occurs when the server shuts down. This
     * method ensures that the process is dead, killing it if it isn't, and then
     * removes it from the server list.
     */
    
    public void completedShutdown()
    {
        synchronized (BZNetworkServer.class)
        {
            try
            {
                process.destroy();
            }
            catch (Exception e)
            {
            }
            BZNetworkServer.getLiveServers().remove(id);
            for (String file : tempFiles)
            {
                new File(BZNetworkServer.cacheFolder, file).delete();
            }
        }
    }
}
