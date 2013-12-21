package jw.bznetwork.client.data;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents some filters to search the log files for.
 * 
 * @author Alexander Boyd
 * 
 */
public class LogsFilterSettings
{
    private Date start;
    private Date end;
    
    public Date getStart()
    {
        return start;
    }
    
    public void setStart(Date start)
    {
        this.start = start;
    }
    
    public Date getEnd()
    {
        return end;
    }
    
    public void setEnd(Date end)
    {
        this.end = end;
    }
    
    public String getSearch()
    {
        return search;
    }
    
    public void setSearch(String search)
    {
        this.search = search;
    }
    
    public boolean isIgnoreCase()
    {
        return ignoreCase;
    }
    
    public void setIgnoreCase(boolean ignoreCase)
    {
        this.ignoreCase = ignoreCase;
    }
    
    public ArrayList<String> getSearchIn()
    {
        return searchIn;
    }
    
    public ArrayList<Integer> getServers()
    {
        return servers;
    }
    
    public ArrayList<String> getEvents()
    {
        return events;
    }
    
    private String search;
    private boolean ignoreCase;
    private ArrayList<String> searchIn = new ArrayList<String>();
    private ArrayList<Integer> servers = new ArrayList<Integer>();
    private ArrayList<String> events = new ArrayList<String>();
}
