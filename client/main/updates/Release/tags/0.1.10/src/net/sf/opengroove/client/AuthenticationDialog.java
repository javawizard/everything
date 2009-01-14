package net.sf.opengroove.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import net.sf.opengroove.client.text.TextManager;

/**
 * This class is obsolete and will be removed.
 */

public class AuthenticationDialog extends JFrame
{

	private String username;

	private JCheckBox autoLoginCheckbox;

	private JLabel jLabel_IL;

	private JLabel newUserLabel;

	private JComboBox usernameField;

	private JLabel usernameLabel;

	private JLabel passwordLabel;

	private JPasswordField passwordField;

	private JButton okButton;

	private JButton cancelButton;

	private String password;

	private boolean clickedOnOK = false;

	private boolean clickedOnNewUser = false;

	/**
	 * returns the last username inputted to this dialog.
	 * 
	 * @return
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * returns the last password inputted to this dialog.
	 * 
	 * @return
	 */
	public String getPassword()
	{
		return password;
	}

	public void setUsernames(String[] usernames)
	{
		usernameField.removeAllItems();
		for (String s : usernames)
		{
			usernameField.addItem(s);
		}
	}

	/**
	 * returns true if the last time this dialog was shown, OK was pressed,
	 * false if Cancel or close was pressed. this will be false while the dialog
	 * is showing.
	 * 
	 * @return
	 */
	public boolean clickedOnOK()
	{
		return clickedOnOK;
	}

	public static void main(String[] args) throws Throwable
	{
		AuthenticationDialog dialog = new AuthenticationDialog(null);
		dialog.show();
		System.out.println("clickedOnOK was " + dialog.clickedOnOK()
				+ ", username was " + dialog.getUsername() + ", password was "
				+ dialog.getPassword());
	}

	public AuthenticationDialog(Frame parent)
	{
		super();
		this.setSize(320, 222);
		setLocationRelativeTo(null);
		setTitle(TextManager.text("intouch3.auth.dialog.caption"));
		SpringLayout layout = new SpringLayout();
		getContentPane().setLayout(null);
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);
		topPanel.setBounds(0, 0, 304, 0);
		{
			cancelButton = new JButton();
			getContentPane().add(cancelButton);
			cancelButton.setText(TextManager
					.text("intouch3.auth.dialog.buttons.cancel"));
			cancelButton.setBounds(220, 158, 84, 28);
			cancelButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					passwordField.setText("");
					clickedOnOK = false;
					clickedOnNewUser = false;
					hide();
				}
			});
		}
		{
			okButton = new JButton();
			getContentPane().add(okButton);
			okButton.setText(TextManager
					.text("intouch3.auth.dialog.buttons.ok"));
			okButton.setBounds(158, 158, 62, 28);
			okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					username = (String) usernameField.getSelectedItem();
					password = new String(passwordField.getPassword());
					passwordField.setText("");
					clickedOnOK = true;
					clickedOnNewUser = false;
					hide();
				}
			});
		}
		{
			passwordField = new JPasswordField();
			getContentPane().add(passwordField);
			passwordField.setBounds(83, 83, 197, 25);
		}
		{
			passwordLabel = new JLabel();
			getContentPane().add(passwordLabel);
			passwordLabel.setText(TextManager
					.text("intouch3.auth.dialog.labels.password"));
			passwordLabel.setBounds(4, 86, 70, 19);
		}
		{
			usernameLabel = new JLabel();
			getContentPane().add(usernameLabel);
			usernameLabel.setText(TextManager
					.text("intouch3.auth.dialog.labels.username"));
			usernameLabel.setBounds(4, 51, 116, 19);
		}
		{
			usernameField = new JComboBox();
			getContentPane().add(usernameField);
			usernameField.setSelectedItem(username);
			usernameField.setBounds(83, 46, 197, 25);
		}
		{
			newUserLabel = new JLabel();
			getContentPane().add(newUserLabel);
			newUserLabel.setText(TextManager
					.text("intouch3.auth.dialog.message.newuser"));
			newUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
			newUserLabel.setAutoscrolls(true);
			newUserLabel.setBounds(8, 23, 284, 16);
			newUserLabel.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
			newUserLabel.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					passwordField.setText("");
					clickedOnOK = false;
					clickedOnNewUser = true;
					hide();
				}
			});
		}
		{
			jLabel_IL = new JLabel();
			getContentPane().add(jLabel_IL);
			jLabel_IL.setText(TextManager
					.text("intouch2.auth.dialog.message.mainmessage"));
			jLabel_IL.setBounds(4, 5, 300, 16);
		}
		// START >> autoLoginCheckbox
		autoLoginCheckbox = new JCheckBox();
		getContentPane().add(getAutoLoginCheckbox());
		autoLoginCheckbox.setText("Log me in when OpenGroove starts");
		autoLoginCheckbox.setBounds(83, 116, 221, 24);
		// END << autoLoginCheckbox
		{
		}
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				passwordField.setText("");
				hide();
			}
		});
	}

	public void show()
	{
		clickedOnOK = false;
		clickedOnNewUser = false;
		passwordField.requestFocusInWindow();
		super.show();
	}

	public void show(String username)
	{
		clickedOnOK = false;
		clickedOnNewUser = false;
		usernameField.setSelectedItem(username);
		passwordField.requestFocusInWindow();
		super.show();
	}

	public void setVisible(boolean b)
	{
		if (b == true)
		{
			clickedOnOK = false;
			passwordField.requestFocusInWindow();
		}
		super.setVisible(b);
	}

	public boolean clickedOnNewUser()
	{
		return clickedOnNewUser;
	}

	public JCheckBox getAutoLoginCheckbox()
	{
		return autoLoginCheckbox;
	}

	public JComboBox getUsernameField()
	{
		return usernameField;
	}

	public JPasswordField getPasswordField()
	{
		return passwordField;
	}

}
