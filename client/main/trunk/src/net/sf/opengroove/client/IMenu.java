package net.sf.opengroove.client;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A menu that allows menu items to be added in the constructor. This makes it
 * easier to create a menu where the list of menu items is already known.
 * 
 * @author Alexander Boyd
 * 
 */
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
