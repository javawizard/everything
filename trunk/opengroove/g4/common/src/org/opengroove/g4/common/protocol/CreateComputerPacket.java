package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;

@ClientToServer
public class CreateComputerPacket extends Packet
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
    
    public CreateComputerPacket(String name)
    {
        super();
        this.name = name;
    }
}
