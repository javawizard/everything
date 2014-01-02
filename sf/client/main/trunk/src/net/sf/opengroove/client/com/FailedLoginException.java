package net.sf.opengroove.client.com;

import org.opengroove.g4.common.protocol.LoginResponse;

/**
 * Thrown from {@link Communicator#start()} for a one-time communicator when
 * authentication with the server failed.
 * 
 * @author Alexander Boyd
 * 
 */
public class FailedLoginException extends RuntimeException
{
    private LoginResponse packet;
    
    public FailedLoginException(LoginResponse packet)
    {
        this.packet = packet;
    }
    
    /**
     * The response received from the server indicating that authenticating with
     * the server failed.
     * 
     * @return
     */
    public LoginResponse getPacket()
    {
        return packet;
    }
    
}
