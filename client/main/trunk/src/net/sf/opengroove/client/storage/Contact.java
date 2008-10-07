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
    @Property
    public String getUserid();
    
    @Property
    public BigInteger getRsaEncPub();
    
    @Property
    public BigInteger getRasEncMod();
    
    @Property
    public BigInteger getRsaSigPub();
    
    @Property
    public BigInteger getRsaSigMod();
    
    @Property
    public boolean isUserContact();
    
    @Property
    public boolean isUserVerified();
    
    public void setUserid(String userid);
    
    public void setRsaEncPub(BigInteger rsaEncPub);
    
    public void setRasEncMod(BigInteger rasEncMod);
    
    public void setRsaSigPub(BigInteger rsaSigPub);
    
    public void setRsaSigMod(BigInteger rsaSigMod);
    
    public void setUserContact(boolean isUserContact);
    
    public void setUserVerified(boolean isUserVerified);
    
    @Property
    public boolean isHasKeys();
    
    public void setHasKeys(boolean hasKeys);
    
    @Property
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
    @Property
    public String getDisplayName();
    
    @Property
    public String getRealName();
    
    public void setRealName(String realName);
    
    @Property
    @Required
    public ContactStatus getStatus();
    
    @Property
    @ListType(ContactComputer.class)
    public StoredList<ContactComputer> getComputers();
    
    @Search(listProperty = "computers", searchProperty = "name")
    public ContactComputer getComputer(String name);
    
    @Constructor
    public ContactComputer createComputer();
}
