package net.sf.opengroove.realmserver.gwt.core;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;

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
     * @param to
     *            Who to send the notification to. This should either be the
     *            string "all", which will send a notification to all users,
     *            "user:USERNAME", where USERNAME is a username, which will send
     *            a notification to that particular user only, or
     *            "computer:USERNAME/COMPUTERNAME", where USERNAME is the
     *            username of a user and COMPUTERNAME is the name of that user's
     *            computer to send the notification to. For example,
     *            "user:javawizard" would send a notification to the user
     *            javawizard, and "user:javawizard/homecomputer" would send a
     *            notification to javawizard's home computer.
     * @param subject
     *            The initial value that the subject widget should have
     * @param message
     *            The initial value that the message widget should have
     * @param priority
     *            The initial value that the priority field should have. This,
     *            if not empty, should be one of "info", "alert", or "critical".
     * @param dismissMinutes
     *            The initial value that the "dismiss time" field should have,
     *            in minutes. If this is not one of the choices in the dropdown
     *            widget that displays this field, a new entry will be added to
     *            the dropdown box for this instance only. The new instance, if
     *            it needs to be added, will be shown in minutes. For example,
     *            if 300 is used (which corresponds to 5 hours), but 5 hours is
     *            not present in the list, it will be shown as 300 minutes, not
     *            5 hours. If 5 hours is present, however, then 5 hours will be
     *            shown.
     */
    public void promptForSend(String to, String subject,
        String message, String priority, int dismissMinutes)
    {
        DialogBox dialog = new DialogBox();
        dialog.setText("Send notification");
        HTMLPanel panel = new HTMLPanel(
            "<table border='0' cellspacing='0' cellpadding='3'>"
                + "<tr><td><b>To: &nbsp; </b></td><td><div id='toField'/></td></tr>"
                + "<tr><td><b>Subject: &nbsp; </b></td><td><div id='subjectField'/></td></tr>"
                + "<tr><td><b>Priority: &nbsp; </b></td><td><div id='priorityField'/></td></tr>"
                + "<tr><td><b>Dismiss in: &nbsp; </b></td><td><div id='dismissField'/></td></tr>"
                + "<tr><td colspan='2'><b>Message: &nbsp; </td></tr>"
                + "<tr><td colspan='2'><div id='messageField'/></td></tr>"
                + "<tr><td colspan='2' align='right'><div id='buttons'/></td></tr>");
        
        dialog.setWidget(panel);
        dialog.center();
        dialog.show();
    }
}
