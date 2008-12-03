package net.sf.opengroove.client.ui.frames;

import com.jidesoft.swing.JideButton;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.swing.text.html.HTMLEditorKit;

import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.UserMessage;
import net.sf.opengroove.client.ui.UserMessageAttachmentsModel;

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
    private JPanel recipientsPanel;
    private JPanel jPanel1;
    private JEditorPane messageArea;
    private JTextField subjectField;
    private JLabel fromLabel;
    private JideButton removeAttachmentButton;
    private JideButton openAttachmentButton;
    private JideButton saveAllButton;
    private JideButton saveAttachmentButton;
    private JList attachmentsList;
    private JLabel attachmentAreaReadLabel;
    private JPanel attachmentHelpPanel;
    private JSeparator jSeparator1;
    private JLabel jLabel9;
    private JLabel attachmentAreaHintLabel;
    private JPanel attachmentUpperPanel;
    private JPanel attachmentScrollingPanel;
    private JLabel inReplyToLabel;
    private JLabel jLabel1;
    private JideButton addRecipientButton;
    private UserMessageAttachmentsModel attachmentsModel;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                ComposeMessageFrame inst = new ComposeMessageFrame(
                    null, null);
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    private Storage storage;
    private UserMessage message;
    
    /**
     * Creates a new ComposeMessageFrame. If the specified storage or user
     * message is null, then no initialization is performed. In general, passing
     * null arguments should only be used for debugging the layout of the frame.
     * 
     * 
     */
    public ComposeMessageFrame(Storage storage,
        UserMessage message)
    {
        this.storage = storage;
        if (storage != null)
            this.message = message;
        initGUI();
        if (storage != null)
        {
            boolean isOutbound = message.isOutbound();
            boolean isDraft = message.isDraft();
        }
    }
    
    private void initGUI()
    {
        try
        {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            {
                rootPanel = new JPanel();
                rootPanel.setBorder(new EmptyBorder(10, 10,
                    10, 10));
                TableLayout rootPanelLayout = new TableLayout(
                    new double[][] {
                        { TableLayout.PREFERRED,
                            TableLayout.FILL },
                        { TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.FILL,
                            TableLayout.PREFERRED, 0.33,
                            TableLayout.PREFERRED } });
                rootPanelLayout.setHGap(5);
                rootPanelLayout.setVGap(5);
                getContentPane().add(rootPanel,
                    BorderLayout.CENTER);
                rootPanel.setLayout(rootPanelLayout);
                {
                    jLabel1 = new JLabel();
                    rootPanel.add(jLabel1, "0, 1");
                    jLabel1.setText("To: ");
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
                    BoxLayout messageTopPanelLayout = new BoxLayout(
                        messageTopPanel,
                        javax.swing.BoxLayout.X_AXIS);
                    rootPanel.add(messageTopPanel, "1, 5");
                    messageTopPanel
                        .setLayout(messageTopPanelLayout);
                }
                {
                    messageArea = new JEditorPane();
                    HTMLEditorKit messageKit = new HTMLEditorKit();
                    messageArea.setEditorKit(messageKit);
                    rootPanel.add(new JScrollPane(
                        messageArea), "0, 6, 1, 6");
                    messageTopPanel.add(Box
                        .createHorizontalGlue());
                }
                {
                    jPanel1 = new JPanel();
                    BorderLayout jPanel1Layout = new BorderLayout();
                    jPanel1.setLayout(jPanel1Layout);
                    rootPanel.add(jPanel1, "1, 1");
                    {
                        recipientsPanel = new JPanel();
                        FlowLayout recipientsPanelLayout = new FlowLayout();
                        recipientsPanelLayout
                            .setAlignment(FlowLayout.LEFT);
                        recipientsPanelLayout.setHgap(5);
                        recipientsPanelLayout.setVgap(1);
                        jPanel1.add(recipientsPanel,
                            BorderLayout.CENTER);
                        recipientsPanel
                            .setLayout(recipientsPanelLayout);
                    }
                    {
                        addRecipientButton = new JideButton(
                            "Add recipient");
                        addRecipientButton
                            .setButtonStyle(3);
                        addRecipientButton
                            .setAlwaysShowHyperlink(true);
                        addRecipientButton
                            .setForeground(new Color(0, 0,
                                255));
                        addRecipientButton
                            .setFont(new java.awt.Font(
                                "Dialog", 0, 12));
                        jPanel1.add(addRecipientButton,
                            BorderLayout.EAST);
                    }
                }
                {
                    jLabel4 = new JLabel();
                    rootPanel.add(jLabel4, "0, 7");
                    jLabel4.setText("Attachments: ");
                }
                {
                    attachmentsToolbar = new JPanel();
                    BoxLayout jPanel2Layout = new BoxLayout(
                        attachmentsToolbar,
                        javax.swing.BoxLayout.X_AXIS);
                    rootPanel.add(attachmentsToolbar,
                        "1, 7");
                    attachmentsToolbar
                        .setLayout(jPanel2Layout);
                    {
                        jLabel6 = new JLabel();
                        attachmentsToolbar.add(jLabel6);
                        jLabel6
                            .setMaximumSize(new java.awt.Dimension(
                                100000, 100000));
                    }
                    {
                        addFileButton = new JideButton();
                        addFileButton
                            .setButtonStyle(JideButton.HYPERLINK_STYLE);
                        addFileButton
                            .setAlwaysShowHyperlink(true);
                        attachmentsToolbar
                            .add(addFileButton);
                        addFileButton.setText("Add file");
                        addFileButton
                            .setForeground(new java.awt.Color(
                                0, 0, 255));
                        addFileButton
                            .setFont(new java.awt.Font(
                                "Dialog", 0, 12));
                    }
                    {
                        addFolderButton = new JideButton();
                        attachmentsToolbar
                            .add(addFolderButton);
                        addFolderButton
                            .setText("Add folder");
                        addFolderButton
                            .setForeground(new java.awt.Color(
                                0, 0, 255));
                        addFolderButton
                            .setFont(new java.awt.Font(
                                "Dialog", 0, 12));
                        addFolderButton
                            .setAlwaysShowHyperlink(true);
                        addFolderButton.setButtonStyle(3);
                    }
                    {
                        removeAttachmentButton = new JideButton();
                        attachmentsToolbar
                            .add(removeAttachmentButton);
                        removeAttachmentButton
                            .setText("Remove");
                        removeAttachmentButton
                            .setFont(new java.awt.Font(
                                "Dialog", 0, 12));
                        removeAttachmentButton
                            .setForeground(new java.awt.Color(
                                0, 0, 255));
                        removeAttachmentButton
                            .setAlwaysShowHyperlink(true);
                        removeAttachmentButton
                            .setButtonStyle(3);
                    }
                    {
                        saveAttachmentButton = new JideButton();
                        attachmentsToolbar
                            .add(saveAttachmentButton);
                        saveAttachmentButton
                            .setText("Save");
                        saveAttachmentButton
                            .setFont(new java.awt.Font(
                                "Dialog", 0, 12));
                        saveAttachmentButton
                            .setForeground(new java.awt.Color(
                                0, 0, 255));
                        saveAttachmentButton
                            .setAlwaysShowHyperlink(true);
                        saveAttachmentButton
                            .setButtonStyle(3);
                    }
                    {
                        saveAllButton = new JideButton();
                        attachmentsToolbar
                            .add(saveAllButton);
                        saveAllButton.setText("Save all");
                        saveAllButton.setButtonStyle(3);
                        saveAllButton
                            .setFont(new java.awt.Font(
                                "Dialog", 0, 12));
                        saveAllButton
                            .setForeground(new java.awt.Color(
                                0, 0, 255));
                        saveAllButton
                            .setAlwaysShowHyperlink(true);
                    }
                    {
                        openAttachmentButton = new JideButton();
                        attachmentsToolbar
                            .add(openAttachmentButton);
                        openAttachmentButton
                            .setText("Open");
                        openAttachmentButton
                            .setAlwaysShowHyperlink(true);
                        openAttachmentButton
                            .setButtonStyle(3);
                        openAttachmentButton
                            .setFont(new java.awt.Font(
                                "Dialog", 0, 12));
                        openAttachmentButton
                            .setForeground(new java.awt.Color(
                                0, 0, 255));
                    }
                }
                {
                    buttonPanel = new JPanel();
                    BoxLayout buttonPanelLayout = new BoxLayout(
                        buttonPanel,
                        javax.swing.BoxLayout.X_AXIS);
                    rootPanel
                        .add(buttonPanel, "0, 9, 1, 9");
                    buttonPanel
                        .setLayout(buttonPanelLayout);
                    {
                        jLabel7 = new JLabel();
                        buttonPanel.add(jLabel7);
                        jLabel7
                            .setMaximumSize(new java.awt.Dimension(
                                100000, 100000));
                    }
                    {
                        sendButton = new JButton();
                        buttonPanel.add(sendButton);
                        sendButton.setText("Send");
                    }
                    {
                        saveButton = new JButton();
                        buttonPanel.add(saveButton);
                        saveButton.setText("Save as draft");
                        saveButton
                            .addActionListener(new ActionListener()
                            {
                                public void actionPerformed(
                                    ActionEvent evt)
                                {
                                    saveButtonActionPerformed(evt);
                                }
                            });
                    }
                    {
                        cancelButton = new JButton();
                        buttonPanel.add(cancelButton);
                        cancelButton.setText("Cancel");
                    }
                }
                {
                    jScrollPane1 = new JScrollPane();
                    rootPanel.add(jScrollPane1,
                        "0, 8, 1, 8");
                    {
                        attachmentScrollingPanel = new JPanel();
                        BorderLayout jPanel4Layout = new BorderLayout();
                        attachmentScrollingPanel
                            .setLayout(jPanel4Layout);
                        jScrollPane1
                            .setViewportView(attachmentScrollingPanel);
                        attachmentScrollingPanel
                            .setBackground(new java.awt.Color(
                                255, 255, 255));
                        {
                            attachmentUpperPanel = new JPanel();
                            BorderLayout jPanel5Layout = new BorderLayout();
                            attachmentUpperPanel
                                .setLayout(jPanel5Layout);
                            attachmentScrollingPanel.add(
                                attachmentUpperPanel,
                                BorderLayout.NORTH);
                            attachmentUpperPanel
                                .setOpaque(false);
                            {
                                attachmentHelpPanel = new JPanel();
                                BorderLayout attachmentHelpPanelLayout = new BorderLayout();
                                attachmentUpperPanel.add(
                                    attachmentHelpPanel,
                                    BorderLayout.SOUTH);
                                attachmentHelpPanel
                                    .setLayout(attachmentHelpPanelLayout);
                                attachmentHelpPanel
                                    .setOpaque(false);
                                {
                                    attachmentAreaHintLabel = new JLabel();
                                    attachmentHelpPanel
                                        .add(
                                            attachmentAreaHintLabel,
                                            BorderLayout.NORTH);
                                    attachmentAreaHintLabel
                                        .setText("To add attachments, drag files or folders here, or use the above links.");
                                    attachmentAreaHintLabel
                                        .setFont(new java.awt.Font(
                                            "Dialog", 0, 12));
                                    attachmentAreaHintLabel
                                        .setForeground(new java.awt.Color(
                                            150, 150, 150));
                                }
                                {
                                    attachmentAreaReadLabel = new JLabel();
                                    attachmentHelpPanel
                                        .add(
                                            attachmentAreaReadLabel,
                                            BorderLayout.SOUTH);
                                    attachmentAreaReadLabel
                                        .setText("To save attachments, drag them from here, or use the links next to the attachment.");
                                    attachmentAreaReadLabel
                                        .setForeground(new java.awt.Color(
                                            150, 150, 150));
                                    attachmentAreaReadLabel
                                        .setFont(new java.awt.Font(
                                            "Dialog", 0, 12));
                                }
                            }
                            {
                                attachmentsList = new JList();
                                if (storage != null)
                                {
                                    attachmentsModel = new UserMessageAttachmentsModel(
                                        storage, message);
                                    attachmentsList
                                        .setModel(attachmentsModel);
                                }
                                attachmentUpperPanel.add(
                                    attachmentsList,
                                    BorderLayout.NORTH);
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
                        jPanel3.add(inReplyToLabel,
                            BorderLayout.CENTER);
                        inReplyToLabel
                            .setText("Not in reply");
                    }
                }
                {
                    fromLabel = new JLabel();
                    rootPanel.add(fromLabel, "0, 0");
                    fromLabel.setText("From: ");
                }
                {
                    jLabel9 = new JLabel();
                    rootPanel.add(jLabel9, "1, 0");
                }
                {
                    jSeparator1 = new JSeparator();
                    rootPanel
                        .add(jSeparator1, "0, 4, 1, 4");
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
        System.out
            .println("saveButton.actionPerformed, event="
                + evt);
        // TODO add your code for saveButton.actionPerformed
    }
    
}
