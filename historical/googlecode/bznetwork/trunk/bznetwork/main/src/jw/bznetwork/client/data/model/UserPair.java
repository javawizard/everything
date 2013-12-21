package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class UserPair implements Serializable
{
    private String provider;
    private String user;
    
    public String getProvider()
    {
        return provider;
    }
    
    public void setProvider(String provider)
    {
        this.provider = provider;
    }
    
    public String getUser()
    {
        return user;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    public UserPair()
    {
        super();
    }
}
