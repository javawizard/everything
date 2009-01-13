package net.sf.opengroove.client.notification;

import java.awt.Component;

import javax.swing.JFrame;

public class FrameShowingNotification extends
    NotificationAdapter
{
    private JFrame showFrame;
    
    public FrameShowingNotification(
        TaskbarNotificationFrame frame,
        Component component, JFrame toShowFrame,
        boolean isAlert, boolean isOneTime)
    {
        super(frame, component, isAlert, isOneTime);
        this.showFrame = toShowFrame;
    }
    
    @Override
    public void clicked()
    {
        super.clicked();
        showFrame.show();
    }
    
}
