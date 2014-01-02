package com.trivergia.intouch3.installer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

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
public class Frame3 extends javax.swing.JFrame
{
	private JButton jButton1;

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
				Frame3 inst = new Frame3();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public Frame3()
	{
		super();
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			// START >> jButton1
			jButton1 = new JButton();
			jButton1.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			});
			getContentPane().add(jButton1);
			jButton1.setText("Close");
			jButton1.setBounds(568, 438, 66, 26);
			// START >> jLabel1
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setText("<html>Convergia has finished installing. You can start Convergia from<br/>your desktop. If you want Convergia to start when you log in to your computer, you can<br/>move the icon for Convergia from the desktop to your startup folder in the start menu.<br/>For more info about doing this, visit http://static.trivergia.com/intouch3");
			jLabel1.setBounds(73, 62, 500, 77);
			// END << jLabel1
			// END << jButton1
			pack();
			this.setSize(650, 500);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
