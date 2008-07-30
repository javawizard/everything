package net.sf.opengroove.client.ui;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.WindowConstants;
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
public class ReceiveWorkspaceInviteFrame extends javax.swing.JFrame {
	private JLabel jLabel1;
	private JLabel fromLabel;
	private JLabel workspaceIdLabel;
	private JLabel jLabel2;
	private JButton rejectButton;
	private JButton acceptButton;
	private JScrollPane jScrollPane1;
	private JTextArea inviteTextArea;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ReceiveWorkspaceInviteFrame inst = new ReceiveWorkspaceInviteFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public ReceiveWorkspaceInviteFrame() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			//START >>  jLabel1
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setText("You have received an invitation to a workspace from:");
			jLabel1.setBounds(12, 12, 373, 23);
			//END <<  jLabel1
			//START >>  fromLabel
			fromLabel = new JLabel();
			getContentPane().add(getFromLabel());
			fromLabel.setText("unknown");
			fromLabel.setBounds(24, 35, 295, 18);
			//END <<  fromLabel
			//START >>  jScrollPane1
			jScrollPane1 = new JScrollPane();
			getContentPane().add(jScrollPane1);
			jScrollPane1.setBounds(36, 112, 337, 129);
			//START >>  inviteTextArea
			inviteTextArea = new JTextArea();
			jScrollPane1.setViewportView(getInviteTextArea());
			inviteTextArea.setBounds(36, 112, 337, 129);
			inviteTextArea.setEditable(false);
			inviteTextArea.setOpaque(false);
			//END <<  inviteTextArea
			//END <<  jScrollPane1
			//START >>  acceptButton
			acceptButton = new JButton();
			getContentPane().add(getAcceptButton());
			acceptButton.setText("Accept");
			acceptButton.setBounds(253, 320, 74, 26);
			//END <<  acceptButton
			//START >>  rejectButton
			rejectButton = new JButton();
			getContentPane().add(getRejectButton());
			rejectButton.setText("Reject");
			rejectButton.setBounds(327, 320, 70, 26);
			//END <<  rejectButton
			//START >>  jLabel2
			jLabel2 = new JLabel();
			getContentPane().add(jLabel2);
			jLabel2.setText("The workspace's id is:");
			jLabel2.setBounds(12, 65, 127, 16);
			//END <<  jLabel2
			//START >>  workspaceIdLabel
			workspaceIdLabel = new JLabel();
			getContentPane().add(getWorkspaceIdLabel());
			workspaceIdLabel.setText("unknown");
			workspaceIdLabel.setBounds(24, 85, 361, 16);
			workspaceIdLabel.setSize(361, 17);
			//END <<  workspaceIdLabel
			pack();
			this.setSize(413, 382);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JLabel getFromLabel() {
		return fromLabel;
	}
	
	public JTextArea getInviteTextArea() {
		return inviteTextArea;
	}
	
	public JButton getAcceptButton() {
		return acceptButton;
	}
	
	public JButton getRejectButton() {
		return rejectButton;
	}
	
	public JLabel getWorkspaceIdLabel() {
		return workspaceIdLabel;
	}

}
