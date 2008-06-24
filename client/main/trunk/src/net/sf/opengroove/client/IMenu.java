package net.sf.opengroove.client;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class IMenu extends JMenu
{

	public IMenu(String string, JMenuItem[] items)
	{
		super(string);
		for (JMenuItem i : items)
		{
			add(i);
		}
	}

}
