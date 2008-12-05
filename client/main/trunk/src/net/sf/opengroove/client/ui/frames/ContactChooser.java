package net.sf.opengroove.client.ui.frames;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jidesoft.swing.JideButton;

import net.sf.opengroove.client.storage.Contact;
import net.sf.opengroove.client.storage.ContactStatus;
import net.sf.opengroove.client.storage.Storage;

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
public class ContactChooser extends javax.swing.JDialog
{
    private JPanel rootPanel;
    private JPanel lowerPanel;
    private JScrollPane jScrollPane1;
    private JPanel contactsPanel;
    private JPanel jPanel1;
    private JSeparator jSeparator1;
    private JLabel mainLabel;
    private JButton cancelButton;
    private JPanel lowerRightPanel;
    
    /**
     * Auto-generated main method to display this JDialog
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame frame = new JFrame();
                ContactChooser inst = new ContactChooser(
                    frame);
                inst.setVisible(true);
            }
        });
    }
    
    public ContactChooser(JFrame frame)
    {
        super(frame, true);
        initGUI();
    }
    
    private void initGUI()
    {
        try
        {
            {
                this
                    .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }
            {
                rootPanel = new JPanel();
                TableLayout rootPanelLayout = new TableLayout(
                    new double[][] {
                        { 20.0, TableLayout.FILL, 5.0 },
                        { 20.0, TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.FILL,
                            TableLayout.PREFERRED, 5.0 } });
                rootPanelLayout.setHGap(5);
                rootPanelLayout.setVGap(5);
                getContentPane().add(rootPanel,
                    BorderLayout.CENTER);
                rootPanel.setLayout(rootPanelLayout);
                {
                    lowerPanel = new JPanel();
                    BorderLayout lowerPanelLayout = new BorderLayout();
                    rootPanel.add(lowerPanel, "1, 4");
                    lowerPanel.setLayout(lowerPanelLayout);
                    {
                        lowerRightPanel = new JPanel();
                        BoxLayout lowerRightPanelLayout = new BoxLayout(
                            lowerRightPanel,
                            javax.swing.BoxLayout.X_AXIS);
                        lowerPanel.add(lowerRightPanel,
                            BorderLayout.EAST);
                        lowerRightPanel
                            .setLayout(lowerRightPanelLayout);
                        {
                            cancelButton = new JButton();
                            lowerRightPanel
                                .add(cancelButton);
                            cancelButton.setText("Cancel");
                            cancelButton
                                .addActionListener(new ActionListener()
                                {
                                    public void actionPerformed(
                                        ActionEvent evt)
                                    {
                                        cancelButtonActionPerformed(evt);
                                    }
                                });
                        }
                    }
                }
                {
                    mainLabel = new JLabel();
                    rootPanel.add(mainLabel, "1, 1");
                    mainLabel.setText("Choose a contact.");
                }
                {
                    jSeparator1 = new JSeparator();
                    rootPanel.add(jSeparator1, "1, 2");
                }
                {
                    jScrollPane1 = new JScrollPane();
                    rootPanel.add(jScrollPane1, "1, 3");
                    {
                        jPanel1 = new JPanel();
                        BorderLayout jPanel1Layout = new BorderLayout();
                        jScrollPane1
                            .setViewportView(jPanel1);
                        jPanel1.setLayout(jPanel1Layout);
                        {
                            contactsPanel = new JPanel();
                            BoxLayout contactsPanelLayout = new BoxLayout(
                                contactsPanel,
                                javax.swing.BoxLayout.Y_AXIS);
                            jPanel1.add(contactsPanel,
                                BorderLayout.NORTH);
                            contactsPanel
                                .setLayout(contactsPanelLayout);
                        }
                    }
                }
            }
            this.setSize(312, 381);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void cancelButtonActionPerformed(ActionEvent evt)
    {
        dispose();
    }
    
    private String chosenContact = null;
    
    public static String chooseContact(JFrame owner,
        Storage storage, String header)
    {
        final ContactChooser chooser = new ContactChooser(
            owner);
        chooser.mainLabel.setText(header);
        for (final Contact contact : storage.getLocalUser()
            .getContacts().isolate())
        {
            ContactStatus status = contact.getStatus();
            if (status == null)
                continue;
            if (!status.isKnown())
                continue;
            if (status.isNonexistant())
                continue;
            /*
             * If we get here, then the contact exists. We'll go ahead and add a
             * link for the contact.
             */
            JideButton button = new JideButton(contact
                .getDisplayName());
            button.setButtonStyle(3);// hyperlink button
            chooser.contactsPanel.add(button);
            button.addActionListener(new ActionListener()
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    chooser.chosenContact = contact
                        .getUserid();
                    chooser.hide();
                }
            });
        }
        if (chooser.contactsPanel.getComponentCount() == 0)
        {
            chooser.contactsPanel.add(new JLabel(
                "No contacts"));
        }
        chooser.setLocationRelativeTo(owner);
        chooser.show();
        return chooser.chosenContact;
    }
    
}
