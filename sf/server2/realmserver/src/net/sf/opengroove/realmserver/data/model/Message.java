package net.sf.opengroove.realmserver.data.model;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class Message
{
    private String id;
    private String sender;
    private String computer;
    private boolean sent;
    
    public Message(String id, String sender,
        String computer, boolean sent)
    {
        super();
        this.id = id;
        this.sender = sender;
        this.computer = computer;
        this.sent = sent;
    }
    
    public Message()
    {
        super();
    }
    
    /**
     * The id of the message. This should start with the user's userid, followed
     * by a hyphen, and should not contain anything else other than letters,
     * numbers, a : in the user's userid, and hyphens.
     * 
     * @return
     */
    
    public String getId()
    {
        return id;
    }
    
    /**
     * The userid of the message's sender.<br/><br/>
     * 
     * Recipients are stored as {@link MessageRecipient}}s.
     * 
     * @return
     */
    public String getSender()
    {
        return sender;
    }
    
    /**
     * The name of the sender's computer that sent the message.
     * 
     * @return
     */
    public String getComputer()
    {
        return computer;
    }
    
    /**
     * True if the message is marked as having been sent. When a message is
     * created, it can be added to and edited by the sender, until the sender
     * sends it. When the sender sends it, it sets this to true, at which point
     * the message is available to it's recipients. Then, if a recipient is from
     * another realm, the message is forwarded to that realm's server. While it
     * is being sent, isSent() is false on the receiving realm server, and is
     * set to true once the message has been fully transferred.
     * 
     * @return
     */
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
    
    /**
     * Returns this message's id, but with : replaced by $.
     * 
     * @return
     */
    public static String getFileId(String messageId)
    {
        return URLEncoder.encode(messageId);
    }
    
    /**
     * Returns this message's id, but with : replaced by $.
     * 
     * @return
     */
    public static String getMessageId(String fileId)
    {
        return URLDecoder.decode(fileId);
    }
    
    public String getFileId()
    {
        return getFileId(id);
    }
}
