package org.opengroove.g4.common.messaging;

import java.io.Serializable;

import org.opengroove.g4.common.data.DataBlock;
import org.opengroove.g4.common.user.Userid;

/**
 * The portion of a message that can generally be loaded into memory without
 * having a negative impact on performance. This currently contains the
 * message's body, sender, recipient list, subject, and in-reply-to message id
 * and subject. This does not contain the message's attachments.
 * 
 * @author Alexander Boyd
 * 
 */
public class MessageHeader implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -2941643641614952607L;
    private String messageId;
    private long date;
    private Userid sender;
    private Userid[] recipients;
    private String subject;
    private String inReplyMessageId;
    private String inReplySubject;
    private String contentType = "text/html";
    private DataBlock body;
    
    public String getMessageId()
    {
        return messageId;
    }
    
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }
    
    public Userid getSender()
    {
        return sender;
    }
    
    public void setSender(Userid sender)
    {
        this.sender = sender;
    }
    
    public Userid[] getRecipients()
    {
        return recipients;
    }
    
    public void setRecipients(Userid[] recipients)
    {
        this.recipients = recipients;
    }
    
    public String getSubject()
    {
        return subject;
    }
    
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    public String getInReplyMessageId()
    {
        return inReplyMessageId;
    }
    
    public void setInReplyMessageId(String inReplyMessageId)
    {
        this.inReplyMessageId = inReplyMessageId;
    }
    
    public String getInReplySubject()
    {
        return inReplySubject;
    }
    
    public void setInReplySubject(String inReplySubject)
    {
        this.inReplySubject = inReplySubject;
    }
    
    public DataBlock getBody()
    {
        return body;
    }
    
    public void setBody(DataBlock body)
    {
        this.body = body;
    }
    
    public long getDate()
    {
        return date;
    }
    
    public void setDate(long date)
    {
        this.date = date;
    }
    
    /**
     * The content type of the message's body. Most clients use text/html for
     * this, and the server typically uses text/plain for notifications.
     * 
     * @return
     */
    public String getContentType()
    {
        return contentType;
    }
    
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
}
