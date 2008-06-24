package net.sf.opengroove.client.toolworkspace;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

public class ToolWrapper implements Serializable
{
	private static final long serialVersionUID = -1723300100110474377L;

	private String id;

	private String name;

	private String typeId;

	private File datastore;

	private HashMap<String, String> info = new HashMap<String, String>();

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

	// tool appears in the tool tabbed pane
	private int index;

	private transient Tool tool;

	private transient ToolManager manager;

	private transient Properties pluginMetadata;

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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		name = name.replaceAll("[^a-zA-Z0-9 ]", "");// only allow alphanumeric
		// characters and spaces
		this.name = name;
	}

	public Tool getTool()
	{
		return tool;
	}

	public void setTool(Tool tool)
	{
		this.tool = tool;
	}

	public String getTypeId()
	{
		return typeId;
	}

	public void setTypeId(String typeId)
	{
		this.typeId = typeId;
	}

	public ToolWorkspace getWorkspace()
	{
		return manager.getWorkspace();
	}

	public ToolManager getManager()
	{
		return manager;
	}

	public void setManager(ToolManager manager)
	{
		this.manager = manager;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public Properties getPluginMetadata()
	{
		return pluginMetadata;
	}

	public void setPluginMetadata(Properties pluginMetadata)
	{
		this.pluginMetadata = pluginMetadata;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
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
		final ToolWrapper other = (ToolWrapper) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
