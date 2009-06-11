package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;
import org.opengroove.g4.common.protocol.doc.ServerToClient;

/**
 * A packet used to tell the client their real name, or to update the real name
 * of this user on the server. This is sent by the server to the client at
 * connection establishment to tell the client their real name, and then once to
 * the client whenever another of their computers changes their user account's
 * real name. Computers change the account's real name by sending this packet to
 * the server with the new real name. Updating the real name will also cause all
 * users that have this user as a contact to receive a new roster.
 * 
 * @author Alexander Boyd
 * 
 */
@ClientToServer
@ServerToClient
public class RealNamePacket extends Packet
{
    private String name;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
