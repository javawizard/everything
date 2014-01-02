package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ServerToClient;
import org.opengroove.g4.common.user.Userid;

@ServerToClient
public class InboundMessagePacket extends Packet
{
    private String messageId;
    private Userid sender;
    private Object message;
    
    public Userid getSender()
    {
        return sender;
    }
    
    public void setSender(Userid sender)
    {
        this.sender = sender;
    }
    
    public Object getMessage()
    {
        return message;
    }
    
    public void setMessage(Object message)
    {
        this.message = message;
    }
    
    /**
     * The id of this message.
     * 
     * @return
     */
    public String getMessageId()
    {
        return messageId;
    }
    
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }
}
