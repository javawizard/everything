package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ServerToClient;

@ServerToClient
public class LoginResponse extends Packet
{
    public static enum Status
    {
        /**
         * Indicates that a login was successful.
         */
        Successful,
        /**
         * Indicates that a login failed because of an incorrect username or
         * password.
         */
        BadAuth,
        /**
         * Indicates that a login failed because the computer specified does not
         * exist.
         */
        BadComputer,
        /**
         * Indicates that a login failed because the user is already connected
         * as that computer. This will not occur if the login does not specify a
         * computer.
         */
        AlreadyConnected,
        /**
         * Indicates that a login failed because The userid specified was
         * absolute and the server contained within it is not this server.
         */
        WrongServer,
        /**
         * Indicates that a login failed for some other reason. The reason
         * should be specified in the reason field.
         */
        Other
    }
    
    private Status status;
    private String reason;
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    public String getReason()
    {
        return reason;
    }
    
    public void setReason(String reason)
    {
        this.reason = reason;
    }
    
    public LoginResponse(Status status, String reason)
    {
        super();
        this.status = status;
        this.reason = reason;
    }
}
