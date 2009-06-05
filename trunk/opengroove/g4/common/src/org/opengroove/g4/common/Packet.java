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
    
    /**
     * The thread of the packet. This is a unique string generated by the client
     * for each packet that it sends. If the server sends a packet that is in
     * response to the client's packet, it will have the same thread id so that
     * the client can pair the command and the response together. In the rare
     * case that the server asks the client for info (such as when the server
     * pings the client to figure out latency and see if the client is still
     * there), the client should follow the same protocol and set the response's
     * thread id to be the initiating packet's thread id.
     * 
     * @return
     */
    public String getPacketThread()
    {
        return packetThread;
    }
    
}
