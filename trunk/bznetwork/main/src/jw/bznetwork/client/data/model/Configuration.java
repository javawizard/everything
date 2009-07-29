package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Configuration implements Serializable
{
    private String welcome;
    private String sitename;
    private String contact;
    private boolean menuleft;
    private boolean currentname;
    
    public boolean isMenuleft()
    {
        return menuleft;
    }

    public void setMenuleft(boolean menuleft)
    {
        this.menuleft = menuleft;
    }

    public boolean isCurrentname()
    {
        return currentname;
    }

    public void setCurrentname(boolean currentname)
    {
        this.currentname = currentname;
    }

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
