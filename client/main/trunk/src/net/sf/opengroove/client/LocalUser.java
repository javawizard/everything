package net.sf.opengroove.client;

import java.io.Serializable;
import java.math.BigInteger;

public class LocalUser implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 5428195665256519157L;
    /**
     * This user's userid. This is used to figure out which server to connect
     * to, as well as to authenticate with that server.
     */
    private String userid;
    /**
     * The name of this computer that the user has chosen. This is used when
     * authenticating with the server.
     */
    private String computer;
    /**
     * This, along with the other rsa fields, contain the user's security keys.
     */
    private BigInteger rsaEncPub;
    private BigInteger rsaEncPrv;
    private BigInteger rasEncMod;
    private BigInteger rsaSigPub;
    private BigInteger rsaSigPrv;
    private BigInteger rsaSigMod;
    /**
     * The user's password hint. The user can set this up in their settings
     * dialog, and it is usually synchronized between computers.
     */
    private String passwordHint;
    /**
     * The user's encrypted password, in the format returned by
     * {@link net.sf.opengroove.security.Hash#hash(String)}. This is used to
     * authenticate the user.
     */
    private String encPassword;
    /**
     * True if the user should be automatically logged on when OpenGroove
     * starts. storedPassword must not be null if this is true. The hashed value
     * of storedPassword should also match encPassword if this is true.
     */
    private boolean autoSignOn;
    /**
     * This user's time lag as compared with the server. This is equal to
     * <code>myTime - serverTime</code>, where myTime is the value of
     * System.currentTimeMillis() invoked on this computer, and serverTime is
     * the value of System.currentTimeMillis() invoked on the server. The
     * server's time can then be reconstructed by evaluating
     * <code>myTime - lag</code>. This allows for time-sensitive operations
     * to be ordered correctly, even if multiple users don't have the same
     * system time on their computers.
     */
    private long lag;
    /**
     * If this is not null, then this is the plain-text value of the user's
     * password, and the user won't be prompted for their password when they
     * want to sign on.
     */
    private String storedPassword;
    
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
    
    /**
     * Returns the user context for this user, if they are logged in. If not,
     * null is returned. This method is a shortcut for
     * <code>OpenGroove.userContextMap.get(getUserid())</code>.
     * 
     * @return
     */
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
    
    public long getLag()
    {
        return lag;
    }
    
    public long getServerTime()
    {
        return System.currentTimeMillis() - lag;
    }
    
    public void setLag(long lag)
    {
        this.lag = lag;
    }
    
    public String getComputer()
    {
        return computer;
    }
    
    public String getStoredPassword()
    {
        return storedPassword;
    }
    
    public void setComputer(String computer)
    {
        this.computer = computer;
    }
    
    public void setStoredPassword(String storedPassword)
    {
        this.storedPassword = storedPassword;
    }
    
    public String getPasswordHint()
    {
        return passwordHint;
    }
    
    public void setPasswordHint(String passwordHint)
    {
        this.passwordHint = passwordHint;
    }
}
