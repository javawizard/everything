package net.sf.opengroove.client;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.sf.opengroove.common.utils.Userids;
import net.sf.opengroove.common.vcard.VCard;

public class Contact implements Serializable
{
    private static final long serialVersionUID = -2910467169227988997L;
    
    // TODO: what to do about a contact's computers? They should probably be
    // listed here (so that the user can choose to send a message to just a
    // particular computer if they want)
    /**
     * This contact's userid. This must be a userid; it cannot be a username.
     */
    private String userid;
    private ContactStatus status;
    /**
     * This field, along with {@link #rsaEncMod}, {@link #rsaSigMod}, and
     * {@link #rsaSigPub}, constitute this contact's public security key. These
     * will be null if hasKeys is false.
     */
    private BigInteger rsaEncPub;
    private BigInteger rasEncMod;
    private BigInteger rsaSigPub;
    private BigInteger rsaSigMod;
    /**
     * True if the keys for this contact have been obtained, false if not.
     * Messages to be sent to the contact will be queued until the key is
     * available, and the same with received messages.
     * 
     * Keys are computer-specific. In otherwords, just because one computer has
     * the keys for a specific user doesn't mean another computer does.
     */
    private boolean hasKeys;
    private String realName;
    /**
     * True if the local user has verified this contact. This means that the
     * local user has reviewed the hash of this contact's security keys and
     * contacted the actual user represented by this contact to validate that
     * those keys are correct, and then indicated to opengroove that they have
     * done this.
     * 
     * Verification is computer-specific. In otherwords, if the local user has
     * two computers, and verifies a particular user on one computer, that won't
     * cause that user to show up as verified on any of the user's other
     * computers.
     */
    private boolean isUserVerified;
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
    
    public static enum Names
    {
        userContact, localName
    }
    
    /**
     * True if this contact does for sure exist. This is initially false when a
     * contact is created, and is set to true when actual correspondence with
     * the contact is received, or the contact's server indicates that this
     * contact really does exist. Some operations, such as adding a user to a
     * workspace, won't succeed unless that user has this field set to true.
     */
    private boolean existanceVerified;
    
    /**
     * The list of this contact's computers. This is automatically synchronized
     * every time the local user goes online.
     */
    private ArrayList<ContactComputer> computers = new ArrayList<ContactComputer>();
    
    public String getRealm()
    {
        return Userids.toRealm(userid);
    }
    
    public String getUsername()
    {
        return Userids.toUsername(userid);
    }
    
    public String getUserid()
    {
        return userid;
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
        return "true".equalsIgnoreCase(properties
            .getProperty("" + Names.userContact));
    }
    
    public boolean isUserVerified()
    {
        return isUserVerified;
    }
    
    public void setRealm(String realm)
    {
        this.userid = Userids.setRealm(userid, realm);
    }
    
    public void setUsername(String username)
    {
        this.userid = Userids.setUsername(userid, username);
    }
    
    public void setUserid(String userid)
    {
        this.userid = userid;
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
        properties.setProperty("" + Names.userContact, ""
            + isUserContact);
    }
    
    public void setUserVerified(boolean isUserVerified)
    {
        this.isUserVerified = isUserVerified;
    }
    
    public boolean isHasKeys()
    {
        return hasKeys;
    }
    
    public ArrayList<ContactComputer> getComputers()
    {
        return computers;
    }
    
    public void setHasKeys(boolean hasKeys)
    {
        this.hasKeys = hasKeys;
    }
    
    public String getLocalName()
    {
        return properties.getProperty("" + Names.localName,
            "");
    }
    
    public void setLocalName(String localName)
    {
        properties.setProperty("" + Names.localName,
            localName);
    }
    
    /**
     * If getLocalName() is not null or equal to the empty string, then it's
     * value is returned. Failing that, getRealName() is checked to see if it is
     * not null or the empty string. If it isn't, then it is returned. Failing
     * that, getUserid() is returned.
     * 
     * @return
     */
    public String getDisplayName()
    {
        if (getLocalName() != null
            && !getLocalName().equals(""))
            return getLocalName();
        if (getRealName() != null
            && !getRealName().equals(""))
            return getRealName();
        return getUserid();
    }
    
    public String getRealName()
    {
        return realName;
    }
    
    public void setRealName(String realName)
    {
        this.realName = realName;
    }
    
}
