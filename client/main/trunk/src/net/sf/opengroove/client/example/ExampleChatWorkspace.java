package net.sf.opengroove.client.example;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.opengroove.client.notification.NotificationAdapter;
import net.sf.opengroove.client.workspace.Workspace;


/**
 * simple chat workspace, intended as an example. when activated, it shows a
 * JFrame that contains a textarea (non-editable) and a text field. you type in
 * the text field, then hit enter, and your chat message is sent to all online
 * members of the workspace. when a chat message is received, it shows up in the
 * text area. whenever you restart Convergia, the text area is cleared (meaning
 * that it stores the current chat info in memory).
 * 
 * it has one configuration
 * 
 * @author Alexander Boyd
 * 
 */
public class ExampleChatWorkspace extends Workspace
{
	private JTextArea ta = new JTextArea();

	private JTextField tf = new JTextField();

	private JFrame frame = new JFrame();

	private NotificationAdapter notification;

	private JLabel notificationLabel;

	private JCheckBox showWhenReceived = new JCheckBox(
			"<html>Show chat window when a chat<br/>message is received");

	@Override
	public void configurationCancelled()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void configurationSaved()
	{
		if (showWhenReceived.isSelected())
			try
			{
				new File(getStorageFile(), "showWhenReceived").createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		else
			new File(getStorageFile(), "showWhenReceived").delete();
	}

	@Override
	public Map<String, JComponent> getConfigurationComponents()
	{
		return null;
	}

	@Override
	public void initialize()
	{
		frame.setTitle(getName());
		frame.setSize(300, 400);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(ta), BorderLayout.CENTER);
		frame.getContentPane().add(tf, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);
		notificationLabel = new JLabel();
		notificationLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		notification = new NotificationAdapter(notificationLabel, true, false)
		{
			public void clicked()
			{
				removeNotification(this);
				frame.show();
			}
		};
		tf.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if (tf.getText().trim().length() > 0)
				{
					try
					{
						for (String u : listOnlineUsers())
						{
							sendMessage(u, tf.getText().trim());
						}
					} catch (Exception e2)
					{
						ta
								.append("You are not connected to the internet right now, so you cannot send chat messages.\n\n");
					}
				}
				tf.setText("");
			}
		});
		ta.setEditable(false);
		tf.requestFocusInWindow();
	}

	@Override
	public void receiveMessage(String from, String message)
	{
		ta.append("" + from + " (" + new Date() + "):\n" + message + "\n\n");
		notificationLabel.setText("" + from + " has sent a chat message in "
				+ getName());
		if (!frame.isActive())
			addNotification(notification, true);
		ta.setCaretPosition(ta.getDocument().getLength());
	}

	@Override
	public void userActivate()
	{
		frame.show();
	}

	@Override
	public void userStatusChanged()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown()
	{
		frame.hide();
	}

}
