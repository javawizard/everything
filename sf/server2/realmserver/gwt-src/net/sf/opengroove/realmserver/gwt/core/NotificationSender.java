package net.sf.opengroove.realmserver.gwt.core;

import java.util.ArrayList;

import net.sf.opengroove.realmserver.gwt.core.rcp.NotificationException;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

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
     *            in minutes. For the default value, use -1. If this is not one
     *            of the choices in the dropdown widget that displays this
     *            field, a new entry will be added to the dropdown box for this
     *            instance only. The new instance, if it needs to be added, will
     *            be shown in minutes. For example, if 300 is used (which
     *            corresponds to 5 hours), but 5 hours is not present in the
     *            list, it will be shown as 300 minutes, not 5 hours. If 5 hours
     *            is present, however, then 5 hours will be shown instead of 300
     *            minutes.
     */
    public static void promptForSend(final String to,
        String subject, String message, String priority,
        int dismissMinutes)
    {
        final DialogBox dialog = new DialogBox();
        dialog.setText("Send notification");
        HTMLPanel panel = new HTMLPanel(
            "<table border='0' cellspacing='6' cellpadding='0'>"
                + "<tr><td><b>To: &nbsp; </b></td><td><div id='toField'/></td></tr>"
                + "<tr><td><b>Subject: &nbsp; </b></td><td><div id='subjectField'/></td></tr>"
                + "<tr><td><b>Priority: &nbsp; </b></td><td><div id='priorityField'/></td></tr>"
                + "<tr><td><b>Dismiss in: &nbsp; </b></td><td><div id='dismissField'/></td></tr>"
                + "<tr><td colspan='2'><b>Message: &nbsp; </td></tr>"
                + "<tr><td colspan='2'><div id='messageField'/></td></tr>"
                + "<tr><td colspan='2' align='right'><div id='buttons'/></td></tr>");
        Label toLabel = new Label(to);
        panel.add(toLabel, "toField");
        final TextBox subjectField = new TextBox();
        subjectField.setVisibleLength(20);
        panel.add(subjectField, "subjectField");
        final ListBox priorityField = new ListBox();
        priorityField.setVisibleItemCount(1);
        priorityField.addItem("Info", "INFO");
        priorityField.addItem("Alert", "ALERT");
        priorityField.addItem("Critical", "CRITICAL");
        priorityField.setSelectedIndex(0);
        panel.add(priorityField, "priorityField");
        final ListBox dismissField = new ListBox();
        dismissField.setVisibleItemCount(1);
        final ArrayList<Integer> dismissValues = new ArrayList<Integer>();
        boolean hasDefaultMinutes = false;
        for (int i = 1; i < 12; i++)
        {
            dismissField.addItem("" + (i * 5) + " minutes");
            dismissValues.add(i * 5);
            if ((dismissMinutes == (i * 5))
                || (dismissMinutes == -1 && (i * 5) == 30))
            {
                dismissField.setSelectedIndex(dismissValues
                    .size() - 1);
                hasDefaultMinutes = true;
            }
        }
        for (int i = 1; i < 24; i++)
        {
            dismissField.addItem("" + i + " hours");
            dismissValues.add(i * 60);
            if (dismissMinutes == (i * 60))
            {
                dismissField.setSelectedIndex(dismissValues
                    .size() - 1);
                hasDefaultMinutes = true;
            }
        }
        for (int i = 1; i < 7; i++)
        {
            dismissField.addItem("" + i + " days");
            dismissValues.add(i * 60 * 24);
            if (dismissMinutes == (i * 60 * 24))
            {
                dismissField.setSelectedIndex(dismissValues
                    .size() - 1);
                hasDefaultMinutes = true;
            }
        }
        for (int i = 1; i <= 12; i++)
        {
            dismissField.addItem("" + i + " weeks");
            dismissValues.add(i * 60 * 24 * 7);
            if (dismissMinutes == (i * 60 * 24 * 7))
            {
                dismissField.setSelectedIndex(dismissValues
                    .size() - 1);
                hasDefaultMinutes = true;
            }
        }
        if (!hasDefaultMinutes)
        {
            dismissValues.add(0, dismissMinutes);
            dismissField.insertItem("" + dismissMinutes
                + " minutes", 0);
            dismissField.setSelectedIndex(0);
        }
        panel.add(dismissField, "dismissField");
        final TextArea messageField = new TextArea();
        messageField.setWidth("100%");
        messageField.setVisibleLines(6);
        panel.add(messageField, "messageField");
        DockPanel buttonPanel = new DockPanel();
        Button sendButton = new Button("Send");
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                dialog.hide();
            }
        });
        sendButton.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                dialog.hide();
                final DialogBox sendingDialog = new DialogBox();
                sendingDialog.setText("Sending...");
                sendingDialog.setWidget(new Label(
                    "The notification is being sent..."));
                sendingDialog.center();
                sendingDialog.show();
                AdminInterface.authLink
                    .sendUserNotification(to, subjectField
                        .getText(), messageField.getText(),
                        priorityField
                            .getValue(priorityField
                                .getSelectedIndex()),
                        dismissValues.get(dismissField
                            .getSelectedIndex()),
                        new AsyncCallback<Void>()
                        {
                            
                            public void onFailure(
                                Throwable caught)
                            {
                                sendingDialog.hide();
                                dialog.show();
                                Window
                                    .alert("The notification failed to send for this reason: "
                                        + ((caught instanceof NotificationException) ? ""
                                            : caught
                                                .getClass()
                                                .getName()
                                                + " - ")
                                        + caught
                                            .getMessage());
                            }
                            
                            public void onSuccess(
                                Void result)
                            {
                                sendingDialog.hide();
                                AdminInterface
                                    .showInfoBox(
                                        "Notification sent",
                                        "The notification has been successfully sent.");
                                /*
                                 * The above message should probably be changed
                                 * to reflect that fact that the notification
                                 * has only been placed in OGRS's task pool, and
                                 * that it may not get sent immediately.
                                 */
                            }
                        });
            }
        });
        buttonPanel.add(sendButton, buttonPanel.WEST);
        buttonPanel.add(cancelButton, buttonPanel.EAST);
        panel.add(buttonPanel, "buttons");
        dialog.setWidget(panel);
        dialog.center();
        dialog.show();
    }
}
