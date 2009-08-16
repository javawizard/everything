package jw.bznetwork.client.data.model;

import java.util.Date;

public class LogRequest
{
    private Date start;
    private Date end;
    private String filter;
    
    public Date getStart()
    {
        return start;
    }
    
    public LogRequest()
    {
        super();
    }
    
    public LogRequest(Date end, String filter, Date start)
    {
        super();
        this.end = end;
        this.filter = filter;
        this.start = start;
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
    
    public String getFilter()
    {
        return filter;
    }
    
    public void setFilter(String filter)
    {
        this.filter = filter;
    }
}
