package net.sf.opengroove.realmserver.data.model;

public class MessageRecipient
{
    private String id;
    private String recipient;
    private String computer;
    
    public MessageRecipient()
    {
        super();
    }
    
    public MessageRecipient(String id, String recipient,
        String computer)
    {
        super();
        this.id = id;
        this.recipient = recipient;
        this.computer = computer;
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getRecipient()
    {
        return recipient;
    }
    
    public String getComputer()
    {
        return computer;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }
    
    public void setComputer(String computer)
    {
        this.computer = computer;
    }
}
