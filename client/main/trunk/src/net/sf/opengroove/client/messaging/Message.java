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
}
