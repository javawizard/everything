package net.sf.opengroove.client.toolworkspace;

import java.awt.Component;

import net.sf.opengroove.client.notification.TaskbarNotification;


public class ToolNotification implements TaskbarNotification
{
	private TaskbarNotification notification;
	
	private String toolId;
	
	public ToolNotification(String toolId, TaskbarNotification notification)
	{
		this.toolId = toolId;
		this.notification = notification;
	}

	public void clicked()
	{
		notification.clicked();
	}

	public Component getComponent()
	{
		return notification.getComponent();
	}

	public boolean isAlert()
	{
		return notification.isAlert();
	}

	public boolean isOneTimeOnly()
	{
		return notification.isOneTimeOnly();
	}

	public void mouseOut()
	{
		notification.mouseOut();
	}

	public void mouseOver()
	{
		notification.mouseOver();
	}
	
	public String getToolId()
	{
		return toolId;
	}
	
	public TaskbarNotification getNotification()
	{
		return notification;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((notification == null) ? 0 : notification.hashCode());
		result = PRIME * result + ((toolId == null) ? 0 : toolId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ToolNotification other = (ToolNotification) obj;
		if (notification == null)
		{
			if (other.notification != null)
				return false;
		} else if (!notification.equals(other.notification))
			return false;
		if (toolId == null)
		{
			if (other.toolId != null)
				return false;
		} else if (!toolId.equals(other.toolId))
			return false;
		return true;
	}

}
