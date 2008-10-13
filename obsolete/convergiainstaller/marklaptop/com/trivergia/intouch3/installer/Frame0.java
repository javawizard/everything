package com.trivergia.intouch3.installer;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

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
public class Frame0 extends javax.swing.JFrame {
	private JLabel jLabel1;
	private JButton jButton1;
	private JProgressBar jProgressBar1;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Frame0 inst = new Frame0();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public Frame0() {
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
			jLabel1.setText("Please wait while the Convergia Installer gets ready to install Convergia...");
			jLabel1.setBounds(77, 50, 538, 28);
			//END <<  jLabel1
			//START >>  jButton1
			jButton1 = new JButton();
			getContentPane().add(jButton1);
			jButton1.setText("Please wait");
			jButton1.setBounds(518, 438, 116, 26);
			jButton1.setEnabled(false);
			//END <<  jButton1
			//START >>  jProgressBar1
			jProgressBar1 = new JProgressBar();
			getContentPane().add(jProgressBar1);
			jProgressBar1.setBounds(89, 237, 416, 16);
			jProgressBar1.setIndeterminate(true);
			jProgressBar1.setFocusable(false);
			jProgressBar1.setMaximum(500);
			//END <<  jProgressBar1
			pack();
			this.setSize(650, 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
