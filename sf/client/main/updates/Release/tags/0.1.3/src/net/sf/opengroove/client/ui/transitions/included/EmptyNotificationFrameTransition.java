package net.sf.opengroove.client.ui.transitions.included;

import javax.swing.JPanel;

import net.sf.opengroove.client.notification.TaskbarNotificationFrame;
import net.sf.opengroove.client.ui.transitions.NotificationFrameTransition;

public class EmptyNotificationFrameTransition implements
    NotificationFrameTransition
{
    protected TaskbarNotificationFrame frame;
    
    @Override
    public void apply(int step)
    {
        if (step < 2)
            frame.hide();
        else
            frame.show();
    }
    
    @Override
    public int getStepCount()
    {
        return 4;
    }
    
    @Override
    public void initialize(TaskbarNotificationFrame frame,
        JPanel panel)
    {
        this.frame = frame;
        frame.getContentPane().add(panel);
    }
    
    @Override
    public void setWindowPosition(int x, int y)
    {
        frame.setLocation(x, y);
    }
    
}
