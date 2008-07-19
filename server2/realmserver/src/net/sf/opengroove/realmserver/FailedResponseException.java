package net.sf.opengroove.realmserver;

import net.sf.opengroove.realmserver.OpenGrooveRealmServer.Status;

/**
 * An exception that can be thrown from within a command handler to indicate
 * that the command failed, and that the connection handler should send a
 * response with the status FAIL and the message specified in the exception.
 * 
 * @author Alexander Boyd
 * 
 */
public class FailedResponseException extends
    RuntimeException
{
    
    public FailedResponseException()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public FailedResponseException(String message,
        Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    public FailedResponseException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    private Status status = Status.FAIL;
    
    public Status getStatus()
    {
        return status;
    }
    
    public FailedResponseException(Status status,
        String message)
    {
        super(message);
        this.status = status;
    }
    
    public FailedResponseException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
}
