package net.sf.opengroove.client.com;

public interface UserNotificationListener
{
    public enum Priority
    {
        INFO, ALERT, CRITICAL
    }
    
    public void receive(long dateIssued, long dateExpires,
        Priority priority, String subject, String message);
}
