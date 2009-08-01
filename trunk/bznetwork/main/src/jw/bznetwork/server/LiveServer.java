package jw.bznetwork.server;

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
    
}
