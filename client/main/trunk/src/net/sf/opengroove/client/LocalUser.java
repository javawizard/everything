package net.sf.opengroove.client;

import java.io.Serializable;

public class LocalUser implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 5428195665256519157L;
    private String realm;
    private String username;
    private String encPassword;
    
    public String getRealm()
    {
        return realm;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getEncPassword()
    {
        return encPassword;
    }
    
    public void setRealm(String realm)
    {
        this.realm = realm;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setEncPassword(String encPassword)
    {
        this.encPassword = encPassword;
    }
}
