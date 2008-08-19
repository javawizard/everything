package net.sf.opengroove.client;

import java.io.Serializable;
import java.math.BigInteger;

public class LocalUser implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 5428195665256519157L;
    private String userid;
    private BigInteger rsaEncPub;
    private BigInteger rsaEncPrv;
    private BigInteger rasEncMod;
    private BigInteger rsaSigPub;
    private BigInteger rsaSigPrv;
    private BigInteger rsaSigMod;
    private String encPassword;
    private boolean autoSignOn;
    /**
     * If this is not null, then this is the plain-text value of the user's
     * password, and they should be automatically logged in upon startup of
     * OpenGroove
     */
    private String autoPassword;
    
    public String getRealm()
    {
        return Userids.toRealm(userid);
    }
    
    public String getUserid()
    {
        return userid;
    }
    
    /**
     * Returns true if the user is logged in on this computer.
     * 
     * @return
     */
    public boolean isLoggedIn()
    {
        return getContext() != null;
    }
    
    public UserContext getContext()
    {
        return OpenGroove.userContextMap.get(userid);
    }
    
    public String getUsername()
    {
        return Userids.toUsername(userid);
    }
    
    public String getEncPassword()
    {
        return encPassword;
    }
    
    public void setRealm(String realm)
    {
        userid = Userids.setRealm(userid, realm);
    }
    
    public void setUserid(String userid)
    {
        this.userid = userid;
    }
    
    public void setUsername(String username)
    {
        this.userid = Userids.setUsername(userid, username);
    }
    
    public void setEncPassword(String encPassword)
    {
        this.encPassword = encPassword;
    }

    public BigInteger getRsaEncPub()
    {
        return rsaEncPub;
    }

    public BigInteger getRsaEncPrv()
    {
        return rsaEncPrv;
    }

    public BigInteger getRasEncMod()
    {
        return rasEncMod;
    }

    public BigInteger getRsaSigPub()
    {
        return rsaSigPub;
    }

    public BigInteger getRsaSigPrv()
    {
        return rsaSigPrv;
    }

    public BigInteger getRsaSigMod()
    {
        return rsaSigMod;
    }

    public boolean isAutoSignOn()
    {
        return autoSignOn;
    }

    public String getAutoPassword()
    {
        return autoPassword;
    }

    public void setRsaEncPub(BigInteger rsaEncPub)
    {
        this.rsaEncPub = rsaEncPub;
    }

    public void setRsaEncPrv(BigInteger rsaEncPrv)
    {
        this.rsaEncPrv = rsaEncPrv;
    }

    public void setRasEncMod(BigInteger rasEncMod)
    {
        this.rasEncMod = rasEncMod;
    }

    public void setRsaSigPub(BigInteger rsaSigPub)
    {
        this.rsaSigPub = rsaSigPub;
    }

    public void setRsaSigPrv(BigInteger rsaSigPrv)
    {
        this.rsaSigPrv = rsaSigPrv;
    }

    public void setRsaSigMod(BigInteger rsaSigMod)
    {
        this.rsaSigMod = rsaSigMod;
    }

    public void setAutoSignOn(boolean autoSignOn)
    {
        this.autoSignOn = autoSignOn;
    }

    public void setAutoPassword(String autoPassword)
    {
        this.autoPassword = autoPassword;
    }
}
