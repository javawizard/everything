package net.sf.opengroove.client.storage;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.sf.opengroove.common.proxystorage.*;
import net.sf.opengroove.common.utils.Userids;
import net.sf.opengroove.common.vcard.VCard;

@ProxyBean
public interface Contact
{
    
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
    
    public String getUserid();
    
    public BigInteger getRsaEncPub();
    
    public BigInteger getRasEncMod();
    
    public BigInteger getRsaSigPub();
    
    public BigInteger getRsaSigMod();
    
    public boolean isUserContact();
    
    public boolean isUserVerified();
    
    public void setUserid(String userid);
    
    public void setRsaEncPub(BigInteger rsaEncPub);
    
    public void setRasEncMod(BigInteger rasEncMod);
    
    public void setRsaSigPub(BigInteger rsaSigPub);
    
    public void setRsaSigMod(BigInteger rsaSigMod);
    
    public void setUserContact(boolean isUserContact);
    
    public void setUserVerified(boolean isUserVerified);
    
    public boolean isHasKeys();
    
    public ArrayList<ContactComputer> getComputers();
    
    public void setHasKeys(boolean hasKeys);
    
    public String getLocalName();
    
    public void setLocalName(String localName);
    
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
    
    public ContactStatus getStatus()
    {
        if (status == null)
        {
            status = new ContactStatus();
        }
        return status;
    }
    
    public void setStatus(ContactStatus status)
    {
        this.status = status;
    }
    
}
