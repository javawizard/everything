package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;
import org.opengroove.g4.common.user.Userid;
@ClientToServer
public class OutboundMessagePacket extends Packet
{
    private String messageId;
    private Userid[] recipients;
    private Object message;
    
    public Userid[] getRecipients()
    {
        return recipients;
    }
    
    public void setRecipients(Userid[] recipients)
    {
        this.recipients = recipients;
    }
    
    /**
     * The id for this message. This must always start with the user's userid
     * (containing exactly the server and the username), followed by a dollar
     * sign. For example, opengroove.org::javawizard$27483728473 would be a
     * valid message id.
     * 
     * @return
     */
    public Object getMessage()
    {
        return message;
    }
    
    public void setMessage(Object message)
    {
        this.message = message;
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
