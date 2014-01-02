package net.sf.opengroove.client.ui;

import java.util.Date;

import javax.swing.table.AbstractTableModel;

import org.opengroove.g4.common.messaging.MessageHeader;
import org.opengroove.g4.common.user.Userid;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.messaging.StoredMessageManager;
import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

public class UserMessageTableModel extends AbstractTableModel
{
    public static final int COL_TYPE = 0;
    public static final int COL_DATE = 1;
    public static final int COL_FROM = 2;
    public static final int COL_TO = 3;
    public static final int COL_SUBJECT = 4;
    public static final int COL_IN_REPLY_TO = 5;
    
    private Storage storage;
    
    private LocalUser user;
    
    private Userid userid;
    
    public UserMessageTableModel(Storage storage)
    {
        this.storage = storage;
        this.user = storage.getLocalUser();
        this.userid = storage.getUserid();
    }
    
    public Class<?> getColumnClass(int i)
    {
        switch (i)
        {
            case COL_TYPE:
            case COL_FROM:
            case COL_TO:
            case COL_SUBJECT:
            case COL_IN_REPLY_TO:
                return String.class;
            case COL_DATE:
                return Date.class;
        }
        return null;
    }
    
    public String getColumnName(int column)
    {
        switch (column)
        {
            case COL_TYPE:
                return "Type";
            case COL_FROM:
                return "From";
            case COL_TO:
                return "To";
            case COL_SUBJECT:
                return "Subject";
            case COL_IN_REPLY_TO:
                return "In reply to";
            case COL_DATE:
                return "Date";
        }
        return null;
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }
    
    public int getColumnCount()
    {
        return 6;
    }
    
    public int getRowCount()
    {
        return OpenGroove.getUserContext(userid).getStoredMessageManager()
            .getMessageCount();
    }
    
    public Object getValueAt(int rowIndex, int column)
    {
        StoredMessageManager messageManager =
            OpenGroove.getUserContext(userid).getStoredMessageManager();
        MessageHeader message = null;
        try
        {
            message = messageManager.getMessageHeaderByIndex(rowIndex);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
        if (message == null)
        {
            /*
             * For some reason, we're trying to get the object for a message
             * that doesn't exist. We'll tell the table itself that it needs to
             * reload our data from scratch, and return null.
             */
            fireTableDataChanged();
            return null;
        }
        StoredMessageManager.Type messageType =
            messageManager.getMessageType(message.getMessageId());
        switch (column)
        {
            case COL_TYPE:
                return messageType.toString();
            case COL_DATE:
                return new Date(message.getDate());
            case COL_FROM:
                return message.getSender().toString();
            case COL_TO:
                return StringUtils.delimited(message.getRecipients(),
                    new ToString<Userid>()
                    {
                        
                        public String toString(Userid object)
                        {
                            return object.toString();
                        }
                    }, ",");
            case COL_SUBJECT:
                return message.getSubject();
            case COL_IN_REPLY_TO:
            {
                if (message.getInReplySubject() == null)
                    return "";
                return message.getInReplySubject();
            }
        }
        return null;
    }
    
    /**
     * Returns true if the specified row matches the specified search, false
     * otherwise.
     * 
     * @param row
     *            The index of the row to search
     * @param criteria
     *            The criteria to search for
     * @param extended
     *            True if this is an extended search, false if it is not. See
     *            the tooltip for searchMessageCheckbox in MessageHistoryFrame
     *            for more info (when that checkbox is checked, then searches
     *            are performed with this variable equal to true).
     * @return True if the specified row matches the specified search, false
     *         otherwise.
     */
    public boolean matches(int row, String criteria, boolean extended)
    {
        if (criteria == null || "".equals(criteria.trim()))
            return true;
        criteria = criteria.toLowerCase();
        MessageHeader message;
        try
        {
            message =
                OpenGroove.getUserContext(userid).getStoredMessageManager()
                    .getMessageHeaderByIndex(row);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            return true;
        }
        if (message == null)
            return true;
        String messageSubject = message.getSubject();
        if (messageSubject.toLowerCase().contains(criteria))
            return true;
        if (extended)
        {
            /*
             * In the future we should search the attachment names and recipient
             * names, but for now we're not going to, since it would slow the
             * search down by quite a bit.
             */
            String messageContents = message.getBody().getString();
            if (messageContents.toLowerCase().contains(criteria))
                return true;
            if (message.getInReplySubject() != null)
            {
                if (message.getInReplySubject().toLowerCase().contains(criteria))
                    return true;
            }
        }
        return false;
    }
}
