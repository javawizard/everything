package net.sf.opengroove.client;

import java.io.Serializable;

/**
 * This class stores information about a contact's computer.
 * 
 * @author Alexander Boyd
 * 
 */
public class ContactComputer implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5092427115370001261L;
    /**
     * The contact's computer name.
     */
    private String name;
    /**
     * The type of computer that the contact is
     */
    private String type;
    /**
     * The server's lag relative to the contact's time.
     */
    private long lag;
    private ContactStatus status;
    
    public long getLag()
    {
        return lag;
    }
    
    public void setLag(long lag)
    {
        this.lag = lag;
    }
    
    public String getName()
    {
        return name;
    }
    
    public ContactStatus getStatus()
    {
        if (status == null)
            status = new ContactStatus();
        return status;
    }
    
    public void setStatus(ContactStatus status)
    {
        this.status = status;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}
