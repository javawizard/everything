package net.sf.convergia.client.notification;

import java.awt.Component;

/**
 * a TaskbarNotification that returns the component passed to the
 * constructor,and the status as to whether it is an alert, and the rest of the
 * methods do nothing.
 * 
 * @author Alexander Boyd
 * 
 */
public class NotificationAdapter implements TaskbarNotification
{
	private Component component;

	private boolean isAlert;

	private boolean isOneTimeOnly;

	public NotificationAdapter(Component component, boolean isAlert,
			boolean isOneTimeOnly)
	{
		super();
		this.component = component;
		this.isAlert = isAlert;
		this.isOneTimeOnly = isOneTimeOnly;
	}

	public NotificationAdapter(Component component, boolean isAlert)
	{
		this(component, isAlert, false);
	}

	public void clicked()
	{
		// TODO Auto-generated method stub

	}

	public Component getComponent()
	{
		// TODO Auto-generated method stub
		return component;
	}

	public boolean isAlert()
	{
		// TODO Auto-generated method stub
		return isAlert;
	}

	public void mouseOut()
	{
		// TODO Auto-generated method stub

	}

	public void mouseOver()
	{
		// TODO Auto-generated method stub

	}

	public boolean isOneTimeOnly()
	{
		// TODO Auto-generated method stub
		return isOneTimeOnly;
	}

}
