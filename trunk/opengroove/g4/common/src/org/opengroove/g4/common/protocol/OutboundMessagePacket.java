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
     * The message's payload. This is the actual data that is delivered to the
     * recipients. Since this is deserialized on the server before being sent to
     * the recipients, it should be wrapped in a PassThroughObject if it
     * contains objects of a class not present on the server, to avoid the
     * server throwing a ClassNotFoundException on deserialization.
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
