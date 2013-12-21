package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Server implements Serializable
{
    private int serverid;
    private int groupid;
    private String name;
    
    public Server()
    {
    }
    
    private int port;
    private boolean listed;
    private boolean running;
    private boolean dirty;
    private String notes;
    private boolean inheritgroupdb;
    private int loglevel;
    
    public int getLoglevel()
    {
        return loglevel;
    }
    
    public void setLoglevel(int loglevel)
    {
        this.loglevel = loglevel;
    }
    
    private int banfile;
    
    public int getBanfile()
    {
        return banfile;
    }
    
    public void setBanfile(int banfile)
    {
        this.banfile = banfile;
    }
    
    public int getServerid()
    {
        return serverid;
    }
    
    public void setServerid(int serverid)
    {
        this.serverid = serverid;
    }
    
    public int getGroupid()
    {
        return groupid;
    }
    
    public void setGroupid(int groupid)
    {
        this.groupid = groupid;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public boolean isListed()
    {
        return listed;
    }
    
    public void setListed(boolean listed)
    {
        this.listed = listed;
    }
    
    public boolean isRunning()
    {
        return running;
    }
    
    public void setRunning(boolean running)
    {
        this.running = running;
    }
    
    public boolean isDirty()
    {
        return dirty;
    }
    
    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }
    
    public String getNotes()
    {
        return notes;
    }
    
    public void setNotes(String notes)
    {
        this.notes = notes;
    }
    
    public boolean isInheritgroupdb()
    {
        return inheritgroupdb;
    }
    
    public void setInheritgroupdb(boolean inheritgroupdb)
    {
        this.inheritgroupdb = inheritgroupdb;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public boolean equals(Object o)
    {
        return (o instanceof Server) && ((Server) o).serverid == this.serverid;
    }
    
    public int hashCode()
    {
        return serverid;
    }
}
