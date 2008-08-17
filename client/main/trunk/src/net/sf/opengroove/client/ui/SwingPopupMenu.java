package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class SwingPopupMenu extends JWindow
{
    private JPanel panel;
    
    public SwingPopupMenu()
    {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.X_AXIS));
        panel.setBorder(new CompoundBorder(new LineBorder(
            Color.DARK_GRAY, 1),
            new EmptyBorder(2, 2, 2, 2)));
        panel.setOpaque(true);
        super.getContentPane()
            .setLayout(new BorderLayout());
        super.getContentPane().add(panel);
        super.setAlwaysOnTop(true);
        super.addFocusListener(new FocusAdapter()
        {
            
            @Override
            public void focusLost(FocusEvent e)
            {
                SwingPopupMenu.this.hide();
            }
        });
    }
    
    /**
     * Adds the specified menu item to this popup menu. This is a convienence
     * method for <code>getContentPane().add(item)</code>.
     * 
     * @param item The menu item to add
     */
    public void add(JMenuItem item)
    {
        getContentPane().add(item);
    }
    
    /**
     * Shows this popup menu. The window will be packed before showing. When the
     * window loses focus, it will be hidden.
     * 
     * @param invoker
     * @param x
     * @param y
     */
    public void show(Component invoker, int x, int y)
    {
        if (invoker != null)
        {
            Point p = new Point(x, y);
            SwingUtilities.convertPointToScreen(p, invoker);
            x = p.x;
            y = p.y;
        }
        setLocation(x, y);
        pack();
        show();
        requestFocus();
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
