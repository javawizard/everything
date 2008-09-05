package net.sf.opengroove.client.ui.frames;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.sf.opengroove.client.com.UserNotificationListener.Priority;


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
public class UserNotificationFrame extends JFrame
{
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JPanel jPanel10;
    private JPanel jPanel9;
    private JPanel jPanel8;
    private JPanel jPanel7;
    private JPanel jPanel6;
    private JPanel jPanel5;
    private JPanel jPanel4;
    private JPanel jPanel3;
    private JPanel jPanel2;
    private JPanel jPanel1;
    private JButton okButton;
    private JLabel priorityLabel;
    private JLabel expiresLabel;
    private JLabel issuedOnLabel;
    private JTextField subjectTextField;
    public UserNotificationFrame(long dateIssued,
        long dateExpires, Priority priority,
        String subject, String message)
    {
        initGUI();
    }
    
    private void initGUI() {
        try {
            {
                BorderLayout thisLayout = new BorderLayout();
                getContentPane().setLayout(thisLayout);
                this.setResizable(false);
                {
                    jPanel1 = new JPanel();
                    BoxLayout jPanel1Layout = new BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS);
                    getContentPane().add(jPanel1, BorderLayout.NORTH);
                    jPanel1.setLayout(jPanel1Layout);
                    {
                        jPanel3 = new JPanel();
                        BorderLayout jPanel3Layout = new BorderLayout();
                        jPanel3.setLayout(jPanel3Layout);
                        jPanel1.add(jPanel3);
                        jPanel3.setPreferredSize(new java.awt.Dimension(213, 32));
                        {
                            jLabel1 = new JLabel();
                            jPanel3.add(jLabel1, BorderLayout.CENTER);
                            jLabel1.setText("Server Notification");
                            jLabel1.setBounds(12, 12, 320, 44);
                            jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
                            jLabel1.setFont(new java.awt.Font("Dialog",1,24));
                        }
                    }
                    {
                        jPanel2 = new JPanel();
                        BorderLayout jPanel2Layout = new BorderLayout();
                        jPanel2.setLayout(jPanel2Layout);
                        jPanel1.add(jPanel2);
                        jPanel2.setPreferredSize(new java.awt.Dimension(64, 21));
                        {
                            jLabel2 = new JLabel();
                            jPanel2.add(jLabel2, BorderLayout.WEST);
                            jLabel2.setText("Subject:");
                            jLabel2.setBounds(12, 69, 100, 26);
                            jLabel2.setFont(new java.awt.Font("Dialog",1,16));
                        }
                    }
                    {
                        jPanel4 = new JPanel();
                        BorderLayout jPanel4Layout = new BorderLayout();
                        jPanel4.setLayout(jPanel4Layout);
                        jPanel1.add(jPanel4);
                        jPanel4.setPreferredSize(new java.awt.Dimension(58, 16));
                        {
                            jLabel3 = new JLabel();
                            jPanel4.add(jLabel3, BorderLayout.WEST);
                            jLabel3.setText("Issued on:");
                            jLabel3.setBounds(12, 94, 100, 20);
                        }
                    }
                    {
                        jPanel5 = new JPanel();
                        BorderLayout jPanel5Layout = new BorderLayout();
                        jPanel5.setLayout(jPanel5Layout);
                        jPanel1.add(jPanel5);
                        jPanel5.setPreferredSize(new java.awt.Dimension(46, 16));
                        {
                            jLabel4 = new JLabel();
                            jPanel5.add(jLabel4, BorderLayout.WEST);
                            jLabel4.setText("Expires:");
                            jLabel4.setBounds(12, 114, 100, 20);
                        }
                    }
                    {
                        jPanel6 = new JPanel();
                        BorderLayout jPanel6Layout = new BorderLayout();
                        jPanel6.setLayout(jPanel6Layout);
                        jPanel1.add(jPanel6);
                        jPanel6.setPreferredSize(new java.awt.Dimension(44, 16));
                        {
                            jLabel5 = new JLabel();
                            jPanel6.add(jLabel5, BorderLayout.WEST);
                            jLabel5.setText("Priority:");
                            jLabel5.setBounds(12, 134, 100, 20);
                            jLabel5.setAutoscrolls(true);
                        }
                    }
                    {
                        jPanel7 = new JPanel();
                        BorderLayout jPanel7Layout = new BorderLayout();
                        jPanel7.setLayout(jPanel7Layout);
                        jPanel1.add(jPanel7);
                        jPanel7.setPreferredSize(new java.awt.Dimension(344, 16));
                        {
                            subjectTextField = new JTextField();
                            jPanel7.add(subjectTextField, BorderLayout.CENTER);
                            subjectTextField.setBounds(112, 62, 220, 32);
                            subjectTextField.setOpaque(false);
                            subjectTextField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                            subjectTextField.setEditable(false);
                        }
                    }
                    {
                        jPanel8 = new JPanel();
                        BorderLayout jPanel8Layout = new BorderLayout();
                        jPanel8.setLayout(jPanel8Layout);
                        jPanel1.add(jPanel8);
                        jPanel8.setPreferredSize(new java.awt.Dimension(52, 16));
                        {
                            issuedOnLabel = new JLabel();
                            jPanel8.add(issuedOnLabel, BorderLayout.CENTER);
                            issuedOnLabel.setText("Unknown");
                            issuedOnLabel.setBounds(111, 94, 221, 19);
                            issuedOnLabel.setFont(new java.awt.Font("Dialog",0,12));
                        }
                    }
                    {
                        jPanel9 = new JPanel();
                        BorderLayout jPanel9Layout = new BorderLayout();
                        jPanel9.setLayout(jPanel9Layout);
                        jPanel1.add(jPanel9);
                        jPanel9.setPreferredSize(new java.awt.Dimension(52, 16));
                        {
                            expiresLabel = new JLabel();
                            jPanel9.add(expiresLabel, BorderLayout.CENTER);
                            expiresLabel.setText("Unknown");
                            expiresLabel.setBounds(112, 116, 220, 16);
                            expiresLabel.setFont(new java.awt.Font("Dialog",0,12));
                        }
                    }
                    {
                        jPanel10 = new JPanel();
                        BorderLayout jPanel10Layout = new BorderLayout();
                        jPanel10.setLayout(jPanel10Layout);
                        jPanel1.add(jPanel10);
                        jPanel10.setPreferredSize(new java.awt.Dimension(52, 16));
                        {
                            priorityLabel = new JLabel();
                            jPanel10.add(priorityLabel, BorderLayout.CENTER);
                            priorityLabel.setText("Unknown");
                            priorityLabel.setBounds(112, 136, 220, 16);
                            priorityLabel.setFont(new java.awt.Font("Dialog",0,12));
                        }
                    }
                    {
                        okButton = new JButton();
                        jPanel1.add(okButton);
                        okButton.setText("OK");
                        okButton.setBounds(293, 348, 51, 26);
                    }
                }
            }
            {
                this.setSize(350, 400);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
