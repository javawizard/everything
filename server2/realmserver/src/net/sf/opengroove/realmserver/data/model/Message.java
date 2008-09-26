package net.sf.opengroove.realmserver.data.model;

public class Message
{
    private String id;
    private String sender;
    private String computer;
    private boolean sent;
    
    public String getId()
    {
        return id;
    }
    
    public String getSender()
    {
        return sender;
    }
    
    public String getComputer()
    {
        return computer;
    }
    
    public boolean isSent()
    {
        return sent;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setSender(String sender)
    {
        this.sender = sender;
    }
    
    public void setComputer(String computer)
    {
        this.computer = computer;
    }
    
    public void setSent(boolean sent)
    {
        this.sent = sent;
    }
}
