package net.sf.opengroove.client.notification;

import java.awt.Component;

import javax.swing.JComponent;

/**
 * 
 * @author Alexander Boyd
 * 
 */
public interface TaskbarNotification
{
	/**
	 * this method should return the component that should be placed in the
	 * notification frame. it is imperative that this method return the same
	 * component each time this method is called. it is also imperative that the
	 * component never change size.
	 * 
	 * @return
	 */
	public Component getComponent();

	/**
	 * returns true if this is an alert. if this is an alert, the taskbar icon
	 * will flash while this is in the notification frame. basically, an alert
	 * is intended to alert the user that they need to click on the component,
	 * for example, a message has been received. an example of a NON alert would
	 * be a notification about that the message is in progress of sending to
	 * someone else.
	 * 
	 * @return
	 */
	public boolean isAlert();

	/**
	 * returns true if this alert is one time only. If an alert is one time
	 * only, then it will be removed from the TaskbarNotificationFrame when it
	 * fades out. an example of a one time only alert might be that so-and-so
	 * has just come online.
	 * 
	 * @return
	 */
	public boolean isOneTimeOnly();

	/**
	 * called when this alert is clicked. the component returned from
	 * getComponent() has a mouse listener added to it and when it is clicked
	 * then this method is called. an alternate way to listen for click events
	 * would be to add a mouse listener to the component returned yourself.
	 */
	public void clicked();

	/**
	 * called when the mouse moves over this alert.
	 * 
	 */
	public void mouseOver();

	/**
	 * called when the mouse moves out of this alert.
	 * 
	 */
	public void mouseOut();
}
