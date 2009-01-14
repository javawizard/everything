package net.sf.opengroove.client;
import java.awt.BorderLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

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
public class WelcomeFirstTimeFrame extends javax.swing.JFrame {
	private JLabel mainLabel;
	private JToggleButton okButton;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WelcomeFirstTimeFrame inst = new WelcomeFirstTimeFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public WelcomeFirstTimeFrame() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			{
				mainLabel = new JLabel();
				getContentPane().add(mainLabel);
				mainLabel.setText("<html>Welcome to OpenGroove! Before you<br/>can start using OpenGroove, you need to create an<br/>account. After you click OK, a window will open, asking<br/>you for your account information. If you already<br/>have an account, just enter your username and<br/>password. If you don't have an account, follow<br/>the directions for creating a new account.<br/><br/>NOTE: You must <u>not</u> use one account accross<br/>multiple computers. ");
				mainLabel.setBounds(12, 12, 360, 204);
				mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
			}
			{
				okButton = new JToggleButton();
				getContentPane().add(getOkButton());
				okButton.setText("ok");
				okButton.setBounds(336, 238, 48, 26);
			}
			pack();
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JToggleButton getOkButton() {
		return okButton;
	}

}
