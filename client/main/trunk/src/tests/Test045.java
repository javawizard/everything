package tests;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import net.sf.opengroove.client.ui.TestFrame;

public class Test045
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TestFrame f = new TestFrame();
        JMenuBar bar = new JMenuBar();
        bar.add(new JMenu("Menu 1"));
        bar.add(new JMenu("Menu 2"));
        bar.add(new JMenu("Menu 3"));
        f.setJMenuBar(bar);
        f.show();
    }
    
}
