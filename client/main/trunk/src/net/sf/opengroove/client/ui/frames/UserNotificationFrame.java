package net.sf.opengroove.client.ui.frames;
import javax.swing.BorderFactory;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
                getContentPane().setLayout(null);
                this.setResizable(false);
                {
                    jLabel1 = new JLabel();
                    getContentPane().add(jLabel1);
                    jLabel1.setText("Server Notification");
                    jLabel1.setBounds(12, 12, 320, 44);
                    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
                    jLabel1.setFont(new java.awt.Font("Dialog",1,24));
                }
                {
                    jLabel2 = new JLabel();
                    getContentPane().add(jLabel2);
                    jLabel2.setText("Subject:");
                    jLabel2.setBounds(12, 68, 96, 20);
                    jLabel2.setSize(100, 26);
                    jLabel2.setFont(new java.awt.Font("Dialog",1,16));
                }
                {
                    jLabel3 = new JLabel();
                    getContentPane().add(jLabel3);
                    jLabel3.setText("Issued on:");
                    jLabel3.setBounds(12, 94, 100, 20);
                }
                {
                    jLabel4 = new JLabel();
                    getContentPane().add(jLabel4);
                    jLabel4.setText("Expires:");
                    jLabel4.setBounds(12, 114, 100, 20);
                }
                {
                    jLabel5 = new JLabel();
                    getContentPane().add(jLabel5);
                    jLabel5.setText("Priority:");
                    jLabel5.setBounds(12, 134, 100, 20);
                    jLabel5.setAutoscrolls(true);
                }
                {
                    subjectTextField = new JTextField();
                    getContentPane().add(subjectTextField);
                    subjectTextField.setBounds(112, 62, 220, 32);
                    subjectTextField.setOpaque(false);
                    subjectTextField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    subjectTextField.setEditable(false);
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
