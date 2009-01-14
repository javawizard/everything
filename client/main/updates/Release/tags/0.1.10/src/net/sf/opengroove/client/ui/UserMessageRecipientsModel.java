package net.sf.opengroove.client.ui;

import javax.swing.AbstractListModel;

import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.UserMessage;

public class UserMessageRecipientsModel extends
    AbstractListModel
{
    private Storage storage;
    private UserMessage message;
    
    public UserMessageRecipientsModel(Storage storage,
        UserMessage message)
    {
        super();
        this.storage = storage;
        this.message = message;
    }
    
    public Object getElementAt(int index)
    {
        return message.getRecipients().get(index)
            .getUserid();
    }
    
    public int getSize()
    {
        return message.getRecipients().size();
    }
    
    public void reload()
    {
        fireContentsChanged(this, 0, getSize());
    }
    
}
