package net.sf.opengroove.client.storage;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Properties;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.UserContext;
import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.common.utils.Userids;
import net.sf.opengroove.common.vcard.VCard;

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
     * The public security key of this user's realm server.
     */
    private BigInteger serverRsaPub;
    /**
     * The security key modulus of this user's realm server.
     */
    private BigInteger serverRsaMod;
    /**
     * The name of this computer that the user has chosen. This is used when
     * authenticating with the server.
     */
    private String computer;
    /**
     * This, along with the other rsa fields, contain the user's security keys.
     * These are stored in the public-rsa-enc-pub, public-rsa-enc-mod,
     * public-rsa-sig-pub, and public-rsa-sig-mod user properties. the private
     * keys are not stored on the server.
     */
    private BigInteger rsaEncPub;
    private BigInteger rsaEncPrv;
    private BigInteger rasEncMod;
    private BigInteger rsaSigPub;
    private BigInteger rsaSigPrv;
    private BigInteger rsaSigMod;
    /**
     * The user's password hint. The user can set this up in their settings
     * dialog, but it is not synchronized between computers at this time, for
     * security reasons.
     */
    private String passwordHint;
    /**
     * The user's encrypted password, in the format returned by
     * {@link net.sf.opengroove.common.security.Hash#hash(String)}. This is
     * used to authenticate the user.
     */
    private String encPassword;
    /**
     * True if the user should be automatically logged on when OpenGroove
     * starts. storedPassword must not be null if this is true. The hashed value
     * of storedPassword should also match encPassword if this is true.
     */
    private boolean autoSignOn;
    /**
     * True if this user has chosen to be publicly visible. This directly
     * corresponds to {@link CommandCommunicator#getVisibility()}. This <i>does</i>
     * synchronize between computers.
     */
    private boolean isSearchVisible;
    /**
     * True if isSearchVisible has been changed since the last time this
     * computer connected to the internet. If this is true when an internet
     * connection is established, then this value will be uploaded to the
     * server. If not, the server's value will be downloaded.
     */
    private boolean needsSearchVisibleUpdate;
    /**
     * True if this user has chosen to be locally visible. If this is true, then
     * the user will respond to multicast queries for users on the same network.
     * This does not synchronize between computers.
     */
    private boolean isLocalVisible;
    
    private static enum Names
    {
        emailAddress, realName, publicEmailAddress
    }
    
    private Properties properties = new Properties();
    private Properties lastModified = new Properties();
    
    public long getLastModified(Names name)
    {
        String value = lastModified.getProperty("" + name);
        if (value == null)
            return 0;
        try
        {
            return Long.parseLong(value);
        }
        catch (Exception exception)
        {
            return 0;
        }
    }
    
    public void setLastModified(Names name, long date)
    {
        lastModified.setProperty("" + name, "" + date);
    }
    
    public Properties getLastModifiedProperties()
    {
        return lastModified;
    }
    
    public Properties getProperties()
    {
        return properties;
    }
    
    /**
     * This user's time lag as compared with the server. This is equal to
     * <code>myTime - serverTime</code>, where myTime is the value of
     * System.currentTimeMillis() invoked on this computer, and serverTime is
     * the value of System.currentTimeMillis() invoked on the server. The
     * server's time can then be reconstructed by evaluating
     * <code>myTime - lag</code>. This allows for time-sensitive operations
     * to be ordered correctly, even if multiple users don't have the same
     * system time on their computers.<br/><br/>
     * 
     * This is not synchronized between the user's computers, as it is usually
     * different for all of them.
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
    
    public BigInteger getServerRsaPub()
    {
        return serverRsaPub;
    }
    
    public void setServerRsaPub(BigInteger serverRsaPub)
    {
        this.serverRsaPub = serverRsaPub;
    }
    
    public BigInteger getServerRsaMod()
    {
        return serverRsaMod;
    }
    
    public void setServerRsaMod(BigInteger serverRsaMod)
    {
        this.serverRsaMod = serverRsaMod;
    }
    
    public boolean isSearchVisible()
    {
        return isSearchVisible;
    }
    
    public boolean isLocalVisible()
    {
        return isLocalVisible;
    }
    
    public String getEmailAddress()
    {
        return properties.getProperty(""
            + Names.emailAddress, "");
    }
    
    public void setSearchVisible(boolean isSearchVisible)
    {
        this.isSearchVisible = isSearchVisible;
    }
    
    public void setLocalVisible(boolean isLocalVisible)
    {
        this.isLocalVisible = isLocalVisible;
    }
    
    public void setEmailAddress(String emailAddress)
    {
        properties.setProperty("" + Names.emailAddress,
            emailAddress);
    }
    
    public String getRealName()
    {
        return properties.getProperty("" + Names.realName);
    }
    
    public void setRealName(String realName)
    {
        properties.setProperty("" + Names.realName,
            realName);
    }
    
    public void setPublicEmailAddress(String addr)
    {
        properties.setProperty(""
            + Names.publicEmailAddress, addr);
    }
    
    public String getPublicEmailAddress()
    {
        return properties.getProperty(""
            + Names.publicEmailAddress, "");
    }
}
