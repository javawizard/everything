package net.sf.opengroove.client.ui;

import javax.swing.AbstractListModel;

import net.sf.opengroove.client.storage.Storage;

import org.opengroove.g4.common.messaging.Message;

public class UserMessageAttachmentsModel extends AbstractListModel
{
    private Storage storage;
    private Message message;
    
    public UserMessageAttachmentsModel(Storage storage, Message message)
    {
        super();
        this.storage = storage;
        this.message = message;
    }
    
    public Object getElementAt(int index)
    {
        try
        {
            return message.getAttachments()[index].getName();
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }
    
    public int getSize()
    {
        return message.getAttachments().length;
    }
    
    public Storage getStorage()
    {
        return storage;
    }
    
    public Message getMessage()
    {
        return message;
    }
    
    public void setStorage(Storage storage)
    {
        this.storage = storage;
    }
    
    public void setMessage(Message message)
    {
        this.message = message;
    }
    
    public void reload()
    {
        fireContentsChanged(this, 0, getSize());
    }
    
}
