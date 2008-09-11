package net.sf.opengroove.client.ui.transitions.included;

import javax.swing.JPanel;

import net.sf.opengroove.client.notification.TaskbarNotificationFrame;
import net.sf.opengroove.client.ui.transitions.NotificationFrameTransition;

/**
 * A transition that slides the notification frame in from the bottom of the
 * screen. It keeps the notification frame's X coordinate as-is, and moves it
 * between it's requested value and the height of the screen.
 * 
 * @author Alexander Boyd
 * 
 */
public class SlideInNotificationFrameTransition implements
    NotificationFrameTransition
{
    
    @Override
    public void apply(int step)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int getStepCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public void initialize(TaskbarNotificationFrame frame,
        JPanel panel)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setWindowPosition(int x, int y)
    {
        // TODO Auto-generated method stub
        
    }
    
}
