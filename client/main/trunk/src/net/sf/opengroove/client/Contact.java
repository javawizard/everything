package net.sf.opengroove.client;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

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
    /**
     * This contact's real name. This is available in the user's user property
     * public-real-name.
     */
    private String realName;
    /**
     * A name set for this contact by the local user. If this is null, then
     * realName should be displayed to the user. If this is not null, then this
     * should be displayed to the user in place of realName.
     */
    private String localName;
    
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
     */
    private boolean hasKeys;
    /**
     * True if the user is a contact. This may seem somewhat counter-intuitive
     * given the name of this class, but Contact objects are created for all
     * users with which this user corresponds, in order to store the public
     * security keys for those users. This field, then, is true if the user
     * represented by this contact object was added to the contacts list by the
     * local user, and false if this contact object only exists because the
     * local user has corresponded with the user represented by this contact
     * object. For example, a user will be added as a contact object with this
     * field equal to false if that user is a member of a workspace that the
     * local user is also a member of.<br/><br/>
     * 
     * When a contact (in the ui sense) is added by the user, they might already
     * have an associated contact object, in which case that object will just
     * have this field set to true. Similarly, when a user deletes a contact,
     * it's contact object is not discarded; this field is simply set to false
     * instead, even if the contact is not a known user by any other means.
     */
    private boolean isUserContact;
    /**
     * True if the local user has verified this contact. This means that the
     * local user has reviewed the hash of this contact's security keys and
     * contacted the actual user represented by this contact to validate that
     * those keys are correct, and then indicated to opengroove that they have
     * done this.
     */
    private boolean isUserVerified;
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
    
}
