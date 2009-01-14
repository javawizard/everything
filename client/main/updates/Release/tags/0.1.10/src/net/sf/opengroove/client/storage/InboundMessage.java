package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface InboundMessage
{
    /**
     * Indicates that basic information about this message, mainly it's id and
     * sender, has been downloaded.
     */
    public static final int STAGE_IMPORTED = 1;
    /**
     * Indicates that the encrypted contents of the message have been
     * downloaded.
     */
    public static final int STAGE_DOWNLOADED = 2;
    /**
     * Indicates that the message has been deleted from the server. The message
     * is not decrypted yet.
     */
    public static final int STAGE_LOCALIZED = 3;
    /**
     * Indicates that the message has been decrypted and it's signature
     * verified. Messages with an invalid signature or encryption will never
     * reach this stage; instead, they will be deleted.
     */
    public static final int STAGE_DECRYPTED = 4;
    /**
     * Indicates that the message has been decoded. This means that it's
     * destination info has been extracted into the message's InboundMessage
     * object and the actual message contents separated into another file.
     */
    public static final int STAGE_DECODED = 5;
    /**
     * Indicates that a message listener has been notified of the message's
     * arrival.
     */
    public static final int STAGE_DISPATCHED = 6;
    /**
     * Indicates that the message has been read and is no longer needed.
     * Generally, the target message hierarchy handler will be the one that sets
     * the message to this state. When a message is set to this state, it is
     * eligibele for deletion, and will typically be deleted soon after.
     */
    public static final int STAGE_READ = 7;
    
    @Property
    public String getTarget();
    
    public void setTarget(String target);
    
    @Property
    public String getId();
    
    public void setId(String id);
    
    @Property
    public int getStage();
    
    public void setStage(int stage);
    
    @Property
    public String getSender();
    
    public void setSender(String sender);
    
    @Property
    public String getSendingComputer();
    
    public void setSendingComputer(String computer);
    
    @Property
    @ListType(MessageProperty.class)
    public StoredList<MessageProperty> getProperties();
    
    @Constructor
    public MessageProperty createProperty();
    
    @Search(listProperty = "properties", searchProperty = "name")
    public MessageProperty getProperty(String name);
    
}
