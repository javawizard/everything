package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ServerToClient;

/**
 * A packet that is sent as a response when an exception was thrown while
 * processing the packet that this one is in reply to. This is only sent from
 * the server to the client as any exception thrown by the client should just be
 * displayed to the user.
 * 
 * @author Alexander Boyd
 * 
 */
@ServerToClient
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
