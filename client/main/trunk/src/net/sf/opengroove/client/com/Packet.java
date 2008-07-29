package net.sf.opengroove.client.com;

import java.io.InputStream;

public class Packet
{
    private String packetId;
    private String command;
    private InputStream contents;
    
    public String getPacketId()
    {
        return packetId;
    }
    
    public String getCommand()
    {
        return command;
    }
    
    public InputStream getContents()
    {
        return contents;
    }
    
    public void setPacketId(String packetId)
    {
        this.packetId = packetId;
    }
    
    public void setCommand(String command)
    {
        this.command = command;
    }
    
    public void setContents(InputStream contents)
    {
        this.contents = contents;
    }
}
