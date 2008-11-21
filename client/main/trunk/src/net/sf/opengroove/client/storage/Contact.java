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
    public class CustomDelegate implements Delegate
    {
        
        @Override
        public Object get(Object on, Class propertyClass,
            String property)
        {
            Contact contact = (Contact) on;
            if (property.equals("displayName"))
            {
                String localName = contact.getLocalName();
                if (localName != null
                    && !localName.equals(""))
                    return localName;
                String realName = contact.getRealName();
                if (realName != null
                    && !realName.equals(""))
                    return realName;
                return contact.getUserid();
            }
            return null;
        }
        
    }
    
    @Property
    public String getUserid();
    
    @Property
    public BigInteger getRsaEncPub();
    
    @Property
    public BigInteger getRsaEncMod();
    
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
    
    public void setRsaEncMod(BigInteger rasEncMod);
    
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
    @CustomProperty(CustomDelegate.class)
    public String getDisplayName();
    
    @Property
    public String getRealName();
    
    public void setRealName(String realName);
    
    /**
     * The date that local information about this contact was last modified, in
     * server time. This is used to synchronize contact information between the
     * user's computers.
     * 
     * @return
     */
    @Property
    public long getLastModified();
    
    public void setLastModifled(long value);
    
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
