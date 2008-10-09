package tests;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sf.opengroove.client.IMenu;
import net.sf.opengroove.client.ui.TestFrame;

public class Test050
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TestFrame frame = new TestFrame();
        JMenu menu = new IMenu("Testmenu", new JMenuItem[] {
            new JMenuItem("Item 1"),
            new JMenuItem("Item 2") });
        JMenu menu2 = new IMenu("Testmenu2",
            new JMenuItem[] { new JMenuItem("Item 1"),
                new JMenuItem("Item 2") });
        menu2.set
        frame.getContentPane().add(menu);
        frame.getContentPane().add(menu2);
        frame.show();
    }
    
}
