package com.trivergia.intouch3.installer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
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
public class Frame2 extends javax.swing.JFrame {
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JButton jButton1;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Frame2 inst = new Frame2();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public Frame2() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			//START >>  jLabel1
			statusLabel = new JLabel();
			getContentPane().add(statusLabel);
			statusLabel.setText("Convergia Installer is getting ready to install Convergia, please wait...");
			statusLabel.setBounds(97, 58, 478, 21);
			//END <<  jLabel1
			//START >>  progressBar
			progressBar = new JProgressBar();
			getContentPane().add(getProgressBar());
			progressBar.setBounds(85, 210, 420, 19);
			progressBar.setIndeterminate(true);
			//END <<  progressBar
			//START >>  jButton1
			jButton1 = new JButton();
			getContentPane().add(jButton1);
			jButton1.setText("Please wait");
			jButton1.setBounds(534, 438, 100, 26);
			jButton1.setEnabled(false);
			//END <<  jButton1
			pack();
			this.setSize(650, 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JLabel getStatusLabel()
	{
		return statusLabel;
	}

}
