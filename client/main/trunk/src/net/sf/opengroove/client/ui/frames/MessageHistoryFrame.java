package net.sf.opengroove.client.ui.frames;

import com.jidesoft.swing.JideButton;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.UserMessage;
import net.sf.opengroove.client.ui.UserMessageTableModel;
import net.sf.opengroove.common.ui.ComponentUtils;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class MessageHistoryFrame extends javax.swing.JFrame
{
    private JPanel rootPanel;
    private JPanel lowerPanel;
    private JPanel toolbarPanel;
    private JTextField searchField;
    private JLabel searchHeaderLabel;
    private JScrollPane tableScrollPane;
    private JideButton searchButton;
    private JideButton clearSearchButton;
    private JCheckBox searchMessageCheckbox;
    private JTable table;
    private JLabel toolbarSpacerLabel;
    private JButton openButton;
    private JPanel contentPanel;
    private JButton okButton;
    
    private Storage storage;
    private LocalUser user;
    private UserMessageTableModel tableModel;
    private TableRowSorter<UserMessageTableModel> sorter;
    private String currentSearchString = "";
    private boolean currentExtendedSearch = false;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                MessageHistoryFrame inst = new MessageHistoryFrame(null);
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public MessageHistoryFrame(Storage storage)
    {
        super();
        this.storage = storage;
        if (storage != null)
            this.user = storage.getLocalUser();
        initGUI();
        addWindowListener(new WindowAdapter()
        {
            
            public void windowClosed(WindowEvent e)
            {
                searchField.setText("");
                searchMessageCheckbox.setSelected(false);
                currentExtendedSearch = false;
                currentSearchString = "";
                if (MessageHistoryFrame.this.storage != null)
                    sorter.sort();
            }
        });
        setLocationRelativeTo(null);
    }
    
    private void initGUI()
    {
        try
        {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            {
                rootPanel = new JPanel();
                BorderLayout rootPanelLayout = new BorderLayout();
                rootPanel.setLayout(rootPanelLayout);
                rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                getContentPane().add(rootPanel, BorderLayout.CENTER);
                {
                    lowerPanel = new JPanel();
                    BorderLayout lowerPanelLayout = new BorderLayout();
                    rootPanel.add(lowerPanel, BorderLayout.SOUTH);
                    lowerPanel.setLayout(lowerPanelLayout);
                    {
                        okButton = new JButton();
                        okButton
                            .setToolTipText(ComponentUtils
                                .htmlTipWrap("Closes the message history window. This does the same thing as if you had just closed the window itself."));
                        lowerPanel.add(okButton, BorderLayout.EAST);
                        okButton.setText("OK");
                        okButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                okButtonActionPerformed(evt);
                            }
                        });
                    }
                }
                {
                    contentPanel = new JPanel();
                    TableLayout contentPanelLayout =
                        new TableLayout(new double[][] { { TableLayout.FILL },
                            { TableLayout.PREFERRED, TableLayout.FILL, 5.0 } });
                    contentPanelLayout.setHGap(5);
                    contentPanelLayout.setVGap(5);
                    rootPanel.add(contentPanel, BorderLayout.CENTER);
                    contentPanel.setLayout(contentPanelLayout);
                    {
                        toolbarPanel = new JPanel();
                        BoxLayout toolbarPanelLayout =
                            new BoxLayout(toolbarPanel, javax.swing.BoxLayout.X_AXIS);
                        toolbarPanel.setLayout(toolbarPanelLayout);
                        contentPanel.add(toolbarPanel, "0, 0");
                        {
                            openButton = new JButton();
                            openButton
                                .setToolTipText(ComponentUtils
                                    .htmlTipWrap("Opens the currently-selected messages. You can "
                                        + "drag your mouse over a bunch of mesages to select "
                                        + "multiple messages at a time, or you can hold down "
                                        + "the control key on your keyboard and click on "
                                        + "messages to select multiple messages. The messages "
                                        + "that are selected will be opened in new windows."));
                            toolbarPanel.add(openButton);
                            openButton.setText("Open");
                            openButton.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent evt)
                                {
                                    openButtonActionPerformed(evt);
                                }
                            });
                        }
                        {
                            toolbarSpacerLabel = new JLabel();
                            toolbarPanel.add(toolbarSpacerLabel);
                            toolbarSpacerLabel.setMaximumSize(new java.awt.Dimension(
                                1000000, 1000000));
                        }
                        {
                            searchHeaderLabel = new JLabel();
                            toolbarPanel.add(searchHeaderLabel);
                        }
                        {
                            searchMessageCheckbox = new JCheckBox();
                            searchMessageCheckbox
                                .setToolTipText(ComponentUtils
                                    .htmlTipWrap("If this is checked, the contents of the message "
                                        + "will be searched. If this "
                                        + "is not checked, only the message's sender and "
                                        + "subject will be searched. This "
                                        + "can slow the search down by a considerable "
                                        + "amount, so you should generally only check this "
                                        + "when you need it."));
                            toolbarPanel.add(searchMessageCheckbox);
                            searchMessageCheckbox.setText("Search message contents");
                        }
                        {
                            searchField = new JTextField(15);
                            searchField
                                .setToolTipText(ComponentUtils
                                    .htmlTipWrap("Type some text to search for here, then click Search."));
                            searchField.setMaximumSize(searchField.getPreferredSize());
                            toolbarPanel.add(searchField);
                        }
                        {
                            searchButton = new JideButton();
                            toolbarPanel.add(searchButton);
                            searchButton.setText("Search");
                            searchButton.setToolTipText(ComponentUtils
                                .htmlTipWrap("Performs the actual search. A dialog "
                                    + "will be opened while the search is in progress to"
                                    + " show you how far it has come."));
                            searchButton.setButtonStyle(3);
                            searchButton.setForeground(new java.awt.Color(0, 0, 255));
                            searchButton.setFont(new java.awt.Font("Dialog", 0, 12));
                            searchButton.setAlwaysShowHyperlink(true);
                            searchButton.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent evt)
                                {
                                    searchButtonActionPerformed(evt);
                                }
                            });
                        }
                        {
                            clearSearchButton = new JideButton();
                            toolbarPanel.add(clearSearchButton);
                            clearSearchButton.setText("X");
                            clearSearchButton
                                .setToolTipText(ComponentUtils
                                    .htmlTipWrap("Clears the current search, so that all messages "
                                        + "will be shown again. You can also clear the current "
                                        + "search by clearing the search text box and "
                                        + "clicking \"Search\". If a search is not in progress, "
                                        + "then this button does nothing."));
                            clearSearchButton.setButtonStyle(3);
                            clearSearchButton.setAlwaysShowHyperlink(true);
                            clearSearchButton.setFont(new java.awt.Font("Dialog", 0, 12));
                            clearSearchButton.setForeground(new java.awt.Color(0, 0, 255));
                            clearSearchButton.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent evt)
                                {
                                    clearSearchButtonActionPerformed(evt);
                                }
                            });
                        }
                    }
                    {
                        tableScrollPane = new JScrollPane();
                        contentPanel.add(tableScrollPane, "0, 1");
                        {
                            table = new JTable()
                            {
                                protected JTableHeader createDefaultTableHeader()
                                {
                                    return new JTableHeader(columnModel)
                                    {
                                        public String getToolTipText(MouseEvent e)
                                        {
                                            String tip = null;
                                            java.awt.Point p = e.getPoint();
                                            int index = columnModel.getColumnIndexAtX(p.x);
                                            int realIndex =
                                                columnModel.getColumn(index).getModelIndex();
                                            switch (realIndex)
                                            {
                                                case UserMessageTableModel.COL_TYPE:
                                                    return ComponentUtils
                                                        .htmlTipWrap("The type of message that this one is. This is either Draft, "
                                                            + "Sent, Unread, or Read. If a message is marked as Draft or Sent, "
                                                            + "then it is an outbound message. If a message is marked as Read or "
                                                            + "Unread, then it is an inbound message. If you send a message to "
                                                            + "yourself, it will appear in the list twice: once as an outbound "
                                                            + "message and once as an inbound message.");
                                                case UserMessageTableModel.COL_ATTACHMENTS:
                                                    return ComponentUtils
                                                        .htmlTipWrap("The number of attachments present on this message.");
                                                case UserMessageTableModel.COL_SIZE:
                                                    return ComponentUtils
                                                        .htmlTipWrap("The total size of this message's attachments, "
                                                            + "in bytes. Even if some attachments have been removed "
                                                            + "locally, they will still show up in this count.");
                                                case UserMessageTableModel.COL_DATE:
                                                    return ComponentUtils
                                                        .htmlTipWrap("If this message is a draft, then this is "
                                                            + "the date that the draft was created. If this "
                                                            + "message has been sent, or if this message is an "
                                                            + "inbound message, then this is the date that it was sent.");
                                                case UserMessageTableModel.COL_FROM:
                                                    return ComponentUtils
                                                        .htmlTipWrap("The sender of this message.");
                                                case UserMessageTableModel.COL_TO:
                                                    return ComponentUtils
                                                        .htmlTipWrap("This message's recipients.");
                                                case UserMessageTableModel.COL_SUBJECT:
                                                    return ComponentUtils
                                                        .htmlTipWrap("The subject of this message.");
                                                case UserMessageTableModel.COL_IN_REPLY_TO:
                                                    return ComponentUtils
                                                        .htmlTipWrap("The subject of the message that this one is in "
                                                            + "reply to. If this message isn't in reply to another"
                                                            + " message, then this column will be empty.");
                                            }
                                            return null;
                                        }
                                    };
                                }
                            };
                            table.addMouseListener(new MouseAdapter()
                            {
                                
                                public void mouseClicked(MouseEvent e)
                                {
                                    if (e.getClickCount() == 2)
                                    {
                                        openButtonActionPerformed(null);
                                    }
                                }
                            });
                            if (storage != null)
                            {
                                tableModel = new UserMessageTableModel(storage);
                                table.setModel(tableModel);
                                sorter =
                                    new TableRowSorter<UserMessageTableModel>(tableModel);
                                RowFilter<UserMessageTableModel, Integer> rowFilter =
                                    new RowFilter<UserMessageTableModel, Integer>()
                                    {
                                        
                                        public boolean include(
                                            javax.swing.RowFilter.Entry<? extends UserMessageTableModel, ? extends Integer> entry)
                                        {
                                            return entry.getModel().matches(
                                                entry.getIdentifier(), currentSearchString,
                                                currentExtendedSearch);
                                        }
                                    };
                                sorter.setRowFilter(rowFilter);
                                table.setRowSorter(sorter);
                                sorter.sort();
                                TableColumnModel columns = table.getColumnModel();
                            }
                            table.setRowSelectionAllowed(true);
                            table.setColumnSelectionAllowed(false);
                            table
                                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                            tableScrollPane.setViewportView(table);
                        }
                    }
                }
            }
            pack();
            this.setSize(687, 606);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void okButtonActionPerformed(ActionEvent evt)
    {
        dispose();
    }
    
    private void openButtonActionPerformed(ActionEvent evt)
    {
        int[] selectedRowIndexes = table.getSelectedRows();
        if (selectedRowIndexes.length > 5
            && !(JOptionPane.showConfirmDialog(this, "<html>You're about to open "
                + selectedRowIndexes.length + " messages. Opening this many messages<br/>"
                + "could slow down your computer quite a bit. "
                + "Are you<br/>sure you still want to open these messages?", null,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION))
            return;
        if (selectedRowIndexes.length == 0)
            return;
        for (int index : selectedRowIndexes)
        {
            UserMessage message =
                storage.getLocalUser().getUserMessages().get(
                    table.convertRowIndexToModel(index));
            if (message != null)
                ComposeMessageFrame.showComposeMessageFrame(storage, message);
        }
    }
    
    private void searchButtonActionPerformed(ActionEvent evt)
    {
        currentSearchString = searchField.getText();
        currentExtendedSearch = searchMessageCheckbox.isSelected();
        sorter.sort();
    }
    
    private void clearSearchButtonActionPerformed(ActionEvent evt)
    {
        searchField.setText("");
        searchButtonActionPerformed(evt);
    }
    
    public void reload()
    {
        tableModel.fireTableDataChanged();
    }
    
}
