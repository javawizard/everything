package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Configuration implements Serializable
{
    private String welcome;
    private String sitename;
    private String contact;
    
    public String getWelcome()
    {
        return welcome;
    }
    
    public void setWelcome(String welcome)
    {
        this.welcome = welcome;
    }
    
    public String getSitename()
    {
        return sitename;
    }
    
    public void setSitename(String sitename)
    {
        this.sitename = sitename;
    }
    
    public String getContact()
    {
        return contact;
    }
    
    public void setContact(String contact)
    {
        this.contact = contact;
    }
    
    public String getExecutable()
    {
        return executable;
    }
    
    public void setExecutable(String executable)
    {
        this.executable = executable;
    }
    
    private String executable;
}
