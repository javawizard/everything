package net.sf.opengroove.realmserver.data.model;

public class UserQuota
{
    private String username;
    private String name;
    private int value;
    
    public String getUsername()
    {
        return username;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setValue(int value)
    {
        this.value = value;
    }
}
