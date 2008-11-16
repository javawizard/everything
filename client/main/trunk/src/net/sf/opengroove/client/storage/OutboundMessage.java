package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface OutboundMessage
{
    /**
     * Indicates that the message has been initialized, and it's plaintext data
     * written and metadata stored on this object.
     */
    public static final int STAGE_INITIALIZED = 1;
    /**
     * Indicates that the message has been encoded. This means that the
     * message's metadata has been encoded into the message's data file itself.
     */
    public static final int STAGE_ENCODED = 2;
    /**
     * Indicates that the message has been encrypted and signed. It is now ready
     * for upload.
     */
    public static final int STAGE_ENCRYPTED = 3;
    /**
     * Indicates that the message has been uploaded to the server.
     */
    public static final int STAGE_UPLOADED = 4;
    /**
     * Indicates that the message has been successfully sent. The message is now
     * eligible for deletion.
     */
    public static final int STAGE_SENT = 5;
    
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
    @ListType(OutboundMessageRecipient.class)
    public StoredList<OutboundMessageRecipient> getRecipients();
    
    @Property
    @ListType(MessageProperty.class)
    public StoredList<MessageProperty> getProperties();
    
    @Search(listProperty = "properties", searchProperty = "name")
    public MessageProperty getProperty(String name);
}
