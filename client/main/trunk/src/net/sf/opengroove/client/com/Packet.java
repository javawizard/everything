package net.sf.opengroove.client.com;

public class Packet
{
    private String packetId;
    private String command;
    private byte[] contents;
    
    public String getPacketId()
    {
        return packetId;
    }
    
    public String getCommand()
    {
        return command;
    }
    
    public byte[] getContents()
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
    
    public void setContents(byte[] contents)
    {
        this.contents = contents;
    }
}
