package net.sf.convergia.client;

import java.awt.Component;

import javax.swing.JProgressBar;

import net.sf.convergia.client.notification.TaskbarNotification;


public class UpdateNotification implements TaskbarNotification
{
	private JProgressBar progress;
	public UpdateNotification()
	{
		progress = new JProgressBar();
		progress.setBorderPainted(false);
		progress.setStringPainted(true);
		progress.setString("OpenGroove is downloading updates...");
	}
	
	public JProgressBar getProgressBar()
	{
		return progress;
	}

	public void clicked()
	{
		// TODO Auto-generated method stub

	}

	public Component getComponent()
	{
		return progress;
	}

	public boolean isAlert()
	{
		return false;
	}

	public boolean isOneTimeOnly()
	{
		return false;
	}

	public void mouseOut()
	{
		// TODO Auto-generated method stub

	}

	public void mouseOver()
	{
		// TODO Auto-generated method stub

	}

}
