package tests;

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.UIManager;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.ui.TestFrame;

public class Test044
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        UIManager.setLookAndFeel(UIManager
            .getSystemLookAndFeelClassName());
        TestFrame frame = new TestFrame();
        JButton button = new JButton("Test button");
        button.setFocusable(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        frame.add(button);
        JButton button2 = new JButton("Test button");
        button2.setFocusable(false);
        frame.add(button2);
        frame.show();
    }
    
}
