package net.sf.opengroove.client.g3com;

/**
 * An interface that can be used to listen for notifications sent from the
 * server. Typically a notification handler would somehow alert the user of the
 * notification.
 * 
 * @author Alexander Boyd
 * 
 */
public interface UserNotificationListener
{
    public enum Priority
    {
        INFO, ALERT, CRITICAL
    }
    
    public void receive(long dateIssued, long dateExpires,
        Priority priority, String subject, String message);
}
