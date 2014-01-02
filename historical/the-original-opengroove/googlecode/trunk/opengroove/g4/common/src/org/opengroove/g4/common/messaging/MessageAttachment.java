package org.opengroove.g4.common.messaging;

import java.io.Serializable;

import org.opengroove.g4.common.data.DataBlock;

public class MessageAttachment implements Serializable
{
    private String name;
    
    public static enum Type
    {
        FILE, FOLDER
    }
    
    private Type type;
    /**
     * True if this is an embedded attachment, false if it is a normal
     * attachment. Embedded attachments are those that are included by nature of
     * the fact that they appear in the body. An image pasted in to the message,
     * for example, would be transferred as an embedded attachment. Voice
     * recordings are stored as embedded attachments too.
     */
    private boolean embedded;
    private DataBlock data;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Type getType()
    {
        return type;
    }
    
    public void setType(Type type)
    {
        this.type = type;
    }
    
    public DataBlock getData()
    {
        return data;
    }
    
    public void setData(DataBlock data)
    {
        this.data = data;
    }
    
    public boolean isEmbedded()
    {
        return embedded;
    }
    
    public void setEmbedded(boolean embedded)
    {
        this.embedded = embedded;
    }
}
