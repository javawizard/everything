package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.user.Userid;

public class InboundMessagePacket extends Packet
{
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
}
