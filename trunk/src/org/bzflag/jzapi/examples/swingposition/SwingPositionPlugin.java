package org.bzflag.jzapi.examples.swingposition;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * This plugin opens a Swing window, with a square in it that shows the current
 * location of each player. It refreshes itself once every second. Dead players
 * are not drawn.
 * 
 * @author Alexander Boyd
 * 
 */
public class SwingPositionPlugin
{
    private static JFrame frame;
    
    private static SwingPositionComponent component;
    
    public static void load(String args)
    {
        frame = new JFrame("BZFS Swing Position Plugin");
        component = new SwingPositionComponent();
        JPanel panel = new JPanel(new BorderLayout());
        frame.getContentPane().add(panel);
        panel.add(component);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.show();
        new Thread()
        {
            public void run()
            {
                while (frame.isShowing())
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                    }
                    component.repaint();
                }
            }
        }.start();
    }
    
    public static void unload()
    {
        frame.dispose();
    }
    
}
