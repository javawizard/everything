package tests;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class Test029
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // A test for seeing if custom components can be added to a JPopupMenu,
        // without using the default layout manager (I think it's BoxLayout)F
        JFrame frame = new JFrame("test029");
        JButton button = new JButton("click me");
        frame.getContentPane().add(button);
        frame.pack();
        JPopupMenu menu = new JPopupMenu();
        JColorChooser colors = new JColorChooser();
        JPanel panel = new JPanel();
        panel.setLayout
        frame.show();
    }
    
}
