package net.sf.opengroove.realmserver.data.model;

public class Computer
{
    private String username;
    private String computerName;
    private String type;
    private String capabilities;
    private long lastOnline;
    
    public String getUsername()
    {
        return username;
    }
    
    public String getComputername()
    {
        return computerName;
    }
    
    public String getType()
    {
        return type;
    }
    
    public long getLastonline()
    {
        return lastOnline;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setComputername(String computerName)
    {
        this.computerName = computerName;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setLastonline(long lastOnline)
    {
        this.lastOnline = lastOnline;
    }
    
    public String getCapabilities()
    {
        return capabilities;
    }
    
    public void setCapabilities(String capabilities)
    {
        this.capabilities = capabilities;
    }
}
