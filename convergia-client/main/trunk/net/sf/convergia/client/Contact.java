package net.sf.convergia.client;

import java.io.Serializable;
import java.util.HashMap;

public class Contact implements Serializable
{
	private static final long serialVersionUID = -2910467169227988997L;

	private String username;

	private HashMap<String, String> metadata = new HashMap<String, String>();

	public HashMap<String, String> getMetadata()
	{
		return metadata;
	}

	public void setMetadata(HashMap<String, String> metadata)
	{
		this.metadata = metadata;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}
}
