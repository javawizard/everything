package net.sf.opengroove.client;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;

public class Contact implements Serializable
{
    private static final long serialVersionUID = -2910467169227988997L;
    
    // TODO: what to do about a contact's computers? They should probably be
    // listed here (so that the user can choose to send a message to just a
    // particular computer if they want)
    
    private String realm;
    private String username;
    private String realName;
    private String localName;
    private BigInteger rsaEncPub;
    private BigInteger rasEncMod;
    private BigInteger rsaSigPub;
    private BigInteger rsaSigMod;
    private boolean isUserContact;
    private boolean isUserVerified;
    
    public String getRealm()
    {
        return realm;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getRealName()
    {
        return realName;
    }
    
    public String getLocalName()
    {
        return localName;
    }
    
    public BigInteger getRsaEncPub()
    {
        return rsaEncPub;
    }
    
    public BigInteger getRasEncMod()
    {
        return rasEncMod;
    }
    
    public BigInteger getRsaSigPub()
    {
        return rsaSigPub;
    }
    
    public BigInteger getRsaSigMod()
    {
        return rsaSigMod;
    }
    
    public boolean isUserContact()
    {
        return isUserContact;
    }
    
    public boolean isUserVerified()
    {
        return isUserVerified;
    }
    
    public void setRealm(String realm)
    {
        this.realm = realm;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setRealName(String realName)
    {
        this.realName = realName;
    }
    
    public void setLocalName(String localName)
    {
        this.localName = localName;
    }
    
    public void setRsaEncPub(BigInteger rsaEncPub)
    {
        this.rsaEncPub = rsaEncPub;
    }
    
    public void setRasEncMod(BigInteger rasEncMod)
    {
        this.rasEncMod = rasEncMod;
    }
    
    public void setRsaSigPub(BigInteger rsaSigPub)
    {
        this.rsaSigPub = rsaSigPub;
    }
    
    public void setRsaSigMod(BigInteger rsaSigMod)
    {
        this.rsaSigMod = rsaSigMod;
    }
    
    public void setUserContact(boolean isUserContact)
    {
        this.isUserContact = isUserContact;
    }
    
    public void setUserVerified(boolean isUserVerified)
    {
        this.isUserVerified = isUserVerified;
    }
}
