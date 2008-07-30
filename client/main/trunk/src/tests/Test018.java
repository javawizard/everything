package tests;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.Border;

public class Test018
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // A class to test hiding button borders when the mouse isn't in the
        // button. Code for this used in ItemChooser.
        JFrame frame = new JFrame();
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        final JButton button = new JButton("click me");
        final Border border = button.getBorder();
        button.addMouseListener(new MouseListener()
        {
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void mouseEntered(MouseEvent e)
            {
                button.setBorder(border);
                button.setBackground(null);
            }
            
            @Override
            public void mouseExited(MouseEvent e)
            {
                button.setBorder(null);
                button.setBackground(new Color(255, 255, 0,
                    0));
                button.setOpaque(false);
            }
            
            @Override
            public void mousePressed(MouseEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void mouseReleased(MouseEvent e)
            {
                // TODO Auto-generated method stub
                
            }
        });
        frame.getContentPane().add(button);
        frame.show();
    }
    
}
