package net.sf.opengroove.client.messaging;

import java.awt.Component;
import java.awt.Cursor;

import javax.swing.JLabel;

import org.opengroove.g4.common.messaging.MessageHeader;
import org.opengroove.g4.common.user.Userid;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.notification.TaskbarNotification;
import net.sf.opengroove.client.ui.frames.ComposeMessageFrame;

public class UserMessageNotification implements TaskbarNotification
{
    private MessageHeader message;
    private Userid user;
    private JLabel label;
    
    public void clicked()
    {
        /*
         * Tell the stored message manager to open this inbound message. The
         * stored message manager will open a dialog showing that the message is
         * opening, then it will go retrieve the message. Since the message is
         * an unread one, the stored message manager will rename it to a read
         * one and then open it.
         */
        ComposeMessageFrame.showComposeMessageFrame()
    }
    
    public Component getComponent()
    {
        /*
         * Component is a label with the message information for now, and the
         * cursor set to be a pointer. In the future, it will also contain a
         * button to clear the message's notification, and maybe a tooltip that
         * shows stuff like the recipient list and a short amount of the
         * message's body.
         */
        return label;
    }
    
    public boolean isAlert()
    {
        return true;
    }
    
    public boolean isOneTimeOnly()
    {
        return false;
    }
    
    public void mouseOut()
    {
        label.setCursor(Cursor.getDefaultCursor());
    }
    
    public void mouseOver()
    {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    public UserMessageNotification(MessageHeader message, Userid user, JLabel label)
    {
        super();
        this.message = message;
        this.user = user;
        this.label = label;
        String labelString =
            "<html>Message from <b>" + message.getSender().toString() + "</b> to you";
        if (message.getRecipients().length > 1)
        {
            labelString += " and " + (message.getRecipients().length - 1) + " other";
            if (message.getRecipients().length != 2)
                labelString += "s";
        }
        if (message.getSubject() != null && !message.getSubject().trim().equals(""))
        {
            labelString += ": <br/>" + message.getSubject();
        }
        label = new JLabel(labelString);
    }
}
