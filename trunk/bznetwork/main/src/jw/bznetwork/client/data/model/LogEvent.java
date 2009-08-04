package jw.bznetwork.client.data.model;

import java.io.Serializable;
import java.util.Date;

public class LogEvent implements Serializable
{
    public LogEvent()
    {
    }
    
    private int serverid;
    private String event;
    private Date when;
    private String source;
    private String target;
    private int sourceid;
    private int targetid;
    private String sourceteam;
    private String targetteam;
    private String ipaddress;
    private String bzid;
    private String email;
    private String metadata;
    private String data;
    
    public int getServerid()
    {
        return serverid;
    }
    
    public void setServerid(int serverid)
    {
        this.serverid = serverid;
    }
    
    public String getEvent()
    {
        return event;
    }
    
    public void setEvent(String event)
    {
        this.event = event;
    }
    
    public Date getWhen()
    {
        return when;
    }
    
    public void setWhen(Date when)
    {
        this.when = when;
    }
    
    public String getSource()
    {
        return source;
    }
    
    public void setSource(String source)
    {
        this.source = source;
    }
    
    public String getTarget()
    {
        return target;
    }
    
    public void setTarget(String target)
    {
        this.target = target;
    }
    
    public int getSourceid()
    {
        return sourceid;
    }
    
    public void setSourceid(int sourceid)
    {
        this.sourceid = sourceid;
    }
    
    public int getTargetid()
    {
        return targetid;
    }
    
    public void setTargetid(int targetid)
    {
        this.targetid = targetid;
    }
    
    public String getSourceteam()
    {
        return sourceteam;
    }
    
    public void setSourceteam(String sourceteam)
    {
        this.sourceteam = sourceteam;
    }
    
    public String getTargetteam()
    {
        return targetteam;
    }
    
    public void setTargetteam(String targetteam)
    {
        this.targetteam = targetteam;
    }
    
    public String getIpaddress()
    {
        return ipaddress;
    }
    
    public void setIpaddress(String ipaddress)
    {
        this.ipaddress = ipaddress;
    }
    
    public String getBzid()
    {
        return bzid;
    }
    
    public void setBzid(String bzid)
    {
        this.bzid = bzid;
    }
    
    public String getMetadata()
    {
        return metadata;
    }
    
    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }
    
    public String getData()
    {
        return data;
    }
    
    public void setData(String data)
    {
        this.data = data;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
}
