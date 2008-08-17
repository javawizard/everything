package net.sf.opengroove.client.ui;

import java.awt.Component;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class SwingPopupMenu extends JWindow
{
    private JPanel panel;
    
    public SwingPopupMenu()
    {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.X_AXIS));
    }
    
    public void show(Component invoker, int x, int y)
    {
        if (invoker != null)
        {
            Point p = new Point(x, y);
            SwingUtilities.convertPointToScreen(p, invoker);
            x = p.x;
            y = p.y;
        }
    }
    
    /**
     * Returns the panel that contains the actual contents of this popup menu.
     * Components should be added to this instead of to the popup menu directly.<br/><br/>
     * 
     * When the popup menu is created, this panel will use a vertical BoxLayout,
     * which means that menus and menu items can be added directly to it. The
     * layout can be changed if some other presentation of components is
     * desired.
     */
    public JPanel getContentPane()
    {
        return panel;
    }
}
