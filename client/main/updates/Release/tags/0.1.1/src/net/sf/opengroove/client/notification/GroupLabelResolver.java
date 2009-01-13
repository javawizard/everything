package net.sf.opengroove.client.notification;

/**
 * A GroupLabelResolver is a class that can be passed to a
 * TaskbarNotificationFrame, and the notification frame will use it to figure
 * out the name of a particular group as the user should see it.
 * 
 * @author Alexander Boyd
 * 
 */
public interface GroupLabelResolver
{
    /**
     * Returns the text that should show as the name of the group in the UI.
     * 
     * @param group
     *            The name of the group, as specified when adding a notification
     *            to the TaskbarNotificationFrame
     * @return a string representing the group's label, or what the group's name
     *         should appear as in the UI
     */
    public String resolveLabel(String group);
}
