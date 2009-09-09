package jw.bznetwork.server.live;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Queue;

import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.live.LivePlayer;
import jw.bznetwork.client.live.LivePlayer.GameType;
import jw.bznetwork.client.live.LivePlayer.TeamType;
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
    /**
     * True if a state change has been requested of the server but the server
     * has not followed it yet, false if no such change has been requested.
     */
    private boolean changingState;
    private boolean starting;
    private OutputStream out;
    private GameType gameType;
    /**
     * The server as it existed when this live server was started.
     */
    private Server server;
    public Server getServer()
    {
        return server;
    }

    public void setServer(Server server)
    {
        this.server = server;
    }

    /**
     * The version of the bznetwork plugin that is being used by this server.
     */
    private String pluginVersion;
    
    public String getPluginVersion()
    {
        return pluginVersion;
    }
    
    public void setPluginVersion(String pluginVersion)
    {
        this.pluginVersion = pluginVersion;
    }
    
    private EnumMap<LivePlayer.TeamType, Integer> teamLimits = new EnumMap<LivePlayer.TeamType, Integer>(
            LivePlayer.TeamType.class);
    
    public OutputStream getOut()
    {
        return out;
    }
    
    public EnumMap<LivePlayer.TeamType, Integer> getTeamLimits()
    {
        return teamLimits;
    }
    
    public void setOut(OutputStream out)
    {
        this.out = out;
    }
    
    public boolean isStarting()
    {
        return starting;
    }
    
    public void setStarting(boolean starting)
    {
        this.starting = starting;
    }
    
    public boolean isChangingState()
    {
        return changingState;
    }
    
    public void setChangingState(boolean changingState)
    {
        this.changingState = changingState;
    }
    
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
    
    /**
     * Requests that this server be shut down. The plugin will be sent a message
     * telling it to shut down the server.
     */
    public void requestShutdown()
    {
        setChangingState(true);
        send("shutdown");
    }
    
    /**
     * Sends the specified text to the plugin as a command.
     * 
     * @param string
     */
    public void send(String string)
    {
        if (string.contains("\r") || string.contains("\n"))
            throw new IllegalArgumentException(
                    "Can't send a carriage return or a newline to a server");
        try
        {
            out.write((string + BZNetworkServer.newline).getBytes());
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Forces this server to shut down. This can be used when requestShutdown is
     * not working properly. This method destroys the server process without
     * allowing the server to perform any cleanup.
     */
    public void forceShutdown()
    {
        process.destroy();
    }
    
    public GameType getGameType()
    {
        return gameType;
    }
    
    public void setGameType(GameType gameType)
    {
        this.gameType = gameType;
    }
    
    public void sayToFromPlayer(String from, String to, String message)
    {
        send("saytofromplayer " + from + "|" + to + "|" + message);
        if (readThread != null)
            readThread.processChatMessage("" + SERVER + "|" + ALL + "|"
                    + TeamType.noteam.name() + "|" + message);
    }
}
