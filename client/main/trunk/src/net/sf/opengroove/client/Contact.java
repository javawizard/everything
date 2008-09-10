package net.sf.opengroove.client;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

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
     * 
     * Verification is computer-specific. In otherwords, if the local user has
     * two computers, and verifies a particular user on one computer, that won't
     * cause that user to show up as verified on any of the user's other
     * computers.
     */
    private boolean isUserVerified;
    /**
     * The date of the last time that this contact was modified. This is used
     * when connecting to the server, to see if the contact needs to be updated
     * on the server. Each contact has a date stored on the server that
     * represents the last time the information for that contact was updated by
     * the local user. When a computer connects, it checks to see if this is
     * greater than the corresponding value on the server. If it is, it uploads
     * the details of this contact to the server, and updates it's lastModified
     * date. If this one is less than the one on the server, the contact details
     * from the server are downloaded to the local computer
     * 
     * In the future, this will be split out into several fields, or perhaps a
     * Properties object, for each item within the contact, so that changing two
     * entirely independant settings of a contact won't overwrite each other.
     */
    private long lastModified;
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
