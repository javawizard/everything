package net.sf.opengroove.client.ui;

import javax.swing.AbstractListModel;

import org.opengroove.g4.common.messaging.MessageHeader;

import net.sf.opengroove.client.storage.Storage;

public class UserMessageRecipientsModel extends AbstractListModel
{
    private Storage storage;
    private MessageHeader message;
    
    public UserMessageRecipientsModel(Storage storage, MessageHeader message)
    {
        super();
        this.storage = storage;
        this.message = message;
    }
    
    public Object getElementAt(int index)
    {
        return message.getRecipients()[index];
    }
    
    public int getSize()
    {
        return message.getRecipients().length;
    }
    
    public void reload()
    {
        fireContentsChanged(this, 0, getSize());
    }
    
}
