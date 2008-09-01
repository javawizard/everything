package tests;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sf.opengroove.client.Symbol;
import net.sf.opengroove.client.ui.Animations;

public class Test040
{
    private static boolean isExpanded = false;
    private static int min = 100;
    private static int max = 500;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        final javax.swing.JFrame frame = new JFrame();
        frame.setSize(500, min);
        frame.setLocationRelativeTo(null);
        frame.setLocation(frame.getX(), frame.getY() - 200);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setResizable(false);
        final JButton button = new JButton("" + Symbol.DOWN);
        button.setFocusable(false);
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
                        Point[] points = Animations
                            .generateLineAnimation(
                                new Point(0,
                                    isExpanded ? max : min),
                                new Point(0,
                                    isExpanded ? min : max),
                                50);
                        for (int i = 0; i < points.length; i++)
                        {
                            frame.setSize(frame.getWidth(),
                                points[i].y);
                            try
                            {
                                Thread.sleep(10);
                            }
                            catch (InterruptedException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        isExpanded = !isExpanded;
                        button.setEnabled(true);
                    }
                }.start();
            }
        });
        frame.getContentPane().add(button);
        frame.show();
    }
    
}
