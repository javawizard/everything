package net.sf.opengroove.client.ui.frames;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

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
public class UseAccountFrame extends javax.swing.JFrame
{
    private JPanel mainPanel;
    private JLabel jLabel2;
    private JTextField useridField;
    private JLabel jLabel3;
    private JTextField computerField;
    private JButton cancelButton;
    private JButton addButton;
    private JPanel lowerRightPanel;
    private JPanel lowerPanel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JSeparator jSeparator1;
    private JLabel jLabel1;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                UseAccountFrame inst = new UseAccountFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public UseAccountFrame()
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
                mainPanel = new JPanel();
                TableLayout mainPanelLayout =
                    new TableLayout(new double[][] {
                        { 6.0, TableLayout.FILL, 6.0, TableLayout.FILL, 6.0 },
                        { 6.0, TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.PREFERRED, TableLayout.FILL,
                            TableLayout.PREFERRED } });
                mainPanelLayout.setHGap(5);
                mainPanelLayout.setVGap(5);
                mainPanel.setLayout(mainPanelLayout);
                getContentPane().add(mainPanel, BorderLayout.CENTER);
                {
                    jLabel1 = new JLabel();
                    mainPanel.add(jLabel1, "1,1,3,1");
                    jLabel1.setText("Use an existing account");
                    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
                    jLabel1.setFont(new java.awt.Font("Arial", 1, 24));
                }
                {
                    jSeparator1 = new JSeparator();
                    mainPanel.add(jSeparator1, "1,2,3,2");
                }
                {
                    jLabel2 = new JLabel();
                    mainPanel.add(jLabel2, "1, 3");
                    jLabel2.setText("Userid:");
                }
                {
                    useridField = new JTextField();
                    useridField
                        .setToolTipText(ComponentUtils
                            .htmlTipWrap("Your userid is your server, two : characters, "
                                + "and your username. For example, \"trivergia.com::javawizard\" "
                                + "is a valid userid. If you didn't specify a server "
                                + "when you created your account, then use your username "
                                + "instead, or use the server \"opengroove.org\"."));
                    mainPanel.add(getUseridField(), "3, 3");
                }
                {
                    passwordLabel = new JLabel();
                    mainPanel.add(passwordLabel, "1, 5");
                    passwordLabel.setText("Password:");
                }
                {
                    passwordField = new JPasswordField();
                    passwordField.setToolTipText(ComponentUtils
                        .htmlTipWrap("Enter the password you used when you "
                            + "created your account."));
                    mainPanel.add(passwordField, "3, 5");
                }
                {
                    lowerPanel = new JPanel();
                    BorderLayout lowerPanelLayout = new BorderLayout();
                    mainPanel.add(lowerPanel, "3, 7");
                    lowerPanel.setLayout(lowerPanelLayout);
                    {
                        lowerRightPanel = new JPanel();
                        BoxLayout lowerRightPanelLayout =
                            new BoxLayout(lowerRightPanel, javax.swing.BoxLayout.X_AXIS);
                        lowerPanel.add(lowerRightPanel, BorderLayout.EAST);
                        lowerRightPanel.setLayout(lowerRightPanelLayout);
                        {
                            addButton = new JButton();
                            lowerRightPanel.add(addButton);
                            addButton.setText("Add");
                        }
                        {
                            cancelButton = new JButton();
                            lowerRightPanel.add(cancelButton);
                            cancelButton.setText("Cancel");
                        }
                    }
                }
                {
                    jLabel3 = new JLabel();
                    mainPanel.add(jLabel3, "1, 4");
                    jLabel3.setText("Computer:");
                }
                {
                    computerField = new JTextField();
                    computerField
                        .setToolTipText(ComponentUtils
                            .htmlTipWrap("The name that you'd like this computer to be known "
                                + "as. Other users will be able to see the online "
                                + "status of each of your computers, and this is "
                                + "the name that they will see for this computer."));
                    mainPanel.add(getComputerField(), "3, 4");
                }
            }
            setSize(400, 300);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public JTextField getUseridField()
    {
        return useridField;
    }
    
    public JPasswordField getPasswordField()
    {
        return passwordField;
    }
    
    public JTextField getComputerField()
    {
        return computerField;
    }
    
}
