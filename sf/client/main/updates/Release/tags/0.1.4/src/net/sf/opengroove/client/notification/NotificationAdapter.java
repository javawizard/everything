package net.sf.opengroove.client.notification;

import java.awt.Component;

/**
 * a TaskbarNotification that returns the component passed to the
 * constructor,and the status as to whether it is an alert. If clicked() is not
 * overridden, the notification will be dismissed when it is clicked.
 * 
 * @author Alexander Boyd
 * 
 */
public class NotificationAdapter implements
    TaskbarNotification
{
    private Component component;
    
    private boolean isAlert;
    
    private boolean isOneTimeOnly;
    
    private TaskbarNotificationFrame frame;
    
    /**
     * Creates a new notification adapter.
     * 
     * @param frame
     *            The parent frame for this adapter. This is only required if
     *            clicked() is not overridden.
     * @param component
     *            The component to display for this notification adapter.
     * @param isAlert
     *            True if this is an alert (in which case the taskbar icon will
     *            flash when this item is in the taskbar notification frame),
     *            false if not.
     * @param isOneTimeOnly
     *            True if this is a one-time-only alert, which means that it
     *            will be automatically dismissed the next time that the
     *            notification frame is hidden.
     */
    public NotificationAdapter(
        TaskbarNotificationFrame frame,
        Component component, boolean isAlert,
        boolean isOneTimeOnly)
    {
        super();
        this.frame = frame;
        this.component = component;
        this.isAlert = isAlert;
        this.isOneTimeOnly = isOneTimeOnly;
    }
    
    public NotificationAdapter(
        TaskbarNotificationFrame frame,
        Component component, boolean isAlert)
    {
        this(frame, component, isAlert, false);
    }
    
    public void clicked()
    {
        if (frame != null)
            frame.removeNotification(this);
    }
    
    public Component getComponent()
    {
        // TODO Auto-generated method stub
        return component;
    }
    
    public boolean isAlert()
    {
        // TODO Auto-generated method stub
        return isAlert;
    }
    
    public void mouseOut()
    {
        // TODO Auto-generated method stub
        
    }
    
    public void mouseOver()
    {
        // TODO Auto-generated method stub
        
    }
    
    public boolean isOneTimeOnly()
    {
        // TODO Auto-generated method stub
        return isOneTimeOnly;
    }
    
}
