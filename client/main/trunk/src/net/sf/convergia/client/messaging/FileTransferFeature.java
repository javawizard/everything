package net.sf.convergia.client.messaging;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sf.convergia.client.features.Feature;

public class FileTransferFeature extends Feature
{

	@Override
	public void initialize()
	{
		final JMenuItem menuitem = new JMenuItem("Send a file");
		menuitem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				new Thread()
				{
					public void run()
					{
						promptToSendFile(SwingUtilities
								.getWindowAncestor(menuitem));
					}
				}.start();
			}
		});
		registerComponent("launchbarConvergiaMenuItem", menuitem);
	}

	protected void promptToSendFile(Window window)
	{
		JOptionPane.showMessageDialog(window,
				"This is a prototype file transfer utility. If there are any bugs, "
						+ "use the Help -> Contact us menu item.");
		if (!isOnline())
		{
			JOptionPane.showMessageDialog(window,
					"You are not online. You must be online to send a file.");
			return;
		}
		String username = JOptionPane.showInputDialog(window,
				"Type the name of the user to send a file to.");
		if (username == null)
			return;
		if (!Arrays.asList(listOnlineUsers()).contains(username))
		{
			JOptionPane.showMessageDialog(window,
					"That user doesn't exist, or they are offline.");
			return;
		}
		String path = JOptionPane
				.showInputDialog(
						window,
						"Type the path of the file to send. This will be replaced with a file chooser soon.");
		if (path == null)
			return;
		File file = new File(path);
		if (!file.exists())
		{
			JOptionPane.showMessageDialog(window, "That file doesn't exist.");
			return;
		}
		if (file.isDirectory())
		{
			JOptionPane.showMessageDialog(window,
					"That is a folder, not a file. You can only send files.");
			return;
		}
		if (!file.canRead())
		{
			JOptionPane
					.showMessageDialog(window,
							"Convergia can't read that file. Try placing it somewhere else on your system.");
			return;
		}
		// FIXME: actually add code for sending the file here
	}

	@Override
	public void receiveMessage(String from, String message)
	{

	}

	@Override
	public void userStatusChanged()
	{
		// TODO Auto-generated method stub

	}

}
