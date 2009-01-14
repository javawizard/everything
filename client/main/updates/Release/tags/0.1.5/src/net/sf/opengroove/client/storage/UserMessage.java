package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.Default;
import net.sf.opengroove.common.proxystorage.Length;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface UserMessage
{
    
    // properties:
    //      
    // outbound - boolean
    // date - long
    // read - boolean
    // recipients - stored list
    // sender - string
    // replyId - string
    // replySubject - string
    // subject - string
    // contentType - string, typically "html"
    // message - string
    // attachments - stored list
    /**
     * The message's id. This is generated when the message is created. This has
     * nothing to do with the id of the stored message that this message
     * was/will be/is packaged in.
     */
    @Property
    public String getId();
    
    public void setId(String id);
    
    /**
     * The message's stored id. This is set to the stored message id of the
     * stored message used to send this message, when the message is about to be
     * sent, and similarly when receiving the message.
     * 
     * @return
     */
    @Property
    public String getStoredMessageId();
    
    public void setStoredMessageId(String id);
    
    /**
     * True if this is an outbound message, false if this is an inbound message.
     * 
     * @return
     */
    @Property
    public boolean isOutbound();
    
    public void setOutbound(boolean outbound);
    
    /**
     * For outbound messages, true if this message is a draft, false if it is
     * not. A draft outbound message is one that has not actually been sent yet,
     * typically because the user wishes to keep editing it. UserMessage objects
     * created because the user is about to compose a message will always be
     * marked as a draft until they are sent.
     * 
     * @return
     */
    @Property
    public boolean isDraft();
    
    public void setDraft(boolean draft);
    
    /**
     * The date that the message was sent on.
     * 
     * @return
     */
    @Property
    public long getDate();
    
    public void setDate(long date);
    
    /**
     * For inbound messages, true if this message has been read yet, false if it
     * has not. Messages with this set to true will have a component shown in
     * the taskbar notification frame.
     * 
     * @return
     */
    @Property
    public boolean isRead();
    
    public void setRead(boolean read);
    
    /**
     * The list of recipients for this message.
     * 
     * @return
     */
    @Property
    @ListType(UserMessageRecipient.class)
    public StoredList<UserMessageRecipient> getRecipients();
    
    /**
     * If this message is in reply to another message, then this is that
     * message's id. Otherwise, this is the empty string.
     * 
     * @return
     */
    @Property
    @Default
    public String getReplyId();
    
    public void setReplyId(String replyId);
    
    @Property
    @Length(2048)
    public String getReplySubject();
    
    public void setReplySubject(String replySubject);
    
    @Property
    @Length(2048)
    public String getSubject();
    
    public void setSubject(String subject);
    
    @Property
    public String getContentType();
    
    public void setContentType(String contentType);
    
    /*
     * The message can only be 32KB according to this declaration. We might want
     * to increase this in the future, but it would probably be pointless, since
     * their VM would probably start spitting out outofmemoryerrors when we try
     * to render the message anyway.
     */
    @Property
    @Length(1024 * 32)
    public String getMessage();
    
    public void setMessage(String message);
    
    @Property
    public String getSender();
    
    public void setSender(String sender);
    
    @Property
    @ListType(UserMessageAttachment.class)
    public StoredList<UserMessageAttachment> getAttachments();
    
    @Constructor
    public UserMessageRecipient createRecipient();
    
    @Constructor
    public UserMessageAttachment createAttachment();
    
    @Search(listProperty = "attachments", searchProperty = "name")
    public UserMessageAttachment getAttachmentByName(
        String name);
    
    @Search(listProperty = "attachments", searchProperty = "embedded")
    public UserMessageAttachment[] getAttachmentsByEmbeddedStatus(
        boolean status);
    
    @Search(listProperty = "recipients", searchProperty = "userid")
    public Object getRecipientById(String selectedValue);
}
