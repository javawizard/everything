package net.sf.opengroove.client.ui;

import javax.swing.table.AbstractTableModel;

import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.UserMessage;

public class UserMessageTableModel extends
    AbstractTableModel
{
    private static final int COL_TYPE = 0;
    private static final int COL_ATTACHMENTS = 1;
    private static final int COL_SIZE = 2;
    private static final int COL_DATE = 3;
    private static final int COL_FROM = 4;
    private static final int COL_TO = 5;
    private static final int COL_SUBJECT = 6;
    private static final int COL_IN_REPLY_TO = 7;
    
    private LocalUser user;
    
    public UserMessageTableModel(LocalUser user)
    {
        this.user = user;
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
        return 7;
    }
    
    public int getRowCount()
    {
        return user.getUserMessages().size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex)
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
    }
    
}
