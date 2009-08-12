package jw.bznetwork.client.data.model;

public class ActionRequest
{
    public ActionRequest(int offset, int length, int literalEvent,
            String literalProvider, String literalUser)
    {
        super();
        this.offset = offset;
        this.length = length;
        this.literalEvent = literalEvent;
        this.literalProvider = literalProvider;
        this.literalUser = literalUser;
    }
    
    private int offset;
    private int length;
    private int literalEvent;
    private String literalProvider;
    private String literalUser;
    
    public int getOffset()
    {
        return offset;
    }
    
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    
    public int getLength()
    {
        return length;
    }
    
    public void setLength(int length)
    {
        this.length = length;
    }
    
    public int getLiteralEvent()
    {
        return literalEvent;
    }
    
    public void setLiteralEvent(int literalEvent)
    {
        this.literalEvent = literalEvent;
    }
    
}
