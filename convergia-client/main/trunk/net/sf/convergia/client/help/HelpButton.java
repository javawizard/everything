package net.sf.convergia.client.help;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sf.convergia.client.Convergia;

import com.l2fprod.common.swing.JLinkButton;

public class HelpButton extends JLinkButton
{
	/**
	 * creates a help button that, when clicked, shows the help referenced by
	 * the help path specified.
	 * 
	 * @param name
	 *            the label, or text, that shows up on this button
	 * @param path
	 *            the path to the help. this is passed to
	 *            Convergia.showHelpTopic()
	 */
	public HelpButton(String name, final String path)
	{
		super("<html><u>" + name + "</u>");
		setBorder(null);
		setForeground(Color.BLUE);
		addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				Convergia.showHelpTopic(path);
			}
		});
	}
}
