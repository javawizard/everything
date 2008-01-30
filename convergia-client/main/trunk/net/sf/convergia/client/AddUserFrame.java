package net.sf.convergia.client;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import net.sf.convergia.client.text.TextManager;


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
public class AddUserFrame extends javax.swing.JFrame
{
	private JToggleButton cancelButton;

	private JPasswordField passwordField;

	private JTextField usernameField;

	private JLabel passwordLabel;

	private JLabel usernameLabel;

	private JToggleButton newAccountButton;

	private JLabel introLabel;

	private JToggleButton okButton;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				AddUserFrame inst = new AddUserFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public AddUserFrame()
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
			this.setTitle(TextManager.text("intouch3.adduser.window.caption"));
			{
				cancelButton = new JToggleButton();
				getContentPane().add(getCancelButton());
				cancelButton.setText(TextManager
						.text("intouch3.adduser.window.buttons.cancel"));
				cancelButton.setBounds(312, 238, 72, 26);
			}
			{
				okButton = new JToggleButton();
				getContentPane().add(getOkButton());
				okButton.setText(TextManager
						.text("intouch3.adduser.window.buttons.ok"));
				okButton.setBounds(157, 238, 48, 26);
			}
			{
				introLabel = new JLabel();
				getContentPane().add(introLabel);
				introLabel.setText(TextManager
						.text("intouch3.adduser.window.message"));
				introLabel.setBounds(37, 12, 306, 80);
			}
			{
				newAccountButton = new JToggleButton();
				getContentPane().add(getNewAccountButton());
				newAccountButton.setText(TextManager
						.text("intouch3.adduser.window.buttons.createuser"));
				newAccountButton.setBounds(205, 238, 107, 26);
			}
			{
				usernameLabel = new JLabel();
				getContentPane().add(usernameLabel);
				usernameLabel.setText(TextManager.text("intouch3.adduser.window.labels.username"));
				usernameLabel.setBounds(78, 134, 73, 16);
			}
			{
				passwordLabel = new JLabel();
				getContentPane().add(passwordLabel);
				passwordLabel.setText(TextManager.text("intouch3.adduser.window.labels.password"));
				passwordLabel.setBounds(78, 162, 73, 16);
			}
			{
				usernameField = new JTextField();
				getContentPane().add(getUsernameField());
				usernameField.setBounds(169, 132, 137, 20);
			}
			{
				passwordField = new JPasswordField();
				getContentPane().add(getPasswordField());
				passwordField.setBounds(169, 160, 137, 20);
			}
			pack();
			setSize(400, 300);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JToggleButton getCancelButton()
	{
		return cancelButton;
	}

	public JToggleButton getOkButton()
	{
		return okButton;
	}

	public JToggleButton getNewAccountButton()
	{
		return newAccountButton;
	}

	public JTextField getUsernameField()
	{
		return usernameField;
	}

	public JPasswordField getPasswordField()
	{
		return passwordField;
	}

}
