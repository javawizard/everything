package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.user.Userid;

public class OutboundMessagePacket extends Packet
{
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

    public Object getMessage()
    {
        return message;
    }

    public void setMessage(Object message)
    {
        this.message = message;
    }
}
