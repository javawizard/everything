package tests;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
        final JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.Y_AXIS));
        panel.add(new JLabel("test label 1"));
        panel.add(new JLabel("test label 2"));
        panel.add(new JButton("test button 1"));
        panel.add(new JButton("test button 2"));
        panel.add(new JLabel("another label"));
        panel.setBorder(new CompoundBorder(new LineBorder(
            Color.GRAY, 1), new EmptyBorder(2, 2, 2, 2)));
        frame.getContentPane().add(panel);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.show();
        frame.requestFocus();
        frame
            .addWindowFocusListener(new WindowFocusListener()
            {
                
                @Override
                public void windowGainedFocus(WindowEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void windowLostFocus(WindowEvent e)
                {
                    frame.dispose();
                    System.out.println("Frame disposed");
                }
            });
        // menu.add(item1);
        // menu.add(item2);
        // menu.show(null, -1,-1);
    }
}
