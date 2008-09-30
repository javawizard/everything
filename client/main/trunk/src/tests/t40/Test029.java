package tests.t40;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        final JButton button = new JButton("click me");
        frame.getContentPane().add(button);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setLocation(new Point(frame.getLocation().x,
            200));
        final JPopupMenu menu = new JPopupMenu();
        JColorChooser colors = new JColorChooser();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(colors, BorderLayout.CENTER);
        menu.setLayout(new BorderLayout());
        menu.add(panel, BorderLayout.CENTER);
        button.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                menu.show(button, 0, button.getHeight());
            }
        });
        frame.show();
    }
    
}
