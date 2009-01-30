package net.sf.opengroove.client.messaging;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.opengroove.client.model.UserComputer;

/**
 * This class represents a stored message, as seen by users of the message
 * hierarchy.
 * 
 * @author Alexander Boyd
 * 
 */
public class Message
{
    private ArrayList<UserComputer> recipients = new ArrayList<UserComputer>();
    private UserComputer sender;
    private File contents;
    private HashMap<String, String> properties = new HashMap<String, String>();
    
    public ArrayList<UserComputer> getRecipients()
    {
        return recipients;
    }
    
    public UserComputer getSender()
    {
        return sender;
    }
    
    public File getContents()
    {
        return contents;
    }
    
    public HashMap<String, String> getProperties()
    {
        return properties;
    }
    
    public void setRecipients(ArrayList<UserComputer> recipients)
    {
        this.recipients = recipients;
    }
    
    public void setSender(UserComputer sender)
    {
        this.sender = sender;
    }
    
    public void setContents(File contents)
    {
        this.contents = contents;
    }
    
    public void setProperties(HashMap<String, String> properties)
    {
        this.properties = properties;
    }
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contents == null) ? 0 : contents.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((recipients == null) ? 0 : recipients.hashCode());
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
        return result;
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Message other = (Message) obj;
        if (contents == null)
        {
            if (other.contents != null)
                return false;
        }
        else if (!contents.equals(other.contents))
            return false;
        if (properties == null)
        {
            if (other.properties != null)
                return false;
        }
        else if (!properties.equals(other.properties))
            return false;
        if (recipients == null)
        {
            if (other.recipients != null)
                return false;
        }
        else if (!recipients.equals(other.recipients))
            return false;
        if (sender == null)
        {
            if (other.sender != null)
                return false;
        }
        else if (!sender.equals(other.sender))
            return false;
        return true;
    }
}
