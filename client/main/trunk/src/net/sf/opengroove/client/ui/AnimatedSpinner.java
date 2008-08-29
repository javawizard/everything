package net.sf.opengroove.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * An animated spinner component. A spinner is used to indicate some sort of
 * indeterminate task is running.
 * 
 * @author Alexander Boyd
 * 
 */
public class AnimatedSpinner extends JComponent
{
    private int size;
    private int step;
    private Timer timer;
    
    public AnimatedSpinner(int size, int delay)
    {
        this.size = size;
        timer = new Timer(delay, new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    public AnimatedSpinner(int size)
    {
        this(size,)
    }
    
    public void paintComponent(Graphics g)
    {
        
    }
}
