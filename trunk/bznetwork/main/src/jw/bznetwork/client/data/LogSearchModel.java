package jw.bznetwork.client.data;

import java.io.Serializable;

import jw.bznetwork.client.data.model.Server;

public class LogSearchModel implements Serializable
{
    private Server[] servers;
    private String[] events;
    
    public Server[] getServers()
    {
        return servers;
    }
    
    public void setServers(Server[] servers)
    {
        this.servers = servers;
    }
    
    public String[] getEvents()
    {
        return events;
    }
    
    public void setEvents(String[] events)
    {
        this.events = events;
    }
}
