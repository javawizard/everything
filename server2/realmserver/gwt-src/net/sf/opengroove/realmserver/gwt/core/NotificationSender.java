package net.sf.opengroove.realmserver.gwt.core;

public class NotificationSender
{
    /**
     * Opens a dialog that prompts the user to send a notification. The "to" of
     * the notification cannot be changed by the user. The rest of the fields,
     * however, can be changed, and the respective widgets will be pre-populated
     * with the values passed into this method. The user is allowed to send or
     * cancel the notification. The dialog that allows the user to do so will be
     * modal.
     * 
     * @param to Who to send the notification to
     * @param subject
     * @param message
     * @param priority
     * @param dismissMinutes
     */
    public void promptForSend(String to, String subject,
        String message, String priority, int dismissMinutes)
    {
        
    }
}
