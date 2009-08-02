package jw.bznetwork.server.live;

import java.util.ArrayList;

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
     * The process that represents the actual bzfs program.
     */
    private Process process;
    
    private ArrayList<LivePlayer> players = new ArrayList<LivePlayer>();
    
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
}
