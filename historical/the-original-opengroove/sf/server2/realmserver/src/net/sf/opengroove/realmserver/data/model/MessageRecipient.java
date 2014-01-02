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
    
    /**
     * The id of the message that this recipient is for
     * 
     * @return
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * The userid (not username) of the message recipient. If this userid is not
     * of this server's realm, then the server will forward the message to the
     * appropriate realm and remove this recipient. If this userid is of this
     * realm, then it means that either the message was created by a user on
     * this realm, or the message was sent by another realm and forwarded to
     * this realm in the aforementioned manner.
     * 
     * @return
     */
    public String getRecipient()
    {
        return recipient;
    }
    
    /**
     * The name of the computer that this message is targeted to. If a message
     * is to be sent to multiple of a particular recipient's computers, then
     * there will be one message recipient for each recipient computer.
     * 
     * @return
     */
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
