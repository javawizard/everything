package net.sf.opengroove.realmserver.data.model;

/*
 * TODO: consider merging with net.sf.opengroove.client.Subscription and putting into OpenGroove Common
 */
public class Subscription
{
    private String type;
    private String username;
    private String onusername;
    private String oncomputername;
    private String onsettingname;
    private boolean deletewithtarget;
    private String properties;
    
    public String getType()
    {
        return type;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getOnusername()
    {
        return onusername;
    }
    
    public String getOncomputername()
    {
        return oncomputername;
    }
    
    public String getOnsettingname()
    {
        return onsettingname;
    }
    
    public boolean isDeletewithtarget()
    {
        return deletewithtarget;
    }
    
    public String getProperties()
    {
        return properties;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setOnusername(String onusername)
    {
        this.onusername = onusername;
    }
    
    public void setOncomputername(String oncomputername)
    {
        this.oncomputername = oncomputername;
    }
    
    public void setOnsettingname(String onsettingname)
    {
        this.onsettingname = onsettingname;
    }
    
    public void setDeletewithtarget(boolean deletewithtarget)
    {
        this.deletewithtarget = deletewithtarget;
    }
    
    public void setProperties(String properties)
    {
        this.properties = properties;
    }
}
