package net.sf.opengroove.client;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import net.sf.opengroove.client.text.TextManager;


/**
 * This class is obsolete and will be removed.
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
