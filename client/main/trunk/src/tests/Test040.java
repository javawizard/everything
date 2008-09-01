package tests;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sf.opengroove.client.Symbol;

public class Test040
{
    private static boolean isExpanded = false;
    private static int min = 100;
    private static int max = 100;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        javax.swing.JFrame frame = new JFrame();
        frame.setSize(500, min);
        frame.setLocationRelativeTo(null);
        frame.setLocation(frame.getX(), frame.getY() - 200);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setResizable(false);
        final JButton button = new JButton("" + Symbol.DOWN);
        button.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                button.setEnabled(false);
                if (isExpanded)
                {
                    button.setText("" + Symbol.DOWN);
                }
                else
                {
                    button.setText("" + Symbol.UP);
                }
                new Thread()
                {
                    public void run()
                    {
                        button.setEnabled(true);
                        
                    }
                }.start();
            }
        });
        frame.getContentPane().add(button);
        frame.show();
    }
    
}
