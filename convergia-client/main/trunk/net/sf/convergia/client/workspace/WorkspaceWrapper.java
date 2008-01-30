package net.sf.convergia.client.workspace;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import net.sf.convergia.client.Convergia;
import net.sf.convergia.client.frames.ConfigureWorkspaceDialog;


public class WorkspaceWrapper implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6525261980589892668L;

	private String id;

	private HashMap<String, String> info = new HashMap<String, String>();

	private transient Workspace workspace;

	private File datastore;

	private String typeId;

	private String name;

	// if mine = true, this is the allowed users that the user has chosen, and
	// should be set into the user's metadata.
	// if mine = false, this is just a cache of what was read from the owner's
	// metadata, and should be regularly updated.
	private List<String> allowedUsers = SetList.cs(String.class);

	// same as for allowedUsers
	private List<String> participants = SetList.cs(String.class);

	public File getDatastore()
	{
		return datastore;
	}

	public void setDatastore(File datastore)
	{
		this.datastore = datastore;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public boolean isMine()
	{
		return WorkspaceManager.getWorkspaceCreator(id).equals(
				Convergia.username);
	}

	public String getTypeId()
	{
		return typeId;
	}

	public void setTypeId(String typeId)
	{
		this.typeId = typeId;
	}

	public Workspace getWorkspace()
	{
		return workspace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final WorkspaceWrapper other = (WorkspaceWrapper) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void setWorkspace(Workspace workspace)
	{
		this.workspace = workspace;
	}

	public List<String> getAllowedUsers()
	{
		return allowedUsers;
	}

	public List<String> getParticipants()
	{
		return participants;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public HashMap<String, String> getInfo()
	{
		return info;
	}

	public String getInfo(String user)
	{
		return info.get(user);
	}

	public void setInfo(String user, String ti)
	{
		info.put(user, ti);
	}

	public void setInfo(HashMap<String, String> info)
	{
		this.info = info;
	}

	public Properties getPluginMetadata()
	{
		// TODO Auto-generated method stub
		return workspace.getPluginMetadata();
	}
}
