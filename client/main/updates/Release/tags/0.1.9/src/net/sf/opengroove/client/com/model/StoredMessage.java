package net.sf.opengroove.client.com.model;

public class StoredMessage
{
    private String messageId;
    private String sender;
    private String computer;
    private boolean sent;
    
    public boolean isSent()
    {
        return sent;
    }

    public void setSent(boolean sent)
    {
        this.sent = sent;
    }

    public String getMessageId()
    {
        return messageId;
    }
    
    public String getSender()
    {
        return sender;
    }
    
    public String getComputer()
    {
        return computer;
    }
    
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }
    
    public void setSender(String sender)
    {
        this.sender = sender;
    }
    
    public void setComputer(String computer)
    {
        this.computer = computer;
    }
}
