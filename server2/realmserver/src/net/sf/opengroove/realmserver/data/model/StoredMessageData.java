package net.sf.opengroove.realmserver.data.model;

public class StoredMessageData
{
    private String id;
    private int block;
    private boolean read;
    private byte[] contents;
    public String getId()
    {
        return id;
    }
    public int getBlock()
    {
        return block;
    }
    public byte[] getContents()
    {
        return contents;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public void setBlock(int block)
    {
        this.block = block;
    }
    public void setContents(byte[] contents)
    {
        this.contents = contents;
    }
    public boolean isRead()
    {
        return read;
    }
    public void setRead(boolean read)
    {
        this.read = read;
    }
}
