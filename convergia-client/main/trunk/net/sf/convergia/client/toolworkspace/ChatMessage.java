package net.sf.convergia.client.toolworkspace;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.convergia.client.InTouch3;


public class ChatMessage
{
	private String id;
	private String message;
	private static final SimpleDateFormat dateformat = new SimpleDateFormat(InTouch3.getDateFormatString());
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public long getDate()
	{
		String[] split = id.split("\\-", 2);
		return Long.parseLong(split[0]);
	}
	public String getUser()
	{
		String[] split = id.split("\\-", 2);
		return split[1];
	}
	public String getDisplayMessage()
	{
		return getUser() + " (" + dateformat.format(new Date()) + "):\n" + message + "\n\n";
	}
	public String getTransmitString()
	{
		return getId() + "|" + getMessage();
	}
}
