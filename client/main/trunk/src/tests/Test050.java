package tests;

import java.awt.BorderLayout;

import info.clearthought.layout.TableLayout;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
        JMenuBar bar = new JMenuBar();
        bar.setLayout(new TableLayout(new double[] {
            TableLayout.PREFERRED, TableLayout.PREFERRED,
            TableLayout.FILL }, new double[] {
            TableLayout.PREFERRED, TableLayout.FILL }));
        JLabel lowerLabel = new JLabel("Alex Boyd");
        bar.add(menu, "0, 0");
        bar.add(menu2, "0, 1");
        bar.add(new JLabel(""), "0, 2, c, c");
        bar.add(lowerLabel, "1, 0, 1, 2, c, c");
        frame.getContentPane()
            .setLayout(new BorderLayout());
        frame.getContentPane().add(bar);
        frame.show();
    }
    
}
