package net.sf.opengroove.client;

import java.awt.HeadlessException;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AMenuItem extends MenuItem
{

	public AMenuItem() throws HeadlessException
	{
		super();
		listen();
	}

	private void listen()
	{
		addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				AMenuItem.this.run(e);
			}
		});
	}

	public AMenuItem(String label) throws HeadlessException
	{
		super(label);
		// TODO Auto-generated constructor stub
		listen();
	}

	public AMenuItem(String label, MenuShortcut s) throws HeadlessException
	{
		super(label, s);
		// TODO Auto-generated constructor stub
		listen();
	}

	public abstract void run(ActionEvent e);
}
