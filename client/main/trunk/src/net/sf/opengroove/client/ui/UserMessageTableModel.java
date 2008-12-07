package net.sf.opengroove.client.ui;

import java.util.Date;

import javax.swing.table.AbstractTableModel;

import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.UserMessage;
import net.sf.opengroove.client.storage.UserMessageAttachment;
import net.sf.opengroove.client.storage.UserMessageRecipient;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

public class UserMessageTableModel extends
    AbstractTableModel
{
    public static final int COL_TYPE = 0;
    public static final int COL_ATTACHMENTS = 1;
    public static final int COL_SIZE = 2;
    public static final int COL_DATE = 3;
    public static final int COL_FROM = 4;
    public static final int COL_TO = 5;
    public static final int COL_SUBJECT = 6;
    public static final int COL_IN_REPLY_TO = 7;
    
    private Storage storage;
    
    private LocalUser user;
    
    public UserMessageTableModel(Storage storage)
    {
        this.storage = storage;
        this.user = storage.getLocalUser();
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
            case COL_ATTACHMENTS:
            case COL_SIZE:
                return Integer.class;
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
            case COL_ATTACHMENTS:
                return "Attachments";
            case COL_SIZE:
                return "Attachment Size (bytes)";
            case COL_DATE:
                return "Date";
        }
        return null;
    }
    
    public boolean isCellEditable(int rowIndex,
        int columnIndex)
    {
        return false;
    }
    
    public int getColumnCount()
    {
        return 8;
    }
    
    public int getRowCount()
    {
        return user.getUserMessages().size();
    }
    
    public Object getValueAt(int rowIndex, int column)
    {
        UserMessage message = user.getUserMessages().get(
            rowIndex);
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
        switch (column)
        {
            case COL_TYPE:
                return (message.isOutbound() ? (message
                    .isDraft() ? "Draft" : "Sent")
                    : (message.isRead() ? "Read" : "Unread"));
            case COL_DATE:
                return new Date(message.getDate());
            case COL_FROM:
                return message.getSender();
            case COL_TO:
                return StringUtils.delimited(message
                    .getRecipients().isolate().toArray(
                        new UserMessageRecipient[0]),
                    new ToString<UserMessageRecipient>()
                    {
                        
                        public String toString(
                            UserMessageRecipient object)
                        {
                            return object.getUserid();
                        }
                    }, ", ");
            case COL_SUBJECT:
                return message.getSubject();
            case COL_IN_REPLY_TO:
                return message.getReplySubject();
            case COL_ATTACHMENTS:
                return message.getAttachments().size();
            case COL_SIZE:
                return computeAttachmentSize(message);
        }
        return null;
    }
    
    private int computeAttachmentSize(UserMessage message)
    {
        UserMessageAttachment[] attachments = message
            .getAttachments().isolate().toArray(
                new UserMessageAttachment[0]);
        int size = 0;
        for (UserMessageAttachment attachment : attachments)
        {
            size += attachment.getSize();
        }
        return size;
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
    public boolean matches(int row, String criteria,
        boolean extended)
    {
        if (criteria == null || "".equals(criteria.trim()))
            return true;
        criteria = criteria.toLowerCase();
        UserMessage message = user.getUserMessages().get(
            row);
        if (message == null)
            return true;
        String messageSubject = message.getSubject();
        if (messageSubject.toLowerCase().contains(criteria))
            return true;
        if (extended)
        {
            /*
             * In the future we should search the attachment names and recipient
             * names, and probably the "reply to" subject, but for now we're not
             * going to, since it would slow the search down by quite a bit.
             */
            String messageContents = message.getMessage();
            if (messageContents.toLowerCase().contains(
                criteria))
                return true;
        }
        return false;
    }
}
