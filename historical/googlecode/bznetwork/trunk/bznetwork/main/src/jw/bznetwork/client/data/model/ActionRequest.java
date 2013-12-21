package jw.bznetwork.client.data.model;

public class ActionRequest
{
    public String getLiteralProvider()
    {
        return literalProvider;
    }
    
    public void setLiteralProvider(String literalProvider)
    {
        this.literalProvider = literalProvider;
    }
    
    public String getLiteralUser()
    {
        return literalUser;
    }
    
    public void setLiteralUser(String literalUser)
    {
        this.literalUser = literalUser;
    }
    
    public ActionRequest(int offset, int length, String literalEvent,
            String literalProvider, String literalUser)
    {
        super();
        this.offset = offset;
        this.length = length;
        this.literalEvent = literalEvent;
        this.literalProvider = literalProvider;
        this.literalUser = literalUser;
    }
    
    public ActionRequest()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private int offset;
    private int length;
    private String literalEvent;
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
    
    public String getLiteralEvent()
    {
        return literalEvent;
    }
    
    public void setLiteralEvent(String literalEvent)
    {
        this.literalEvent = literalEvent;
    }
    
}
