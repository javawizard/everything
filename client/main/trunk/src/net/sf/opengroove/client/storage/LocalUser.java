package net.sf.opengroove.client.storage;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Properties;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.UserContext;
import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.common.proxystorage.*;
import net.sf.opengroove.common.utils.Userids;
import net.sf.opengroove.common.vcard.VCard;

@ProxyBean
public interface LocalUser
{
    
    public String getUserid();
    
    /**
     * Returns true if the user is logged in on this computer.
     * 
     * @return
     */
    @CustomProperty(CustomDelegate.class)
    public boolean isLoggedIn();
    
    /**
     * Returns the user context for this user, if they are logged in. If not,
     * null is returned. This method is a shortcut for
     * <code>OpenGroove.userContextMap.get(getUserid())</code>.
     * 
     * @return
     */
    @CustomProperty(CustomDelegate.class)
    public UserContext getContext();
    
    @Property
    @ListType(Contact.class)
    public StoredList<Contact> getContacts();
    
    @Search(listProperty = "contacts", searchProperty = "userid")
    public Contact getContact(String userid);
    
    @Constructor
    public Contact createContact();
    
    @Property
    public String getEncPassword();
    
    public void setUserid(String userid);
    
    public void setEncPassword(String encPassword);
    
    @Property
    public BigInteger getRsaEncPub();
    
    @Property
    public BigInteger getRsaEncPrv();
    
    @Property
    public BigInteger getRasEncMod();
    
    @Property
    public BigInteger getRsaSigPub();
    
    @Property
    public BigInteger getRsaSigPrv();
    
    @Property
    public BigInteger getRsaSigMod();
    
    @Property
    public boolean isAutoSignOn();
    
    public void setRsaEncPub(BigInteger rsaEncPub);
    
    public void setRsaEncPrv(BigInteger rsaEncPrv);
    
    public void setRasEncMod(BigInteger rasEncMod);
    
    public void setRsaSigPub(BigInteger rsaSigPub);
    
    public void setRsaSigPrv(BigInteger rsaSigPrv);
    
    public void setRsaSigMod(BigInteger rsaSigMod);
    
    public void setAutoSignOn(boolean autoSignOn);
    
    @Property
    public long getLag();
    
    @Property
    public long getServerTime();
    
    public void setLag(long lag);
    
    @Property
    public String getComputer();
    
    @Property
    public String getStoredPassword();
    
    public void setComputer(String computer);
    
    public void setStoredPassword(String storedPassword);
    
    @Property
    public String getPasswordHint();
    
    public void setPasswordHint(String passwordHint);
    
    @Property
    public BigInteger getServerRsaPub();
    
    public void setServerRsaPub(BigInteger serverRsaPub);
    
    @Property
    public BigInteger getServerRsaMod();
    
    public void setServerRsaMod(BigInteger serverRsaMod);
    
    @Property
    public boolean isSearchVisible();
    
    @Property
    public boolean isLocalVisible();
    
    @Property
    public String getEmailAddress();
    
    public void setSearchVisible(boolean isSearchVisible);
    
    public void setLocalVisible(boolean isLocalVisible);
    
    public void setEmailAddress(String emailAddress);
    
    @Property
    public String getRealName();
    
    public void setRealName(String realName);
    
    public void setPublicEmailAddress(String addr);
    
    @Property
    public String getPublicEmailAddress();
    
    public static class CustomDelegate implements Delegate
    {
        
        @Override
        public Object get(Object on, Class propertyClass,
            String property)
        {
            LocalUser user = (LocalUser) on;
            if (property.equals("context"))
                return OpenGroove.userContextMap.get(user
                    .getUserid());
            else if (property.equals("loggedIn"))
                return OpenGroove.userContextMap.get(user
                    .getUserid()) != null;
            return null;
        }
        
    }
}
