package org.opengroove.g4.client.ui.frames;

import com.jidesoft.swing.JideButton;
import com.jidesoft.tooltip.BalloonTip;
import com.jidesoft.tooltip.shapes.RoundedRectangularBalloonShape;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;

import javax.swing.SwingUtilities;

import org.opengroove.g4.client.Statics;
import org.opengroove.g4.client.ui.SVGConstraints;
import org.opengroove.g4.client.ui.SVGPanel;

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
public class LoginFrame extends javax.swing.JFrame
{
    static
    {
        Statics.run();
    }
    private SVGPanel fillContainer;
    private JideButton helpButton;
    private JLabel useridLabel;
    private BalloonTip capsLockBalloon;
    private JButton cancelButton;
    private JLabel jLabel3;
    private JButton loginButton;
    private JLabel jLabel2;
    private JButton newAccountButton;
    private JPanel southPanel;
    private JideButton passwordHintButton;
    private JPanel passwordHintPanel;
    private JCheckBox rememberPasswordCheckbox;
    private JPanel passwordFooterPanel;
    private JPasswordField passwordField;
    private JLabel jLabel1;
    private JPanel passwordHeaderPanel;
    private JPanel useridPanel;
    private JPanel northContents;
    private JLabel iconLabel;
    protected String passwordHint;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                LoginFrame inst = new LoginFrame();
                inst.getIconLabel().setIcon(new ImageIcon("trayicon48.png"));
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public LoginFrame()
    {
        super("Log in - OpenGroove");
        initGUI();
        passwordField.requestFocusInWindow();
    }
    
    private void initGUI()
    {
        try
        {
            BorderLayout thisLayout = new BorderLayout();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            getContentPane().setLayout(thisLayout);
            {
                fillContainer =
                    new SVGPanel(new String[] {
                        new File("icons/backdrops/loginframe.svg").toURI().toURL()
                            .toString(),
                        new File("icons/backdrops/loginframe-1.svg").toURI().toURL()
                            .toString() },
                        new SVGConstraints[] { new SVGConstraints(true, 0, 0),
                            new SVGConstraints(false, 0, 0) });
                TableLayout fillContainerLayout =
                    new TableLayout(new double[][] {
                        { 12.0, TableLayout.PREFERRED, TableLayout.FILL,
                            TableLayout.PREFERRED },
                        { TableLayout.PREFERRED, TableLayout.PREFERRED,
                            TableLayout.FILL } });
                fillContainerLayout.setHGap(5);
                fillContainerLayout.setVGap(5);
                getContentPane().add(fillContainer, BorderLayout.CENTER);
                fillContainer.setLayout(fillContainerLayout);
                {
                    helpButton = new JideButton();
                    fillContainer.add(helpButton, "3, 0");
                    helpButton.setText("Help");
                    helpButton.setButtonStyle(JideButton.HYPERLINK_STYLE);
                    helpButton.setFont(new java.awt.Font("Dialog", 0, 12));
                    helpButton.setForeground(new java.awt.Color(0, 0, 255));
                    helpButton.setAlwaysShowHyperlink(true);
                }
                {
                    iconLabel = new JLabel();
                    fillContainer.add(iconLabel, "1, 1");
                }
                {
                    northContents = new JPanel();
                    BoxLayout contentsLayout =
                        new BoxLayout(northContents, javax.swing.BoxLayout.Y_AXIS);
                    fillContainer.add(northContents, "2, 1, 3, 2");
                    northContents.setLayout(contentsLayout);
                    northContents.setOpaque(false);
                    northContents
                        .setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                    {
                        useridPanel = new JPanel();
                        BoxLayout useridPanelLayout =
                            new BoxLayout(useridPanel, javax.swing.BoxLayout.X_AXIS);
                        northContents.add(useridPanel);
                        useridPanel.setLayout(useridPanelLayout);
                        useridPanel.setOpaque(false);
                        {
                            useridLabel = new JLabel();
                            useridPanel.add(getUseridLabel());
                            useridLabel.setText("server::username:computer");
                            useridLabel.setFont(new java.awt.Font("Dialog", 1, 12));
                        }
                        useridPanel.add(Box.createHorizontalGlue());
                    }
                    {
                        passwordHeaderPanel = new JPanel();
                        BoxLayout passwordPanelLayout =
                            new BoxLayout(passwordHeaderPanel,
                                javax.swing.BoxLayout.X_AXIS);
                        passwordHeaderPanel.setLayout(passwordPanelLayout);
                        northContents.add(passwordHeaderPanel);
                        passwordHeaderPanel.setOpaque(false);
                        {
                            jLabel1 = new JLabel();
                            passwordHeaderPanel.add(jLabel1);
                            jLabel1.setText("Password:");
                            jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
                        }
                        passwordHeaderPanel.add(Box.createHorizontalGlue());
                    }
                    {
                        passwordField = new JPasswordField()
                        {
                            public Dimension getMaximumSize()
                            {
                                return new Dimension(Integer.MAX_VALUE,
                                    getPreferredSize().height);
                            }
                        };
                        passwordField.addFocusListener(new FocusListener()
                        {
                            
                            @Override
                            public void focusGained(FocusEvent e)
                            {
                                if (passwordField.getToolkit().getLockingKeyState(
                                    KeyEvent.VK_CAPS_LOCK))
                                {
                                    getCapsLockBalloon().show(passwordField,
                                        passwordField.getHeight() / 2,
                                        passwordField.getHeight() / 2);
                                }
                            }
                            
                            @Override
                            public void focusLost(FocusEvent e)
                            {
                                // TODO Auto-generated method stub
                                
                            }
                        });
                        passwordField.addKeyListener(new KeyListener()
                        {
                            
                            @Override
                            public void keyPressed(KeyEvent e)
                            {
                                if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK
                                    && passwordField.getToolkit().getLockingKeyState(
                                        KeyEvent.VK_CAPS_LOCK))
                                {
                                    getCapsLockBalloon().show(passwordField,
                                        passwordField.getHeight() / 2,
                                        passwordField.getHeight() / 2);
                                }
                                else if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK)
                                {
                                    getCapsLockBalloon().hide();
                                }
                            }
                            
                            @Override
                            public void keyTyped(KeyEvent e)
                            {
                                // TODO Auto-generated method stub
                                
                            }
                            
                            @Override
                            public void keyReleased(KeyEvent e)
                            {
                                // TODO Auto-generated method stub
                                
                            }
                            
                        });
                        northContents.add(getPasswordField());
                        passwordField.setBackground(new java.awt.Color(255, 255, 255));
                    }
                    {
                        passwordFooterPanel = new JPanel();
                        BoxLayout passwordFooterPanelLayout =
                            new BoxLayout(passwordFooterPanel,
                                javax.swing.BoxLayout.X_AXIS);
                        northContents.add(passwordFooterPanel);
                        passwordFooterPanel.setLayout(passwordFooterPanelLayout);
                        passwordFooterPanel.setOpaque(false);
                        {
                            rememberPasswordCheckbox = new JCheckBox();
                            passwordFooterPanel.add(getRememberPasswordCheckbox());
                            rememberPasswordCheckbox
                                .setText("Remember my password on this computer");
                            rememberPasswordCheckbox
                                .addActionListener(new ActionListener()
                                {
                                    
                                    @Override
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        JOptionPane
                                            .showMessageDialog(
                                                LoginFrame.this,
                                                "We're still working on this feature. When we've added this feature, you won't see this message anymore.");
                                    }
                                });
                            rememberPasswordCheckbox.setOpaque(false);
                            rememberPasswordCheckbox.setFont(new java.awt.Font(
                                "Dialog", 0, 12));
                        }
                        passwordFooterPanel.add(Box.createHorizontalGlue());
                    }
                    {
                        passwordHintPanel = new JPanel();
                        BoxLayout passwordHintPanelLayout =
                            new BoxLayout(passwordHintPanel,
                                javax.swing.BoxLayout.X_AXIS);
                        northContents.add(passwordHintPanel);
                        passwordHintPanel.setLayout(passwordHintPanelLayout);
                        passwordHintPanel.setOpaque(false);
                        passwordHintPanel.add(Box.createHorizontalGlue());
                        {
                            passwordHintButton = new JideButton();
                            passwordHintPanel.add(getPasswordHintButton());
                            passwordHintButton.setText("Password hint");
                            passwordHintButton.setFont(new java.awt.Font("Dialog", 0,
                                12));
                            passwordHintButton.setForeground(new java.awt.Color(0, 0,
                                255));
                            passwordHintButton.setButtonStyle(3);
                            passwordHintButton.setAlwaysShowHyperlink(true);
                            passwordHintButton.addActionListener(new ActionListener()
                            {
                                
                                @Override
                                public void actionPerformed(ActionEvent e)
                                {
                                    JOptionPane
                                        .showMessageDialog(
                                            LoginFrame.this,
                                            passwordHint == null ? "You do not have a password hint."
                                                : passwordHint);
                                }
                            });
                        }
                    }
                    northContents.add(Box.createVerticalGlue());
                    {
                        southPanel = new JPanel();
                        BoxLayout southPanelLayout =
                            new BoxLayout(southPanel, javax.swing.BoxLayout.X_AXIS);
                        northContents.add(southPanel);
                        southPanel.setLayout(southPanelLayout);
                        southPanel.setOpaque(false);
                        southPanel.setBorder(BorderFactory
                            .createEmptyBorder(4, 4, 4, 4));
                        southPanel.add(Box.createHorizontalGlue());
                        {
                            newAccountButton = new JButton();
                            southPanel.add(getNewAccountButton());
                            newAccountButton.setText("New account");
                        }
                        {
                            jLabel2 = new JLabel();
                            southPanel.add(jLabel2);
                            jLabel2.setText(" ");
                        }
                        {
                            loginButton = new JButton();
                            southPanel.add(getLoginButton());
                            loginButton.setText("Login");
                        }
                        {
                            jLabel3 = new JLabel();
                            southPanel.add(jLabel3);
                            jLabel3.setText(" ");
                        }
                        {
                            cancelButton = new JButton();
                            southPanel.add(getCancelButton());
                            cancelButton.setText("Cancel");
                        }
                    }
                }
            }
            pack();
            this.setSize(377, 212);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public JideButton getHelpButton()
    {
        return helpButton;
    }
    
    public JLabel getIconLabel()
    {
        return iconLabel;
    }
    
    public JLabel getUseridLabel()
    {
        return useridLabel;
    }
    
    public JPasswordField getPasswordField()
    {
        return passwordField;
    }
    
    public JCheckBox getRememberPasswordCheckbox()
    {
        return rememberPasswordCheckbox;
    }
    
    public JideButton getPasswordHintButton()
    {
        return passwordHintButton;
    }
    
    public JButton getNewAccountButton()
    {
        return newAccountButton;
    }
    
    public JButton getLoginButton()
    {
        return loginButton;
    }
    
    public JButton getCancelButton()
    {
        return cancelButton;
    }
    
    private BalloonTip getCapsLockBalloon()
    {
        if (capsLockBalloon == null)
        {
            JLabel balloonLabel =
                new JLabel(
                    "<html><b>Caps lock is on</b><br/>Having caps lock on might cause you<br/>to type your password incorrectly.");
            balloonLabel.setFont(balloonLabel.getFont().deriveFont(Font.PLAIN));
            capsLockBalloon = new BalloonTip(balloonLabel);
            ((RoundedRectangularBalloonShape) capsLockBalloon.getBalloonShape())
                .setCornerSize(5);
            ((RoundedRectangularBalloonShape) capsLockBalloon.getBalloonShape())
                .setArrowLeftRatio(0.70);
            ((RoundedRectangularBalloonShape) capsLockBalloon.getBalloonShape())
                .setArrowRightRatio(0.20);
            ((RoundedRectangularBalloonShape) capsLockBalloon.getBalloonShape())
                .setVertexPosition(0.85);
            ((RoundedRectangularBalloonShape) capsLockBalloon.getBalloonShape())
                .setBalloonSizeRatio(0.73);
            ((RoundedRectangularBalloonShape) capsLockBalloon.getBalloonShape())
                .setPosition(SwingConstants.BOTTOM);
            
        }
        return capsLockBalloon;
    }
    
    public String getPasswordHint()
    {
        return passwordHint;
    }
    
    public void setPasswordHint(String passwordHint)
    {
        this.passwordHint = passwordHint;
    }
    
    private String currentUsername = "";
    
    public String getUserid()
    {
        return currentUsername;
    }
    
    public void setUserid(String userid)
    {
        currentUsername = userid;
        useridLabel.setText(userid);
    }
}
