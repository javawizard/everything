package tests.t40;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
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
        JFrame other = new JFrame();
        other.setSize(300, 200);
        final JButton showButton = new JButton("show popup");
        other.getContentPane().setLayout(new FlowLayout());
        other.getContentPane().add(showButton);
        final JDialog frame = new JDialog();
        showButton.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.pack();
                Point p = new Point(0, showButton
                    .getHeight());
                SwingUtilities.convertPointToScreen(p,
                    showButton);
                frame.setLocation(p);
                frame.show();
                frame.requestFocus();
            }
        });
        other.setLocationRelativeTo(null);
        other.show();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.Y_AXIS));
        panel.add(new JLabel("test label 1"));
        panel.add(new JLabel("test label 2"));
        panel.add(new JButton("test button 1"));
        final JButton button = new JButton("test button 2");
        panel.add(button);
        panel.add(new JLabel("another label"));
        panel.setBorder(new CompoundBorder(new LineBorder(
            Color.GRAY, 1), new EmptyBorder(2, 2, 2, 2)));
        final JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem(
            "A test menu item that has a long name"));
        JMenu menu2 = new JMenu("An actual menu");
        menu2.add(new JMenuItem("A subitem"));
        menu2.add(new JMenuItem("another test subitem"));
        menu.add(menu2);
        button.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                menu.show(button, 0, button.getHeight());
            }
        });
        frame.getContentPane().add(panel);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
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
                    frame.hide();
                    System.out.println("Frame hidden");
                }
            });
        // menu.add(item1);
        // menu.add(item2);
        // menu.show(null, -1,-1);
    }
}
