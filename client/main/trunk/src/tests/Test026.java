package tests;

import javax.swing.JLabel;

import net.sf.opengroove.client.notification.NotificationAdapter;
import net.sf.opengroove.client.notification.TaskbarNotificationFrame;

public class Test026
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TaskbarNotificationFrame frame = new TaskbarNotificationFrame();
        frame.addNotification("testgroup1",
            new NotificationAdapter(new JLabel(
                "notification 1"), true, true), false);
        frame.addNotification("testgroup1",
            new NotificationAdapter(new JLabel(
                "notification 2"), true, true), false);
        frame.addNotification("testgroup2",
            new NotificationAdapter(new JLabel(
                "notification 3"), true, true), false);
        frame.addNotification("testgroup1",
            new NotificationAdapter(new JLabel(
                "notification 4"), true, true), false);
        frame.addNotification("testgroup1",
            new NotificationAdapter(new JLabel(
                "notification 5"), true, true), false);
        frame.addNotification("testgroup2",
            new NotificationAdapter(new JLabel(
                "notification 6"), true, true), false);
        frame.addNotification("testgroup2",
            new NotificationAdapter(new JLabel(
                "notification 7"), true, true), true);
    }
    
}
