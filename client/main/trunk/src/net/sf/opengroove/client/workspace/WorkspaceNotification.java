package net.sf.opengroove.client.workspace;

import java.awt.Component;

import net.sf.opengroove.client.notification.TaskbarNotification;


public class WorkspaceNotification implements TaskbarNotification
{
	private TaskbarNotification notification;
	
	private String workspaceId;
	
	public WorkspaceNotification(String workspaceId, TaskbarNotification notification)
	{
		this.workspaceId = workspaceId;
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
	
	public String getWorkspaceId()
	{
		return workspaceId;
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
		result = PRIME * result + ((workspaceId == null) ? 0 : workspaceId.hashCode());
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
		final WorkspaceNotification other = (WorkspaceNotification) obj;
		if (notification == null)
		{
			if (other.notification != null)
				return false;
		} else if (!notification.equals(other.notification))
			return false;
		if (workspaceId == null)
		{
			if (other.workspaceId != null)
				return false;
		} else if (!workspaceId.equals(other.workspaceId))
			return false;
		return true;
	}

}
