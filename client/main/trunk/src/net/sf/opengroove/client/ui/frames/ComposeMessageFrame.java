package net.sf.opengroove.client.ui.frames;

import com.jidesoft.swing.JideButton;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.swing.text.html.HTMLEditorKit;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.UserContext;
import net.sf.opengroove.client.com.model.StoredMessageRecipient;
import net.sf.opengroove.client.messaging.MessageHierarchy;
import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.OutboundMessage;
import net.sf.opengroove.client.storage.OutboundMessageRecipient;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.UserMessage;
import net.sf.opengroove.client.storage.UserMessageAttachment;
import net.sf.opengroove.client.storage.UserMessageRecipient;
import net.sf.opengroove.client.ui.StatusDialog;
import net.sf.opengroove.client.ui.UserMessageAttachmentsModel;
import net.sf.opengroove.client.ui.UserMessageRecipientsModel;
import net.sf.opengroove.common.ui.ComponentUtils;
import net.sf.opengroove.common.utils.DataUtils;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * This frame shows a window that allows a user to compose a message. It handles
 * loading and saving it's contents from and to a UserMessage object and it's
 * attachment folders. In fact, it has to have a UserMessage object passed in
 * when it's created, and it will use the attachment folder corresponding to
 * that message when adding attachments.
 * 
 * The only button who's action is handled by this frame is the "save as draft"
 * button, which simply discards this frame.
 * 
 * The layout of this frame was created using Jigloo. See the license on other
 * files created with Jigloo for more information.
 * 
 * @author Alexander Boyd
 * 
 */
public class ComposeMessageFrame extends javax.swing.JFrame
{
    public class AddAttachmentTransferHandler extends TransferHandler
    {
        public boolean canImport(TransferSupport support)
        {
            if (Arrays.asList(support.getDataFlavors()).contains(
                DataFlavor.javaFileListFlavor))
                return true;
            return false;
        }
        
        public boolean importData(TransferSupport support)
        {
            Transferable transfer = support.getTransferable();
            try
            {
                final List<File> fileList =
                    (List<File>) transfer.getTransferData(DataFlavor.javaFileListFlavor);
                new Thread()
                {
                    public void run()
                    {
                        importAttachments(fileList.toArray(new File[0]));
                    }
                }.start();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            return true;
        }
    }
    
    public class AttachmentsListCellRenderer extends JLabel implements ListCellRenderer
    {
        
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus)
        {
            if (value.equals(list.getSelectedValue()))
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                setOpaque(true);
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
                setOpaque(list.isOpaque());
            }
            String attachmentName = (String) value;
            UserMessageAttachment attachment = message.getAttachmentByName(attachmentName);
            if (attachment == null)
            {
                setIcon(null);
                setText("NONEXISTANT: " + attachmentName);
            }
            else
            {
                setText(attachmentName);
                setIcon(attachment.isFolder() ? OpenGroove.Icons.FOLDER_16.getIcon()
                    : OpenGroove.Icons.FILE_16.getIcon());
            }
            return this;
        }
    }
    
    private JPanel rootPanel;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JPanel messageTopPanel;
    private JLabel jLabel4;
    private JPanel jPanel3;
    private JLabel jLabel8;
    private JScrollPane jScrollPane1;
    private JButton cancelButton;
    private JButton saveButton;
    private JLabel jLabel7;
    private JButton sendButton;
    private JPanel buttonPanel;
    private JLabel jLabel6;
    private JideButton addFolderButton;
    private JideButton addFileButton;
    private JPanel attachmentsToolbar;
    private JPanel jPanel1;
    private JEditorPane messageArea;
    private JTextField subjectField;
    private JLabel fromLabel;
    private JCheckBox includeHistoryCheckbox;
    private JButton closeButton;
    private JButton forwardButton;
    private JButton replyToAllButton;
    private JButton replyButton;
    private JideButton removeAttachmentButton;
    private JideButton openAttachmentButton;
    private JideButton saveAllButton;
    private JideButton saveAttachmentButton;
    private JideButton removeRecipientButton;
    private JPanel jPanel2;
    private JList recipientsList;
    private JList attachmentsList;
    private JLabel attachmentAreaReadLabel;
    private JPanel attachmentHelpPanel;
    private JSeparator jSeparator1;
    private JLabel fromContents;
    private JLabel attachmentAreaHintLabel;
    private JPanel attachmentUpperPanel;
    private JPanel attachmentScrollingPanel;
    private JLabel inReplyToLabel;
    private JLabel jLabel1;
    private JideButton addRecipientButton;
    private UserMessageAttachmentsModel attachmentsModel;
    private static final HashMap<UserMessage, ComposeMessageFrame> composeFrames =
        new HashMap<UserMessage, ComposeMessageFrame>();
    
    private static JFileChooser addFileChooser = new JFileChooser();
    private static JFileChooser addFolderChooser = new JFileChooser();
    private static JFileChooser saveChooser = new JFileChooser();
    private static JFileChooser saveAllChooser = new JFileChooser();
    
    public synchronized static void showComposeMessageFrame(Storage storage,
        UserMessage message)
    {
        if (storage != null && message != null && composeFrames.get(message) != null)
        {
            composeFrames.get(message).show();
            composeFrames.get(message).toFront();
            composeFrames.get(message).show();
        }
        else
        {
            ComposeMessageFrame frame = new ComposeMessageFrame(storage, message);
            frame.setLocationByPlatform(true);
            frame.show();
        }
    }
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                ComposeMessageFrame inst = new ComposeMessageFrame(null, null);
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    private Storage storage;
    private UserMessage message;
    private boolean isEditable;
    private UserMessageRecipientsModel recipientsModel;
    
    /**
     * Creates a new ComposeMessageFrame. If the specified storage or user
     * message is null, then no initialization is performed. In general, passing
     * null arguments should only be used for debugging the layout of the frame.
     * 
     * 
     */
    public ComposeMessageFrame(Storage storage, UserMessage message)
    {
        this.storage = storage;
        if (storage != null)
            this.message = message;
        initGUI();
        if (storage != null)
        {
            boolean isOutbound = message.isOutbound();
            boolean isDraft = message.isDraft();
            addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    int choice =
                        JOptionPane.showOptionDialog(ComposeMessageFrame.this,
                            "Would you like to save this message, or discard it?", null,
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                            new String[] { "Save", "Discard", "Cancel" }, "Save");
                    if (choice == 0)
                        ComposeMessageFrame.this.dispose();
                    else if (choice == 1)
                        discardMessage();
                }
                
                public void windowClosed(WindowEvent e)
                {
                    saveMessage();
                    composeFrames.remove(ComposeMessageFrame.this.message);
                }
            });
            composeFrames.put(ComposeMessageFrame.this.message, ComposeMessageFrame.this);
            attachmentsList.setEnabled(true);
            attachmentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            isEditable = isOutbound && isDraft;
            if (isEditable)
            {
                buttonPanel.remove(includeHistoryCheckbox);
                buttonPanel.remove(replyButton);
                buttonPanel.remove(replyToAllButton);
                buttonPanel.remove(forwardButton);
                buttonPanel.remove(closeButton);
            }
            else
            {
                subjectField.setEditable(false);
                messageArea.setEditable(false);
                addRecipientButton.setEnabled(false);
                removeRecipientButton.setEnabled(false);
                attachmentsToolbar.remove(addFileButton);
                attachmentsToolbar.remove(addFolderButton);
                attachmentsToolbar.remove(removeAttachmentButton);
                attachmentHelpPanel.remove(attachmentAreaHintLabel);
                buttonPanel.remove(sendButton);
                buttonPanel.remove(saveButton);
                buttonPanel.remove(cancelButton);
            }
            fromContents.setText(message.getSender());
            subjectField.setText(message.getSubject());
            messageArea.setText(message.getMessage());
            addFileChooser.setMultiSelectionEnabled(true);
            addFolderChooser.setMultiSelectionEnabled(true);
            saveChooser.setMultiSelectionEnabled(false);
            saveAllChooser.setMultiSelectionEnabled(false);
            addFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            addFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            saveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            saveAllChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            /*
             * TODO: we should perform some sort of initial check here to see if
             * there are any attachment storage files without backing attachment
             * proxystorage objects, and delete them.
             */
        }
    }
    
    /**
     * Saves the subject field and the message field to storage, and reloads the
     * message history table.
     */
    protected void saveMessage()
    {
        if (isEditable)
        {
            /*
             * This means that the message is stil a draft that hasn't been sent
             * yet. We'll want to save the message contents and the subject.
             */
            ComposeMessageFrame.this.message.setMessage(messageArea.getText());
            ComposeMessageFrame.this.message.setSubject(subjectField.getText());
            UserContext context =
                ComposeMessageFrame.this.storage.getLocalUser().getContext();
            if (context != null)
                context.getMessageHistoryFrame().reload();
        }
    }
    
    private void initGUI()
    {
        try
        {
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            {
                rootPanel = new JPanel();
                rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                TableLayout rootPanelLayout =
                    new TableLayout(new double[][] {
                        { TableLayout.PREFERRED, TableLayout.FILL },
                        { TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL,
                            TableLayout.PREFERRED, 0.33, TableLayout.PREFERRED } });
                rootPanelLayout.setHGap(5);
                rootPanelLayout.setVGap(5);
                getContentPane().add(rootPanel, BorderLayout.CENTER);
                rootPanel.setLayout(rootPanelLayout);
                {
                    jLabel1 = new JLabel();
                    rootPanel.add(jLabel1, "0, 1");
                    jLabel1.setText("To: ");
                    jLabel1.setVerticalAlignment(SwingConstants.TOP);
                }
                {
                    jLabel2 = new JLabel();
                    rootPanel.add(jLabel2, "0, 2");
                    jLabel2.setText("Subject: ");
                }
                {
                    subjectField = new JTextField();
                    rootPanel.add(subjectField, "1, 2");
                }
                {
                    jLabel3 = new JLabel();
                    rootPanel.add(jLabel3, "0, 5");
                    jLabel3.setText("Message: ");
                }
                {
                    messageTopPanel = new JPanel();
                    BoxLayout messageTopPanelLayout =
                        new BoxLayout(messageTopPanel, javax.swing.BoxLayout.X_AXIS);
                    rootPanel.add(messageTopPanel, "1, 5");
                    messageTopPanel.setLayout(messageTopPanelLayout);
                }
                {
                    messageArea = new JEditorPane();
                    HTMLEditorKit messageKit = new HTMLEditorKit();
                    messageArea.setEditorKit(messageKit);
                    messageArea.setFont(Font.decode(null));
                    rootPanel.add(new JScrollPane(messageArea), "0, 6, 1, 6");
                    messageTopPanel.add(Box.createHorizontalGlue());
                }
                {
                    jPanel1 = new JPanel();
                    BorderLayout jPanel1Layout = new BorderLayout();
                    jPanel1.setLayout(jPanel1Layout);
                    rootPanel.add(jPanel1, "1, 1");
                    {
                        jPanel2 = new JPanel();
                        BoxLayout jPanel2Layout1 =
                            new BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS);
                        jPanel2.setLayout(jPanel2Layout1);
                        jPanel1.add(jPanel2, BorderLayout.EAST);
                        {
                            addRecipientButton = new JideButton("Add recipient");
                            jPanel2.add(addRecipientButton);
                            addRecipientButton.setButtonStyle(3);
                            addRecipientButton.setAlwaysShowHyperlink(true);
                            addRecipientButton.setForeground(new Color(0, 0, 255));
                            addRecipientButton.setFont(new java.awt.Font("Dialog", 0, 12));
                            addRecipientButton.setVerticalAlignment(SwingConstants.TOP);
                            addRecipientButton.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent evt)
                                {
                                    addRecipientButtonActionPerformed(evt);
                                }
                            });
                        }
                        {
                            removeRecipientButton = new JideButton();
                            jPanel2.add(removeRecipientButton);
                            removeRecipientButton.setText("Remove");
                            removeRecipientButton
                                .setFont(new java.awt.Font("Dialog", 0, 12));
                            removeRecipientButton
                                .setForeground(new java.awt.Color(0, 0, 255));
                            removeRecipientButton.setButtonStyle(3);
                            removeRecipientButton.setAlwaysShowHyperlink(true);
                            removeRecipientButton.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent evt)
                                {
                                    removeRecipientButtonActionPerformed(evt);
                                }
                            });
                        }
                    }
                    {
                        recipientsList = new JList();
                        recipientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        if (storage != null)
                        {
                            recipientsModel =
                                new UserMessageRecipientsModel(storage, message);
                            recipientsList.setModel(recipientsModel);
                        }
                        recipientsList.setBackground(new Color(0, 0, 0, 0));
                        jPanel1.add(recipientsList, BorderLayout.CENTER);
                        recipientsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                        recipientsList.setOpaque(false);
                    }
                }
                {
                    jLabel4 = new JLabel();
                    rootPanel.add(jLabel4, "0, 7");
                    jLabel4.setText("Attachments: ");
                }
                {
                    attachmentsToolbar = new JPanel();
                    BoxLayout jPanel2Layout =
                        new BoxLayout(attachmentsToolbar, javax.swing.BoxLayout.X_AXIS);
                    rootPanel.add(attachmentsToolbar, "1, 7");
                    attachmentsToolbar.setLayout(jPanel2Layout);
                    {
                        jLabel6 = new JLabel();
                        attachmentsToolbar.add(jLabel6);
                        jLabel6.setMaximumSize(new java.awt.Dimension(100000, 100000));
                    }
                    {
                        addFileButton = new JideButton();
                        addFileButton.setButtonStyle(JideButton.HYPERLINK_STYLE);
                        addFileButton.setAlwaysShowHyperlink(true);
                        attachmentsToolbar.add(addFileButton);
                        addFileButton.setText("Add file");
                        addFileButton.setForeground(new java.awt.Color(0, 0, 255));
                        addFileButton.setFont(new java.awt.Font("Dialog", 0, 12));
                        addFileButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                addFileButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        addFolderButton = new JideButton();
                        attachmentsToolbar.add(addFolderButton);
                        addFolderButton.setText("Add folder");
                        addFolderButton.setForeground(new java.awt.Color(0, 0, 255));
                        addFolderButton.setFont(new java.awt.Font("Dialog", 0, 12));
                        addFolderButton.setAlwaysShowHyperlink(true);
                        addFolderButton.setButtonStyle(3);
                        addFolderButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                addFolderButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        removeAttachmentButton = new JideButton();
                        attachmentsToolbar.add(removeAttachmentButton);
                        removeAttachmentButton.setText("Remove");
                        removeAttachmentButton.setFont(new java.awt.Font("Dialog", 0, 12));
                        removeAttachmentButton.setForeground(new java.awt.Color(0, 0, 255));
                        removeAttachmentButton.setAlwaysShowHyperlink(true);
                        removeAttachmentButton.setButtonStyle(3);
                        removeAttachmentButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                removeAttachmentButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        saveAttachmentButton = new JideButton();
                        attachmentsToolbar.add(saveAttachmentButton);
                        saveAttachmentButton.setText("Save");
                        saveAttachmentButton.setFont(new java.awt.Font("Dialog", 0, 12));
                        saveAttachmentButton.setForeground(new java.awt.Color(0, 0, 255));
                        saveAttachmentButton.setAlwaysShowHyperlink(true);
                        saveAttachmentButton.setButtonStyle(3);
                        saveAttachmentButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                saveAttachmentButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        saveAllButton = new JideButton();
                        attachmentsToolbar.add(saveAllButton);
                        saveAllButton.setText("Save all");
                        saveAllButton.setButtonStyle(3);
                        saveAllButton.setFont(new java.awt.Font("Dialog", 0, 12));
                        saveAllButton.setForeground(new java.awt.Color(0, 0, 255));
                        saveAllButton.setAlwaysShowHyperlink(true);
                        saveAllButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                saveAllButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        openAttachmentButton = new JideButton();
                        attachmentsToolbar.add(openAttachmentButton);
                        openAttachmentButton.setText("Open");
                        openAttachmentButton.setAlwaysShowHyperlink(true);
                        openAttachmentButton.setButtonStyle(3);
                        openAttachmentButton.setFont(new java.awt.Font("Dialog", 0, 12));
                        openAttachmentButton.setForeground(new java.awt.Color(0, 0, 255));
                        openAttachmentButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                openAttachmentButtonActionPerformed(evt);
                            }
                        });
                    }
                }
                {
                    buttonPanel = new JPanel();
                    BoxLayout buttonPanelLayout =
                        new BoxLayout(buttonPanel, javax.swing.BoxLayout.X_AXIS);
                    rootPanel.add(buttonPanel, "0, 9, 1, 9");
                    buttonPanel.setLayout(buttonPanelLayout);
                    {
                        includeHistoryCheckbox = new JCheckBox();
                        buttonPanel.add(includeHistoryCheckbox);
                        includeHistoryCheckbox.setText("Include history in reply");
                    }
                    {
                        jLabel7 = new JLabel();
                        buttonPanel.add(jLabel7);
                        jLabel7.setMaximumSize(new java.awt.Dimension(100000, 100000));
                    }
                    {
                        sendButton = new JButton();
                        buttonPanel.add(sendButton);
                        sendButton.setText("Send");
                        sendButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                sendButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        saveButton = new JButton();
                        buttonPanel.add(saveButton);
                        saveButton.setText("Save as draft");
                        saveButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                saveButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        cancelButton = new JButton();
                        buttonPanel.add(cancelButton);
                        cancelButton.setText("Discard");
                        cancelButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                cancelButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        replyButton = new JButton();
                        buttonPanel.add(replyButton);
                        replyButton.setText("Reply");
                        replyButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                replyButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        replyToAllButton = new JButton();
                        buttonPanel.add(replyToAllButton);
                        replyToAllButton.setText("Reply to all");
                        replyToAllButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                replyToAllButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        forwardButton = new JButton();
                        buttonPanel.add(forwardButton);
                        forwardButton.setText("Forward");
                        forwardButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                forwardButtonActionPerformed(evt);
                            }
                        });
                    }
                    {
                        closeButton = new JButton();
                        buttonPanel.add(closeButton);
                        closeButton.setText("Close");
                        closeButton.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                closeButtonActionPerformed(evt);
                            }
                        });
                    }
                }
                {
                    jScrollPane1 = new JScrollPane();
                    rootPanel.add(jScrollPane1, "0, 8, 1, 8");
                    {
                        attachmentScrollingPanel = new JPanel();
                        BorderLayout jPanel4Layout = new BorderLayout();
                        attachmentScrollingPanel.setLayout(jPanel4Layout);
                        jScrollPane1.setViewportView(attachmentScrollingPanel);
                        attachmentScrollingPanel.setBackground(new java.awt.Color(255, 255,
                            255));
                        {
                            attachmentUpperPanel = new JPanel();
                            BorderLayout jPanel5Layout = new BorderLayout();
                            attachmentUpperPanel.setLayout(jPanel5Layout);
                            attachmentScrollingPanel.add(attachmentUpperPanel,
                                BorderLayout.NORTH);
                            attachmentUpperPanel.setOpaque(false);
                            {
                                attachmentHelpPanel = new JPanel();
                                BorderLayout attachmentHelpPanelLayout = new BorderLayout();
                                attachmentUpperPanel.add(attachmentHelpPanel,
                                    BorderLayout.SOUTH);
                                attachmentHelpPanel.setLayout(attachmentHelpPanelLayout);
                                attachmentHelpPanel.setOpaque(false);
                                {
                                    attachmentAreaHintLabel = new JLabel();
                                    attachmentHelpPanel.add(attachmentAreaHintLabel,
                                        BorderLayout.NORTH);
                                    attachmentAreaHintLabel
                                        .setText("To add attachments, drag files or folders onto this hint, or use the above links.");
                                    attachmentAreaHintLabel.setFont(new java.awt.Font(
                                        "Dialog", 0, 12));
                                    attachmentAreaHintLabel
                                        .setForeground(new java.awt.Color(150, 150, 150));
                                    attachmentAreaHintLabel
                                        .setTransferHandler(new AddAttachmentTransferHandler());
                                }
                                {
                                    attachmentAreaReadLabel = new JLabel();
                                    attachmentHelpPanel.add(attachmentAreaReadLabel,
                                        BorderLayout.SOUTH);
                                    attachmentAreaReadLabel
                                        .setText("To save attachments, drag them from the attachment list, or use the links next to the attachment.");
                                    attachmentAreaReadLabel
                                        .setForeground(new java.awt.Color(150, 150, 150));
                                    attachmentAreaReadLabel.setFont(new java.awt.Font(
                                        "Dialog", 0, 12));
                                }
                            }
                            {
                                attachmentsList = new JList();
                                attachmentsList
                                    .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                if (storage != null)
                                {
                                    attachmentsModel =
                                        new UserMessageAttachmentsModel(storage, message);
                                    attachmentsList.setModel(attachmentsModel);
                                    attachmentsList
                                        .setCellRenderer(new AttachmentsListCellRenderer());
                                }
                                attachmentUpperPanel
                                    .add(attachmentsList, BorderLayout.NORTH);
                            }
                        }
                    }
                }
                {
                    jLabel8 = new JLabel();
                    rootPanel.add(jLabel8, "0, 3");
                    jLabel8.setText("In reply to: ");
                }
                {
                    jPanel3 = new JPanel();
                    BorderLayout jPanel3Layout = new BorderLayout();
                    rootPanel.add(jPanel3, "1, 3");
                    jPanel3.setLayout(jPanel3Layout);
                    {
                        inReplyToLabel = new JLabel();
                        jPanel3.add(inReplyToLabel, BorderLayout.CENTER);
                        inReplyToLabel.setText("Not in reply");
                    }
                }
                {
                    fromLabel = new JLabel();
                    rootPanel.add(fromLabel, "0, 0");
                    fromLabel.setText("From: ");
                }
                {
                    fromContents = new JLabel();
                    rootPanel.add(fromContents, "1, 0");
                }
                {
                    jSeparator1 = new JSeparator();
                    rootPanel.add(jSeparator1, "0, 4, 1, 4");
                }
            }
            pack();
            this.setSize(495, 510);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void saveButtonActionPerformed(ActionEvent evt)
    {
        /*
         * All we need to do is dispose the frame.
         */
        dispose();
    }
    
    private void addFileButtonActionPerformed(ActionEvent evt)
    {
        new Thread()
        {
            public void run()
            {
                addFileChooser.setAcceptAllFileFilterUsed(true);
                if (addFileChooser.showOpenDialog(ComposeMessageFrame.this) != JFileChooser.APPROVE_OPTION)
                    return;
                File[] files = addFileChooser.getSelectedFiles();
                importAttachments(files);
            }
        }.start();
    }
    
    private void addFolderButtonActionPerformed(ActionEvent evt)
    {
        new Thread()
        {
            public void run()
            {
                if (addFolderChooser.showOpenDialog(ComposeMessageFrame.this) != JFileChooser.APPROVE_OPTION)
                    return;
                File[] folders = addFolderChooser.getSelectedFiles();
                importAttachments(folders);
            }
        }.start();
    }
    
    private void removeAttachmentButtonActionPerformed(ActionEvent evt)
    {
        String attachmentName = (String) attachmentsList.getSelectedValue();
        if (attachmentName == null)
        {
            JOptionPane.showMessageDialog(this,
                "You must select an attachment to remove first.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove this attachment?", null,
            JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        UserMessageAttachment attachment = message.getAttachmentByName(attachmentName);
        message.getAttachments().remove(attachment);
        storage.getMessageAttachmentFile(message.getId(), attachmentName).delete();
        attachmentsModel.reload();
    }
    
    private void saveAttachmentButtonActionPerformed(ActionEvent evt)
    {
        System.out.println("saveAttachmentButton.actionPerformed, event=" + evt);
        // TODO add your code for saveAttachmentButton.actionPerformed
    }
    
    private void saveAllButtonActionPerformed(ActionEvent evt)
    {
        System.out.println("saveAllButton.actionPerformed, event=" + evt);
        // TODO add your code for saveAllButton.actionPerformed
    }
    
    private void openAttachmentButtonActionPerformed(ActionEvent evt)
    {
        System.out.println("openAttachmentButton.actionPerformed, event=" + evt);
        // TODO add your code for openAttachmentButton.actionPerformed
    }
    
    private void sendButtonActionPerformed(ActionEvent evt)
    {
        /*
         * We'll first pop up a dialog that indicates that we are bundling the
         * message together to send it, so that the user doesn't sit wondering
         * what OpenGroove is doing. We'll then create the outbound message, and
         * begin writing it's fields to the file that represents it. We'll use
         * writeStringUTF() to encode strings. After that's done, we'll send the
         * message, and then mark it as not a draft anymore.
         */
        new Thread()
        {
            public void run()
            {
                sendMessage();
            }
        }.start();
    }
    
    protected void sendMessage()
    {
        if (!isEditable)
            return;
        saveMessage();
        message.setDate(System.currentTimeMillis());
        if (message.getRecipients().size() < 1)
        {
            JOptionPane.showMessageDialog(this,
                "This message doesn't have any recipients. A message "
                    + "cannot be sent if it doesn't have any recipients.");
            return;
        }
        StatusDialog dialog =
            new StatusDialog(this, "OpenGroove is bundling your message together "
                + "for sending, please wait...");
        dialog.showImmediate();
        LocalUser localUser = storage.getLocalUser();
        UserContext context = localUser.getContext();
        MessageHierarchy hierarchy = context.getUserMessageHierarchy();
        OutboundMessage outboundMessage = hierarchy.createMessage();
        for (UserMessageRecipient recipient : message.getRecipients().isolate())
        {
            OutboundMessageRecipient outboundRecipient = outboundMessage.createRecipient();
            outboundRecipient.setUserid(recipient.getUserid());
            outboundRecipient.setComputer("");
            outboundMessage.getRecipients().add(outboundRecipient);
        }
        File outfile = hierarchy.getOutboundMessageFile(outboundMessage.getId());
        try
        {
            FileOutputStream fileOut = new FileOutputStream(outfile);
            DataOutputStream out = new DataOutputStream(fileOut);
            /*
             * First, we'll write an int that specifies an encoding number. This
             * won't be used right now, but could later be used to recignize
             * messages with additional fields, by incrementing this number each
             * time the message encoding protocol changes.
             */
            out.writeInt(1);
            /*
             * Next, we'll write the message's internal id. We'll need to strip
             * off the trailing character, which should be an "o". See the
             * comment in UserContext.composeMessage where the id is generated
             * for more info on this letter "o".
             */
            out.writeUTF(message.getId().substring(0, message.getId().length() - 1));
            /*
             * Next comes the date.
             */
            out.writeLong(message.getDate());
            /*
             * Next we'll write the content type. Currently, this will only
             * contain the value text/html, but in the future, other message
             * renderings (such as plain text) could be used.
             */
            out.writeUTF(message.getContentType());
            /*
             * Next, we'll write the reply id and the reply subject. An empty
             * reply id indicates that this message is not in reply.
             */
            out.writeUTF(message.getReplyId());
            out.writeUTF(message.getReplySubject());
            /*
             * Next, we'll write the sender of the message. This will typically
             * be our own userid.
             */
            out.writeUTF(message.getSender());
            /*
             * Next, we'll write the message's recipient list.
             */
            ArrayList<UserMessageRecipient> recipientList =
                message.getRecipients().isolate();
            out.writeInt(recipientList.size());
            for (UserMessageRecipient recipient : recipientList)
            {
                out.writeUTF(recipient.getUserid());
            }
            /*
             * Now we'll write the actual message body. Since the body could
             * theoretically be larger than 32768 bytes (the maximum string size
             * allowed by writeUTF()), we'll first write the length of the byte
             * representation of the string as an int, and then the string's
             * bytes.
             */
            byte[] messageBytes = message.getMessage().getBytes();
            out.writeInt(messageBytes.length);
            out.write(messageBytes);
            /*
             * Now we'll write the int 0. This serves as another way (in
             * addition to the type byte, written as the first byte of the
             * message) to extend messages in the future. The receiving side
             * will read this number and will discard that many bytes after.
             * Future versions could include actual data there, and this number
             * would be the length of that data.
             */
            out.writeInt(0);
            /*
             * Next come the message's attachments. We'll write the number of
             * attachments present, and then we'll write each attachment's info
             * followed by it's contents.
             */
            ArrayList<UserMessageAttachment> attachmentList =
                message.getAttachments().isolate();
            out.writeInt(attachmentList.size());
            for (UserMessageAttachment attachment : attachmentList)
            {
                /*
                 * First, we'll write this attachment's name.
                 */
                out.writeUTF(attachment.getName());
                /*
                 * Then, we'll write it's size. When it comes time to write the
                 * attachment's actual data, we'll write the size again, but
                 * that time taken from the file, in case there's a mixup where
                 * the stored size doesn't reflect the attachment data size.
                 */
                out.writeInt(attachment.getSize());
                /*
                 * Now we'll write isEmbedded, isInternal, and isFolder, in that
                 * order.
                 */
                out.writeBoolean(attachment.isEmbedded());
                out.writeBoolean(attachment.isInternal());
                out.writeBoolean(attachment.isFolder());
                /*
                 * Now we'll write internalType. UserMessageAttachment defines
                 * (by way of a Default annotation) that internalType will not
                 * be null, so we don't have to worry about that when writing.
                 */
                out.writeUTF(attachment.getInternalType());
                /*
                 * Next comes the actual contents of the attachment, preceded by
                 * it's length.
                 */
                File attachmentFile =
                    storage.getMessageAttachmentFile(message.getId(), attachment.getName());
                out.writeInt((int) attachmentFile.length());
                FileInputStream attIn = new FileInputStream(attachmentFile);
                StringUtils.copy(attIn, out);
                attIn.close();
                /*
                 * That's it for the attachments.
                 */
            }
            /*
             * The message has now been written to the message file. We'll close
             * the file and then send the message.
             */
            out.flush();
            out.close();
            hierarchy.sendMessage(outboundMessage);
            storage.getLocalUser().getUserMessages().remove(message);
            storage.getLocalUser().getContext().getMessageHistoryFrame().reload();
            /*
             * The message has now been sent. We'll dispose the dialog and then
             * dispose this frame.
             */
            dialog.dispose();
            dispose();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            outfile.delete();
            outfile.deleteOnExit();
            dialog.dispose();
            JOptionPane.showMessageDialog(this,
                "Sending the message failed, for an unknown reason. "
                    + "Contact us at support@opengroove.org for assistance.");
        }
        finally
        {
            dialog.dispose();
        }
    }
    
    private void cancelButtonActionPerformed(ActionEvent evt)
    {
        if (JOptionPane.showConfirmDialog(this,
            "Are you sure you want to discard this message?", null,
            JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        discardMessage();
    }
    
    private void discardMessage()
    {
        String messageId = message.getId();
        storage.getLocalUser().getUserMessages().remove(message);
        File[] attachmentFiles = storage.getMessageAttachmentFolder(messageId).listFiles();
        if (attachmentFiles != null)
        {
            for (File file : attachmentFiles)
            {
                file.delete();
            }
        }
        isEditable = false;
        dispose();
        try
        {
            storage.getLocalUser().getContext().getMessageHistoryFrame().reload();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
    
    private void replyButtonActionPerformed(ActionEvent evt)
    {
        System.out.println("replyButton.actionPerformed, event=" + evt);
        // TODO add your code for replyButton.actionPerformed
    }
    
    private void replyToAllButtonActionPerformed(ActionEvent evt)
    {
        System.out.println("replyToAllButton.actionPerformed, event=" + evt);
        // TODO add your code for replyToAllButton.actionPerformed
    }
    
    private void forwardButtonActionPerformed(ActionEvent evt)
    {
        UserContext context = storage.getLocalUser().getContext();
        context.composeMessage("Fw: " + subjectField.getText(),
            "<hr/>Forwarded message from " + fromContents.getText() + " on "
                + new Date(message.getDate()) + ":<br/><br/>", "", "", new String[] {});
        /*
         * We really shouldn't do this, since the message contents are
         * surrounded by <html> tags, so we're relying on the JEditorPane to
         * detect the elements outside of the <html> element and merge them into
         * the <body> element.
         */
    }
    
    private void closeButtonActionPerformed(ActionEvent evt)
    {
        /*
         * All we need to do is dispose the window.
         */
        dispose();
    }
    
    /**
     * Called when the user has chosen some attachments to import. This is
     * called when the user chooses a file or folder via the "add file" or "add
     * folder" link, or when the user drags files or folders into the attachment
     * pane. This method validates that the specified files or folders really do
     * exist, and then it adds them first as message attachment files and then
     * as message attachment objects.<br/> <br/>
     * 
     * This method blocks until the attachments have been imported.<br/> <br/>
     * 
     * Right now, this method doesn't check to make sure that the message is
     * less than 2GB in size. It needs to do this in the future, to avoid the
     * user adding too many attachments and messing up the message.
     * 
     * @param attachments
     */
    private void importAttachments(File[] files)
    {
        if (!isEditable)
        {
            /*
             * This typically shouldn't happen if we've done our calculations
             * right, but we'll prepare for the worst
             */
            JOptionPane
                .showMessageDialog(this,
                    "You can't add attachments to this message, since it's not an outbound message.");
            return;
        }
        for (File file : files)
        {
            if (!file.exists())
            {
                JOptionPane
                    .showMessageDialog(this,
                        "You tried to add an attachment, but the file or folder you specified doesn't exist.");
                return;
            }
            if (message.getAttachmentByName(file.getName().toLowerCase()) != null)
            {
                new Thread()
                {
                    public void run()
                    {
                        JOptionPane
                            .showMessageDialog(ComposeMessageFrame.this, ComponentUtils
                                .htmlTipWrap("An attachment already exists with that name."));
                    }
                }.start();
                return;
            }
        }
        /*
         * All of the attachments-to-be exist at this point. We'll begin copying
         * them over.
         */
        final AddingAttachmentDialog dialog = new AddingAttachmentDialog(this);
        new Thread()
        {
            public void run()
            {
                dialog.show();
            }
        }.start();
        dialog.getProgress().setIndeterminate(true);
        try
        {
            for (File file : files)
            {
                String name = file.getName();
                name = name.toLowerCase();
                if (message.getAttachmentByName(name) != null)
                {
                    dialog.dispose();
                    new Thread()
                    {
                        public void run()
                        {
                            JOptionPane
                                .showMessageDialog(
                                    ComposeMessageFrame.this,
                                    ComponentUtils
                                        .htmlTipWrap("An attachment already exists with that name."));
                        }
                    }.start();
                    return;
                }
                File attachmentFile =
                    storage.getMessageAttachmentFile(message.getId(), name);
                if (attachmentFile.exists())
                {
                    /*
                     * This puts us in a problematic position. If we get here,
                     * then either an attachment file exists from an attachment
                     * that didn't get added as a proxystoage object, in which
                     * case we can delete it and replace it with this one, or
                     * there is a corresponding attachment, and the filesystem
                     * naming conventions are causing us problems. However,
                     * since we've converted the name to lower case before
                     * adding the attachment, then we can be reasonably certain
                     * that it's not the filesystem's problem, so we'll just
                     * delete the file and replace it.
                     */
                    if (!attachmentFile.delete())
                        throw new RuntimeException(
                            "attachment existing file couldn't be deleted.");
                }
                /*
                 * The attachment file doesn't exist at this point, and the
                 * source file does. We'll begin copying it over.
                 */
                dialog.getProgress().setString(name);
                dialog.getProgress().setIndeterminate(true);
                if (file.isFile())
                    importFile(file, attachmentFile);
                else if (file.isDirectory())
                    importFolder(file, attachmentFile);
                else
                    throw new RuntimeException();
                /*
                 * We've imported the attachment file or folder, so now we have
                 * a single file that we can add. Now we'll create a user
                 * message attachment, and add it to the message.
                 */
                UserMessageAttachment attachment = message.createAttachment();
                attachment.setEmbedded(false);
                attachment.setFolder(file.isDirectory());
                attachment.setInternal(false);
                attachment.setInternalType("");
                attachment.setName(name.toLowerCase());
                attachment.setSize((int) attachmentFile.length());
                
                message.getAttachments().add(attachment);
                attachmentsModel.reload();
            }
            /*
             * We're done! We'll let the finally block hide the dialog for us.
             */
        }
        catch (Exception e)
        {
            e.printStackTrace();
            dialog.dispose();
            new Thread()
            {
                public void run()
                {
                    JOptionPane
                        .showMessageDialog(ComposeMessageFrame.this, ComponentUtils
                            .htmlTipWrap("The attachment(s) could not be added, because "
                                + "of an internal error. Contact us "
                                + "(support@opengroove.org) for assistance, "
                                + "or try again."));
                }
            }.start();
        }
        finally
        {
            dialog.dispose();
        }
    }
    
    private void importFolder(File file, File attachmentFile) throws IOException
    {
        FileOutputStream fileOut = new FileOutputStream(attachmentFile);
        ZipOutputStream out = new ZipOutputStream(fileOut);
        recursiveZipWrite(file, out, "");
        out.flush();
        out.close();
    }
    
    /**
     * Adds the file or folder to the zip file specified, including all of it's
     * subfolders (as "recurisve" in the method name should imply).
     * 
     * @param file
     * @param out
     * @param currentParentPath
     * @throws IOException
     */
    private void recursiveZipWrite(File file, ZipOutputStream out, String currentParentPath)
        throws IOException
    {
        if (file.isFile())
        {
            ZipEntry entry = new ZipEntry(currentParentPath + file.getName());
            entry.setTime(file.lastModified());
            out.putNextEntry(entry);
            FileInputStream in = new FileInputStream(file);
            StringUtils.copy(in, out);
            in.close();
            out.closeEntry();
        }
        else if (file.isDirectory())
        {
            for (File subfile : file.listFiles())
            {
                recursiveZipWrite(subfile, out, currentParentPath + file.getName() + "/");
            }
        }
    }
    
    private void importFile(File file, File attachmentFile) throws IOException
    {
        /*
         * All we need to do is copy the attachment over.
         */
        FileInputStream in = new FileInputStream(file);
        FileOutputStream out = new FileOutputStream(attachmentFile);
        StringUtils.copy(in, out);
        out.flush();
        out.close();
        in.close();
    }
    
    private void removeRecipientButtonActionPerformed(ActionEvent evt)
    {
        if (!isEditable)
            return;
        if (recipientsList.getSelectedIndex() == -1)
        {
            JOptionPane
                .showMessageDialog(
                    this,
                    "Select a recipient to delete first. You can select a recipient by clicking on their userid, to the left.");
        }
        /*
         * A recipient is selected, and the message is editable. We'll remove
         * the recipient now.
         */
        message.getRecipients().remove(
            message.getRecipientById((String) recipientsList.getSelectedValue()));
        recipientsModel.reload();
    }
    
    private void addRecipientButtonActionPerformed(ActionEvent evt)
    {
        if (!isEditable)
            return;
        String newUserid =
            ContactChooser.chooseContact(this, storage, "Choose a contact to add.");
        if (newUserid == null)
            /*
             * They clicked "cancel" or closed the dialog
             */
            return;
        if (message.getRecipientById(newUserid) != null)
        {
            JOptionPane.showMessageDialog(this, "That user is already a recipient");
            return;
        }
        UserMessageRecipient newRecipient = message.createRecipient();
        newRecipient.setUserid(newUserid);
        message.getRecipients().add(newRecipient);
        recipientsModel.reload();
    }
    
}
