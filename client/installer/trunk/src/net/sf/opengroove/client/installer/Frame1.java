package net.sf.opengroove.client.installer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
public class Frame1 extends javax.swing.JFrame
{
	private File installFile;

	public File getInstallFile()
	{
		return installFile;
	}

	private JTextArea jTextArea1;

	private JTextField fileTextField;

	private JButton browseButton;

	private JButton installButton;

	private JFileChooser installFolderChooser;

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
				Frame1 inst = new Frame1();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public Frame1()
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
			// START >> jTextArea1
			jTextArea1 = new JTextArea();
			getContentPane().add(jTextArea1);
			jTextArea1
					.setText("Welcome to the OpenGroove Installer. Choose the following " +
							"options, make sure you are connected to the internet, and " +
							"click \"install\". The latest version of OpenGroove will be " +
							"downloaded and installed in the folder you specify below." +
							"\n\nIf you are using Windows Vista, you will need to select " +
							"a folder that is NOT in your Program Files folder.\n\n" +
							"OpenGroove will install for the current user ONLY. If you " +
							"need to use OpenGroove for multiple computer user accounts, " +
							"visit www.opengroove.org/install-all");
			jTextArea1.setBounds(82, 30, 481, 187);
			jTextArea1.setFont(new java.awt.Font("Dialog", 1, 12));
			jTextArea1.setOpaque(false);
			jTextArea1.setEditable(false);
			jTextArea1.setFocusable(false);
			jTextArea1.setLineWrap(true);
			jTextArea1.setWrapStyleWord(true);
			// END << jTextArea1
			// START >> fileTextField
			fileTextField = new JTextField();
			getContentPane().add(fileTextField);
			fileTextField.setBounds(205, 229, 269, 20);
			fileTextField.setEditable(false);
			// END << fileTextField
			// START >> browseButton
			browseButton = new JButton();
			browseButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					getInstallFolderChooser().showDialog(Frame1.this, null);
					installFile = getInstallFolderChooser().getSelectedFile();
					if (installFile == null)
						return;
					try
					{
						fileTextField.setText(installFile.getCanonicalPath());
					} catch (IOException e1)
					{
					}
				}
			});
			getContentPane().add(getBrowseButton());
			browseButton.setText("Choose a folder");
			browseButton.setBounds(486, 229, 123, 20);
			// END << browseButton
			// START >> jLabel1
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setText("Folder to install in:");
			jLabel1.setBounds(77, 229, 122, 20);
			// END << jLabel1
			// START >> installButton
			installButton = new JButton();
			getContentPane().add(getInstallButton());
			installButton.setText("Install");
			installButton.setBounds(566, 438, 68, 26);
			// END << installButton
			pack();
			this.setSize(650, 500);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JButton getBrowseButton()
	{
		return browseButton;
	}

	public JButton getInstallButton()
	{
		return installButton;
	}

	private synchronized JFileChooser getInstallFolderChooser()
	{
		if (installFolderChooser == null)
		{
			installFolderChooser = new JFileChooser();
			installFolderChooser.setMultiSelectionEnabled(false);
			installFolderChooser.setAcceptAllFileFilterUsed(true);
			installFolderChooser.setApproveButtonText("Choose");
			installFolderChooser
					.setApproveButtonToolTipText("Install OpenGroove in this folder");
			installFolderChooser
					.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		return installFolderChooser;
	}

}
