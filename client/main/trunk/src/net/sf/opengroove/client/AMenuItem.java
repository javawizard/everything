package net.sf.opengroove.client;

import java.awt.HeadlessException;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A class that allows for creation of anonymous inner MenuItems <i>en masse</i>,
 * IE you can add them directly to an array without having to worry about adding
 * action listeners invidivually to each element. Classes that extend this class
 * (which are intended to be anonymous inner classes) should implement the
 * {@link #run(ActionEvent)} method, which is called whenever an action event is
 * fired on this menu item. In addition, action listeners may still be
 * registered as normal to this menu item.
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class AMenuItem extends MenuItem
{
    /**
     * Creates a new menu item.
     * 
     * @throws HeadlessException
     */
    public AMenuItem() throws HeadlessException
    {
        super();
        listen();
    }
    
    /**
     * Adds an action listener to this menu item that calls run() when it's
     * actionPerformed() method is called. This is called by the constructors of
     * this class.
     */
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
    
    public AMenuItem(String label, MenuShortcut s)
        throws HeadlessException
    {
        super(label, s);
        // TODO Auto-generated constructor stub
        listen();
    }
    
    /**
     * Called when an action event is fired on this menu item.
     * 
     * @param e
     *            The action event that was fired
     */
    public abstract void run(ActionEvent e);
}
