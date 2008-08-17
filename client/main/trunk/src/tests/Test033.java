package tests;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Test033
{
    
    /**
     * A test for displaying a custom heavyweight popup menu. This completely
     * re-implements a jpopupmenu's functionality by creating a JWindow with a
     * vertical BoxLayout. Menu items can then be added. When the popup menu is
     * to be shown, it's location is set to the location specified, and it is
     * shown as an always-on-top focused window. When it loses focus, it is
     * hidden.
     */
    public static void main(String[] args)
    {
        // SwingPopupMenu menu = new SwingPopupMenu();
        JFrame frame = new JFrame();
        frame.getContentPane().add(
            new JLabel("This is the test frame"));
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.show();
        // menu.add(item1);
        // menu.add(item2);
        // menu.show(null, -1,-1);
    }
    
}
