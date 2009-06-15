package org.opengroove.g4.common.messaging;

import java.io.Serializable;

/**
 * A message that can be sent from one client to another to indicate message
 * status. When a client receives a {@link Message}, it sends a MessageStatus to
 * the sender indicating that the message was delivered. When the user opens the
 * essage, it sends a MessageStatus to the sender indicating that the message
 * was opened.
 * 
 * @author Alexander Boyd
 * 
 */
public class MessageStatus implements Serializable
{
    public static enum Status
    {
        Delivered, Opened
    }
    
    /**
     * The status that this message status indicates. If Opened is received
     * before Delivered, the client acts as if Delivered had been received at
     * the same time.
     */
    private Status status;
    /**
     * The id of the message that this pertains to
     */
    private String messageId;
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    public String getMessageId()
    {
        return messageId;
    }
    
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }
}
