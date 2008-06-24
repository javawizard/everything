package net.sf.opengroove.client;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import com.l2fprod.common.swing.JLinkButton;

public class UserInformationLink extends JLinkButton
{
	public UserInformationLink(final String username, String label)
	{
		super(label);
		setBorder(null);
		addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				OpenGroove.showUserInformationDialog(username,
						(JFrame) getTopLevelAncestor());
			}
		});
	}

	public UserInformationLink(String username)
	{
		this(username, "   " + username);
	}
}
