package net.sf.opengroove.client.ui;

import javax.swing.AbstractListModel;

import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.UserMessage;

public class UserMessageAttachmentsModel extends
    AbstractListModel
{
    private Storage storage;
    private UserMessage message;
    
    public UserMessageAttachmentsModel(Storage storage,
        UserMessage message)
    {
        super();
        this.storage = storage;
        this.message = message;
    }
    
    public Object getElementAt(int index)
    {
        try
        {
            return message
                .getAttachmentsByEmbeddedStatus(false)[index]
                .getName();
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }
    
    public int getSize()
    {
        return message
            .getAttachmentsByEmbeddedStatus(false).length;
    }
    
    public Storage getStorage()
    {
        return storage;
    }
    
    public UserMessage getMessage()
    {
        return message;
    }
    
    public void setStorage(Storage storage)
    {
        this.storage = storage;
    }
    
    public void setMessage(UserMessage message)
    {
        this.message = message;
    }
    
    public void reload()
    {
        fireContentsChanged(this, 0, getSize());
    }
    
}
