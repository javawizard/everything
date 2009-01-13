package net.sf.opengroove.client.ui;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


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
public class InviteToWorkspaceDialog extends javax.swing.JDialog {
	private JLabel jLabel1;
	private JLabel workspaceLabel;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JTextArea messageTextArea;
	private JButton cancelButton;
	private JButton sendButton;
	private JScrollPane jScrollPane1;
	private JTextField userTextField;
	private JLabel workspaceIdLabel;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				InviteToWorkspaceDialog inst = new InviteToWorkspaceDialog(frame);
				inst.setVisible(true);
			}
		});
	}
	
	public InviteToWorkspaceDialog(JFrame frame) {
		super(frame,true);
		initGUI();
	}
	
	private void initGUI() {
		try {
			//START >>  this
			getContentPane().setLayout(null);
			//START >>  jLabel1
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setText("You are inviting a user to:");
			jLabel1.setBounds(12, 12, 377, 16);
			//END <<  jLabel1
			//START >>  workspaceLabel
			workspaceLabel = new JLabel();
			getContentPane().add(getWorkspaceLabel());
			workspaceLabel.setText("unknown");
			workspaceLabel.setBounds(24, 34, 305, 16);
			//END <<  workspaceLabel
			//START >>  jLabel2
			jLabel2 = new JLabel();
			getContentPane().add(jLabel2);
			jLabel2.setText("This workspace's id is:");
			jLabel2.setBounds(12, 62, 352, 16);
			//END <<  jLabel2
			//START >>  workspaceIdLabel
			workspaceIdLabel = new JLabel();
			getContentPane().add(getWorkspaceIdLabel());
			workspaceIdLabel.setText("unknown");
			workspaceIdLabel.setBounds(24, 84, 383, 16);
			//END <<  workspaceIdLabel
			//START >>  jLabel3
			jLabel3 = new JLabel();
			getContentPane().add(jLabel3);
			jLabel3.setText("Type the username of the user you wish to invite to this workspace:");
			jLabel3.setBounds(12, 112, 407, 16);
			//END <<  jLabel3
			//START >>  userTextField
			userTextField = new JTextField();
			getContentPane().add(getUserTextField());
			userTextField.setBounds(24, 134, 365, 20);
			//END <<  userTextField
			//START >>  jLabel4
			jLabel4 = new JLabel();
			getContentPane().add(jLabel4);
			jLabel4.setText("Type a message to send along with the invite:");
			jLabel4.setBounds(12, 166, 377, 16);
			//END <<  jLabel4
			//START >>  jScrollPane1
			jScrollPane1 = new JScrollPane();
			getContentPane().add(jScrollPane1);
			jScrollPane1.setBounds(24, 194, 365, 169);
			//START >>  messageTextArea
			messageTextArea = new JTextArea();
			jScrollPane1.setViewportView(getMessageTextArea());
			messageTextArea.setBounds(24, 194, 365, 169);
			messageTextArea.setLineWrap(true);
			messageTextArea.setWrapStyleWord(true);
			//END <<  messageTextArea
			//END <<  jScrollPane1
			//START >>  sendButton
			sendButton = new JButton();
			getContentPane().add(getSendButton());
			sendButton.setText("Send");
			sendButton.setBounds(283, 395, 63, 26);
			//END <<  sendButton
			//START >>  cancelButton
			cancelButton = new JButton();
			getContentPane().add(getCancelButton());
			cancelButton.setText("Cancel");
			cancelButton.setBounds(346, 395, 73, 26);
			//END <<  cancelButton
			//END <<  this
			this.setSize(435, 457);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JLabel getWorkspaceLabel() {
		return workspaceLabel;
	}
	
	public JLabel getWorkspaceIdLabel() {
		return workspaceIdLabel;
	}
	
	public JTextField getUserTextField() {
		return userTextField;
	}
	
	public JTextArea getMessageTextArea() {
		return messageTextArea;
	}
	
	public JButton getSendButton() {
		return sendButton;
	}
	
	public JButton getCancelButton() {
		return cancelButton;
	}

}
