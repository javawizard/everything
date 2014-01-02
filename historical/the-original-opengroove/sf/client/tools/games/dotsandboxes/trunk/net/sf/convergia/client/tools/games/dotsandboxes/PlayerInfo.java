package net.sf.convergia.client.tools.games.dotsandboxes;

import java.io.Serializable;

public class PlayerInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3714739097874305418L;

	private String idLetter;

	private String username;

	public String getIdLetter()
	{
		return idLetter;
	}

	public void setIdLetter(String idLetter)
	{
		this.idLetter = idLetter;
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
