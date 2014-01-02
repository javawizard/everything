package org.opengroove.g4.client.ui.frames;

import com.jidesoft.swing.JideButton;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import net.sf.opengroove.common.ui.ComponentUtils;

import org.opengroove.g4.client.Statics;

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
public class CreateAccountFrame extends javax.swing.JFrame
{
    private JPanel mainPanel;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JTextField computerField;
    private JButton cancelButton;
    private JButton addButton;
    private JPanel lowerRightPanel;
    private JLabel chooseServerLabel;
    private JPanel lowerPanel;
    private JTextField server;
    private JideButton jideButton1;
    private JPasswordField passwordAgainField;
    private JPasswordField passwordField;
    private JTextField usernameField;
    private JSeparator jSeparator1;
    private JLabel jLabel1;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        Statics.run();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                CreateAccountFrame inst = new CreateAccountFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public CreateAccountFrame()
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
                setSize(400, 300);
                mainPanel = new JPanel();
                getContentPane().add(mainPanel, BorderLayout.CENTER);
                TableLayout mainPanelLayout =
                    new TableLayout(new double[][] {
                        { 6.0, TableLayout.FILL, 6.0, TableLayout.FILL, 6.0 },
                        { 6.0, TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.FILL, TableLayout.PREFERRED } });
                mainPanelLayout.setHGap(5);
                mainPanelLayout.setVGap(5);
                mainPanel.setLayout(mainPanelLayout);
                {
                    jLabel1 = new JLabel();
                    mainPanel.add(jLabel1, "1, 1, 3, 1");
                    jLabel1.setText("New Account");
                    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
                    jLabel1.setFont(new java.awt.Font("Arial", 1, 26));
                }
                {
                    jSeparator1 = new JSeparator();
                    mainPanel.add(jSeparator1, "1,2,3,2");
                }
                {
                    jLabel2 = new JLabel();
                    mainPanel.add(jLabel2, "1, 3");
                    jLabel2.setText("Username:");
                }
                {
                    usernameField = new JTextField();
                    usernameField
                        .setToolTipText(ComponentUtils
                            .htmlTipWrap("Choose a username that you'd like to use. This can only contain letters and numbers."));
                    mainPanel.add(getUsernameField(), "3, 3");
                }
                {
                    jLabel3 = new JLabel();
                    mainPanel.add(jLabel3, "1, 5");
                    jLabel3.setText("Password:");
                }
                {
                    jLabel4 = new JLabel();
                    mainPanel.add(jLabel4, "1, 6");
                    jLabel4.setText("Password again:");
                }
                {
                    passwordField = new JPasswordField();
                    passwordField.setToolTipText(ComponentUtils
                        .htmlTipWrap("Choose a password for your account. You will "
                            + "need to use this password whenever you start G4, "
                            + "and you will need it to use your account on "
                            + "other computers."));
                    mainPanel.add(passwordField, "3, 5");
                }
                {
                    passwordAgainField = new JPasswordField();
                    passwordAgainField
                        .setToolTipText(ComponentUtils
                            .htmlTipWrap("Enter your password again. This makes sure that "
                                + "you didn't accidentally type your password incorrectly."));
                    mainPanel.add(passwordAgainField, "3, 6");
                }
                {
                    jideButton1 = new JideButton();
                    jideButton1
                        .setToolTipText(ComponentUtils
                            .htmlTipWrap("OpenGroove.org provides a server that you can use. By "
                                + "default, your account will be created on this server."
                                + " If you run your own server and would prefer that G4 "
                                + "send its data through your server, you can use this "
                                + "link to choose another server."));
                    mainPanel.add(jideButton1, "1, 7, 3, 7");
                    jideButton1.setText("Choose a server (Advanced users only)");
                    jideButton1.setAlwaysShowHyperlink(true);
                    jideButton1.setButtonStyle(3);
                    jideButton1.setForeground(new java.awt.Color(0, 0, 255));
                    jideButton1.setHorizontalAlignment(SwingConstants.LEFT);
                    jideButton1.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent evt)
                        {
                            jideButton1ActionPerformed(evt);
                        }
                    });
                }
                {
                    chooseServerLabel = new JLabel();
                    mainPanel.add(chooseServerLabel, "1, 8");
                    chooseServerLabel.setText("Server:");
                    chooseServerLabel.setVisible(false);
                }
                {
                    server = new JTextField();
                    mainPanel.add(server, "3, 8");
                    server.setText("localhost");
                    server.setVisible(false);
                }
                {
                    lowerPanel = new JPanel();
                    BorderLayout lowerPanelLayout = new BorderLayout();
                    mainPanel.add(lowerPanel, "3, 10");
                    lowerPanel.setLayout(lowerPanelLayout);
                    {
                        lowerRightPanel = new JPanel();
                        BoxLayout lowerRightPanelLayout =
                            new BoxLayout(lowerRightPanel, javax.swing.BoxLayout.X_AXIS);
                        lowerPanel.add(lowerRightPanel, BorderLayout.EAST);
                        lowerRightPanel.setLayout(lowerRightPanelLayout);
                        {
                            addButton = new JButton();
                            lowerRightPanel.add(getAddButtonx());
                            addButton.setText("Add");
                        }
                        {
                            cancelButton = new JButton();
                            lowerRightPanel.add(getCancelButton());
                            cancelButton.setText("Cancel");
                        }
                    }
                }
                {
                    jLabel5 = new JLabel();
                    mainPanel.add(jLabel5, "1, 4");
                    jLabel5.setText("Computer:");
                }
                {
                    computerField = new JTextField();
                    computerField
                        .setToolTipText(ComponentUtils
                            .htmlTipWrap("Choose a name you'd like for this computer. If you"
                                + " decide to use G4 on multiple computers, then"
                                + " this computer will be known by the name that you type here."));
                    mainPanel.add(getComputerField(), "3, 4");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public JTextField getUsernameField()
    {
        return usernameField;
    }
    
    public JPasswordField getPasswordField()
    {
        return passwordField;
    }
    
    public JPasswordField getPasswordAgainField()
    {
        return passwordAgainField;
    }
    
    public JTextField getServer()
    {
        return server;
    }
    
    public JButton getAddButtonx()
    {
        return addButton;
    }
    
    public JButton getCancelButton()
    {
        return cancelButton;
    }
    
    private void jideButton1ActionPerformed(ActionEvent evt)
    {
        chooseServerLabel.show();
        server.show();
    }
    
    public JTextField getComputerField()
    {
        return computerField;
    }
    
}
