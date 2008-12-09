package net.sf.opengroove.client.storage;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Properties;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.UserContext;
import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.settings.SettingStore;
import net.sf.opengroove.common.proxystorage.*;
import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.utils.Userids;
import net.sf.opengroove.common.vcard.VCard;

@ProxyBean
public interface LocalUser extends ProxyObject
{
    @Property
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
    
    @CustomProperty(CustomDelegate.class)
    public String getDisplayName();
    
    @Property
    @ListType(Contact.class)
    public StoredList<Contact> getContacts();
    
    @Search(listProperty = "contacts", searchProperty = "userid")
    @Filter(parameterFilter = UsernameToUseridFilter.class)
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
    
    @CustomProperty(CustomDelegate.class)
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
    
    @Property
    @ListType(ConfigProperty.class)
    public StoredList<ConfigProperty> getProperties();
    
    @Constructor
    public ConfigProperty createProperty();
    
    /**
     * The date that local information about this user was last modified. This
     * is used for server synchronization purposes.
     * 
     * @return
     */
    @Property
    public long getLastModified();
    
    public void setLastModified(long lastModified);
    
    @Search(listProperty = "properties", searchProperty = "name")
    public ConfigProperty getProperty(String name);
    
    /**
     * A list of all of the certificates that this user has chosen to trust.
     * This does not include the OpenGroove CA Certificate. Each string in this
     * list is the PEM encoded form of the certificate. This can be parsed into
     * an {@link X509Certificate} object by calling
     * {@link CertificateUtils#readCert(String)} and passing in a string in this
     * list.
     * 
     * @return A list that stores all of the end certificates (IE not CA
     *         certificates) that this user has chosen to trust
     */
    @Property
    @ListType(TrustedCertificate.class)
    public StoredList<TrustedCertificate> getTrustedCertificates();
    
    @Property
    @ListType(InboundMessage.class)
    public StoredList<InboundMessage> getInboundMessages();
    
    @Constructor
    public OutboundMessage createOutboundMessage();
    
    @Constructor
    public InboundMessage createInboundMessage();
    
    @Property
    @ListType(OutboundMessage.class)
    public StoredList<OutboundMessage> getOutboundMessages();
    
    @Search(listProperty = "outboundMessages", searchProperty = "stage")
    public OutboundMessage[] listOutboundMessagesForStage(
        int stage);
    
    @Search(listProperty = "inboundMessages", searchProperty = "stage")
    public InboundMessage[] listInboundMessagesForStage(
        int stage);
    
    @Search(listProperty = "inboundMessages", searchProperty = "id")
    public InboundMessage getInboundMessageById(String id);
    
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
            else if (property.equals("serverTime"))
                return System.currentTimeMillis()
                    - user.getLag();
            else if (property.equals("displayName"))
            {
                String realName = user.getRealName();
                if (realName == null
                    || realName.trim().equals(""))
                    return user.getUserid();
                return realName;
            }
            return null;
        }
        
    }
    
    @Property
    public SettingStore getSettingStore();
    
    public void setSettingStore(SettingStore store);
    
    @Constructor
    public SettingStore createSettingStore();
    
    @Search(listProperty = "inboundMessages", searchProperty = "target", exact = false, anywhere = false)
    public InboundMessage[] getInboundMessagesByFloatingTarget(
        String floatingPath);
    
    @Search(listProperty = "inboundMessages", searchProperty = "target", exact = true, anywhere = false)
    public InboundMessage[] getInboundMessagesByFixedTarget(
        String fixedPath);
    
    @Property
    @ListType(UserMessage.class)
    public StoredList<UserMessage> getUserMessages();
    
    @Constructor
    public UserMessage createUserMessage();
    
    @Search(listProperty = "userMessages", searchProperty = "id")
    public UserMessage getUserMessageById(String decode);
    
    @Search(listProperty = "outboundMessages", searchProperty = "id")
    public OutboundMessage getOutboundMessageById(
        String decode);
}
