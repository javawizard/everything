package net.sf.opengroove.client.ui.transitions.included;

import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPanel;

import net.sf.opengroove.client.notification.TaskbarNotificationFrame;
import net.sf.opengroove.client.ui.Animations;
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
    private TaskbarNotificationFrame frame;
    
    private int rx;
    private int ry;
    private int sy;
    private double scale = 0;
    private static final int STEP_COUNT = 64;
    
    @Override
    public void apply(int step)
    {
        sy = Toolkit.getDefaultToolkit().getScreenSize().height;
        scale = (step * 1.0) / (getStepCount() * 1.0);
        relocate();
    }
    
    private void relocate()
    {
        Double newScale = ((Animations.ScaleFunction.DOUBLE_SINE
            .getValue(((scale / 2) + 0.5)) - 0.5) * 2.0);
        int sDifference = sy - ry;
        if (sDifference < 0)
            sDifference = 0;
        double newDifference = sDifference * newScale;
        int newY = (int) (sy - newDifference);
        frame.setLocation(rx, newY);
    }
    
    @Override
    public int getStepCount()
    {
        return STEP_COUNT;
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
        this.rx = x;
        this.ry = y;
        relocate();
    }
    
}
