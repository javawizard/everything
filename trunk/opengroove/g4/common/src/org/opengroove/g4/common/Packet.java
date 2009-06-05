package org.opengroove.g4.common;

import java.io.Serializable;

/**
 * A packet that can be sent from the client to the server or vice versa. In the
 * future, packets will also be able to be sent from one server to another.
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class Packet implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1890623610831616381L;
    
    private String packetThread;
    
    public void setPacketThread(String packetThread)
    {
        this.packetThread = packetThread;
    }
    
    public String getPacketThread()
    {
        return packetThread;
    }
    
}
