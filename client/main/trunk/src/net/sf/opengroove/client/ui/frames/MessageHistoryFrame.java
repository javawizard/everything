package net.sf.opengroove.client.ui.frames;

import com.jidesoft.swing.JideButton;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.Storage;
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
    private TableRowSorter sorter;
    private String currentSearchString = "";
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                MessageHistoryFrame inst = new MessageHistoryFrame(
                    null);
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
                rootPanel.setBorder(new EmptyBorder(10, 10,
                    10, 10));
                getContentPane().add(rootPanel,
                    BorderLayout.CENTER);
                {
                    lowerPanel = new JPanel();
                    BorderLayout lowerPanelLayout = new BorderLayout();
                    rootPanel.add(lowerPanel,
                        BorderLayout.SOUTH);
                    lowerPanel.setLayout(lowerPanelLayout);
                    {
                        okButton = new JButton();
                        okButton
                            .setToolTipText(ComponentUtils
                                .htmlTipWrap("Closes the message history window. This does the same thing as if you had just closed the window itself."));
                        lowerPanel.add(okButton,
                            BorderLayout.EAST);
                        okButton.setText("OK");
                        okButton
                            .addActionListener(new ActionListener()
                            {
                                public void actionPerformed(
                                    ActionEvent evt)
                                {
                                    okButtonActionPerformed(evt);
                                }
                            });
                    }
                }
                {
                    contentPanel = new JPanel();
                    TableLayout contentPanelLayout = new TableLayout(
                        new double[][] {
                            { TableLayout.FILL },
                            { TableLayout.PREFERRED,
                                TableLayout.FILL, 5.0 } });
                    contentPanelLayout.setHGap(5);
                    contentPanelLayout.setVGap(5);
                    rootPanel.add(contentPanel,
                        BorderLayout.CENTER);
                    contentPanel
                        .setLayout(contentPanelLayout);
                    {
                        toolbarPanel = new JPanel();
                        BoxLayout toolbarPanelLayout = new BoxLayout(
                            toolbarPanel,
                            javax.swing.BoxLayout.X_AXIS);
                        toolbarPanel
                            .setLayout(toolbarPanelLayout);
                        contentPanel.add(toolbarPanel,
                            "0, 0");
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
                            openButton
                                .addActionListener(new ActionListener()
                                {
                                    public void actionPerformed(
                                        ActionEvent evt)
                                    {
                                        openButtonActionPerformed(evt);
                                    }
                                });
                        }
                        {
                            toolbarSpacerLabel = new JLabel();
                            toolbarPanel
                                .add(toolbarSpacerLabel);
                            toolbarSpacerLabel
                                .setMaximumSize(new java.awt.Dimension(
                                    1000000, 1000000));
                        }
                        {
                            searchHeaderLabel = new JLabel();
                            toolbarPanel
                                .add(searchHeaderLabel);
                        }
                        {
                            searchMessageCheckbox = new JCheckBox();
                            searchMessageCheckbox
                                .setToolTipText(ComponentUtils
                                    .htmlTipWrap("If this is checked, the contents of the message "
                                        + "(including the names of it's attachments, and "
                                        + "it's reply subject) will be searched. If this "
                                        + "is not checked, only the message's sender, "
                                        + "recipients, and subject will be searched. This "
                                        + "can slow the search down by a considerable "
                                        + "amount, so you should generally only check this "
                                        + "when you need it."));
                            toolbarPanel
                                .add(searchMessageCheckbox);
                            searchMessageCheckbox
                                .setText("Search message contents");
                        }
                        {
                            searchField = new JTextField(15);
                            searchField
                                .setToolTipText(ComponentUtils
                                    .htmlTipWrap("Type some text to search for here, then click Search."));
                            searchField
                                .setMaximumSize(searchField
                                    .getPreferredSize());
                            toolbarPanel.add(searchField);
                        }
                        {
                            searchButton = new JideButton();
                            toolbarPanel.add(searchButton);
                            searchButton.setText("Search");
                            searchButton
                                .setToolTipText(ComponentUtils
                                    .htmlTipWrap("Performs the actual search. A dialog "
                                        + "will be opened while the search is in progress to"
                                        + " show you how far it has come."));
                            searchButton.setButtonStyle(3);
                            searchButton
                                .setForeground(new java.awt.Color(
                                    0, 0, 255));
                            searchButton
                                .setFont(new java.awt.Font(
                                    "Dialog", 0, 12));
                            searchButton
                                .setAlwaysShowHyperlink(true);
                            searchButton
                                .addActionListener(new ActionListener()
                                {
                                    public void actionPerformed(
                                        ActionEvent evt)
                                    {
                                        searchButtonActionPerformed(evt);
                                    }
                                });
                        }
                        {
                            clearSearchButton = new JideButton();
                            toolbarPanel
                                .add(clearSearchButton);
                            clearSearchButton.setText("X");
                            clearSearchButton
                                .setToolTipText(ComponentUtils
                                    .htmlTipWrap("Clears the current search, so that all messages "
                                        + "will be shown again. You can also clear the current "
                                        + "search by clearing the search text box and "
                                        + "clicking \"Search\". If a search is not in progress, "
                                        + "then this button does nothing."));
                            clearSearchButton
                                .setButtonStyle(3);
                            clearSearchButton
                                .setAlwaysShowHyperlink(true);
                            clearSearchButton
                                .setFont(new java.awt.Font(
                                    "Dialog", 0, 12));
                            clearSearchButton
                                .setForeground(new java.awt.Color(
                                    0, 0, 255));
                        }
                    }
                    {
                        tableScrollPane = new JScrollPane();
                        contentPanel.add(tableScrollPane,
                            "0, 1");
                        {
                            if(storage != null)
                            {
                                tableModel = new UserMessageTableModel(storage);
                                table.setModel(tableModel);
                            }
                            table = new JTable();
                            tableScrollPane
                                .setViewportView(table);
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
        System.out
            .println("okButton.actionPerformed, event="
                + evt);
        // TODO add your code for okButton.actionPerformed
    }
    
    private void openButtonActionPerformed(ActionEvent evt)
    {
        System.out
            .println("openButton.actionPerformed, event="
                + evt);
        // TODO add your code for openButton.actionPerformed
    }
    
    private void searchButtonActionPerformed(ActionEvent evt)
    {
        System.out
            .println("searchButton.actionPerformed, event="
                + evt);
        // TODO add your code for searchButton.actionPerformed
    }
    
}
