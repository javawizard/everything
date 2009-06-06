package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;

/**
 * A packet that is sent as a response when an exception was thrown while
 * processing the packet that this one is in reply to.
 * 
 * @author Alexander Boyd
 * 
 */
public class ExceptionPacket extends Packet
{
    private Exception exception;
    
    public Exception getException()
    {
        return exception;
    }
    
    public void setException(Exception exception)
    {
        this.exception = exception;
    }
}
