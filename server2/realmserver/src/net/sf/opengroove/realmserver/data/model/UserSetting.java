package net.sf.opengroove.realmserver.data.model;

public class UserSetting
{
    private String username;
    private String name;
    private String value;
    
    public String getUsername()
    {
        return username;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getValue()
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
    
    public void setValue(String value)
    {
        this.value = value;
    }

}
