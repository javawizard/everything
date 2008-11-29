package net.sf.opengroove.client.ui.frames;

import com.jidesoft.swing.JideButton;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.swing.text.html.HTMLEditorKit;

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
public class ComposeMessageFrame extends javax.swing.JFrame
{
    private JPanel rootPanel;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JPanel messageTopPanel;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JScrollPane jScrollPane1;
    private JPanel attachmentsPanel;
    private JButton cancelButton;
    private JButton saveButton;
    private JLabel jLabel7;
    private JButton sendButton;
    private JPanel buttonPanel;
    private JLabel jLabel6;
    private JideButton addFolderButton;
    private JideButton addFileButton;
    private JPanel jPanel2;
    private JPanel recipientsPanel;
    private JPanel jPanel1;
    private JEditorPane messageArea;
    private JTextField subjectField;
    private JLabel jLabel1;
    private JideButton addRecipientButton;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                ComposeMessageFrame inst = new ComposeMessageFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public ComposeMessageFrame()
    {
        super();
        initGUI();
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
                    rootPanel.add(jLabel1, "0, 0");
                    jLabel1.setText("To: ");
                }
                {
                    jLabel2 = new JLabel();
                    rootPanel.add(jLabel2, "0, 1");
                    jLabel2.setText("Subject: ");
                }
                {
                    subjectField = new JTextField();
                    rootPanel.add(subjectField, "1, 1");
                }
                {
                    jLabel3 = new JLabel();
                    rootPanel.add(jLabel3, "0, 2");
                    jLabel3.setText("Message: ");
                }
                {
                    messageTopPanel = new JPanel();
                    BoxLayout messageTopPanelLayout = new BoxLayout(
                        messageTopPanel,
                        javax.swing.BoxLayout.X_AXIS);
                    rootPanel.add(

                    messageTopPanel, "1, 2");
                    messageTopPanel
                        .setLayout(messageTopPanelLayout);
                }
                {
                    messageArea = new JEditorPane();
                    HTMLEditorKit messageKit = new HTMLEditorKit();
                    messageArea.setEditorKit(messageKit);
                    rootPanel.add(new JScrollPane(
                        messageArea), "0, 3, 1, 3");
                    messageTopPanel.add(Box
                        .createHorizontalGlue());
                }
                {
                    jPanel1 = new JPanel();
                    BorderLayout jPanel1Layout = new BorderLayout();
                    jPanel1.setLayout(jPanel1Layout);
                    rootPanel.add(jPanel1, "1, 0");
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
                    rootPanel.add(jLabel4, "0, 4");
                    jLabel4.setText("Attachments: ");
                }
                {
                    jPanel2 = new JPanel();
                    BoxLayout jPanel2Layout = new BoxLayout(
                        jPanel2,
                        javax.swing.BoxLayout.X_AXIS);
                    rootPanel.add(jPanel2, "1, 4");
                    jPanel2.setLayout(jPanel2Layout);
                    {
                        jLabel5 = new JLabel();
                        jPanel2.add(jLabel5);
                        jLabel5
                            .setText("You can attach up to 2GB.");
                        jLabel5.setFont(new java.awt.Font(
                            "Dialog", 0, 12));
                    }
                    {
                        jLabel6 = new JLabel();
                        jPanel2.add(jLabel6);
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
                        jPanel2.add(addFileButton);
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
                        jPanel2.add(addFolderButton);
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
                }
                {
                    buttonPanel = new JPanel();
                    BoxLayout buttonPanelLayout = new BoxLayout(
                        buttonPanel,
                        javax.swing.BoxLayout.X_AXIS);
                    rootPanel
                        .add(buttonPanel, "0, 6, 1, 6");
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
                        "0, 5, 1, 5");
                    {
                        attachmentsPanel = new JPanel();
                        jScrollPane1
                            .setViewportView(attachmentsPanel);
                        BoxLayout attachmentsPanelLayout = new BoxLayout(
                            attachmentsPanel,
                            javax.swing.BoxLayout.Y_AXIS);
                        attachmentsPanel
                            .setLayout(attachmentsPanelLayout);
                        attachmentsPanel
                            .setBackground(new java.awt.Color(
                                255, 255, 255));
                    }
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
    
}
