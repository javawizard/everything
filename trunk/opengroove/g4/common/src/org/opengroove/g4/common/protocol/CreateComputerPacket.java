package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;

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
