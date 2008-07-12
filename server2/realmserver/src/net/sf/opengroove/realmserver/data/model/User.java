package net.sf.opengroove.realmserver.data.model;

public class User
{
    private String username;
    private String password;
    private boolean publiclyListed;
    
    public String getUsername()
    {
        return username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public boolean isPubliclylisted()
    {
        return publiclyListed;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void setPubliclylisted(boolean publiclyListed)
    {
        this.publiclyListed = publiclyListed;
    }
}
