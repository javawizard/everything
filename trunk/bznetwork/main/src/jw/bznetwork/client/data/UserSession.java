package jw.bznetwork.client.data;

import java.io.Serializable;

public class UserSession implements Serializable
{
    /**
     * The id of the session
     */
    private String id;
    /**
     * The user that is logged into this session, or null if the user has not
     * yet logged in
     */
    private AuthUser user;
    private String ip;
    private String userAgent;
    private long lastAccessTime;
    private long loggedIn;
    
    public String getUserAgent()
    {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }
    
    public long getLastAccessTime()
    {
        return lastAccessTime;
    }
    
    public void setLastAccessTime(long lastAccessTime)
    {
        this.lastAccessTime = lastAccessTime;
    }
    
    public long getLoggedIn()
    {
        return loggedIn;
    }
    
    public void setLoggedIn(long loggedIn)
    {
        this.loggedIn = loggedIn;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public AuthUser getUser()
    {
        return user;
    }
    
    public void setUser(AuthUser user)
    {
        this.user = user;
    }
    
    public UserSession()
    {
    }
}
