package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;

public class CreateComputerResponse extends Packet
{
    public static enum Status
    {
        Successful, AlreadyExists, Other
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
}
