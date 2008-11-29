package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Length;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
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
    // replyId - string
    // replySubject - string
    // subject - string
    // contentType - string, typically "html"
    // message - string
    // attachments - stored list
    
    @Property
    public String getId();
    
    public void setId(String id);
    
    @Property
    public boolean isOutbound();
    
    public void setOutbound(boolean outbound);
    
    @Property
    public boolean isDraft();
    
    public void setDraft(boolean draft);
    
    @Property
    public long getDate();
    
    public void setDate(long date);
    
    @Property
    public boolean isRead();
    
    public void setRead(boolean read);
    
    @Property
    @ListType(UserMessageRecipient.class)
    public StoredList<UserMessageRecipient> getRecipients();
    
    @Property
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
    @ListType(UserMessageAttachment.class)
    public StoredList<UserMessageAttachment> getAttachments();
}
