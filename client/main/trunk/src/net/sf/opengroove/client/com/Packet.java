package net.sf.opengroove.client.com;

public class Packet
{
    private String packetId;
    private String command;
    private String response;
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
    
    public Packet(String packetId, String command,
        byte[] contents)
    {
        super();
        this.packetId = packetId;
        this.command = command;
        this.contents = contents;
    }
    
    public Packet(String packetId, String command,
        String response, byte[] contents)
    {
        super();
        this.packetId = packetId;
        this.command = command;
        this.response = response;
        this.contents = contents;
    }
    
    public Packet()
    {
        super();
    }
    
    public String getResponse()
    {
        return response;
    }
    
    public void setResponse(String response)
    {
        this.response = response;
    }
    
    public String toString()
    {
        return "(PACKET: "
            + getPacketId()
            + " "
            + getCommand()
            + " "
            + (getResponse() == null ? "" : getResponse()
                + " ")
            + new String(getContents(), 0, Math.min(96,
                getContents().length));
    }
}
