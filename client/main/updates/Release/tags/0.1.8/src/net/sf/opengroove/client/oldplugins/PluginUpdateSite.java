package net.sf.opengroove.client.oldplugins;

import java.net.URL;

public class PluginUpdateSite
{
	private int versionIndex;

	private String versionString;

	private URL url;

	private URL websiteUrl;

	private String name;

	private String description;

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public int getVersionIndex()
	{
		return versionIndex;
	}

	public void setVersionIndex(int versionIndex)
	{
		this.versionIndex = versionIndex;
	}

	public String getVersionString()
	{
		return versionString;
	}

	public void setVersionString(String versionString)
	{
		this.versionString = versionString;
	}

	public URL getWebsiteUrl()
	{
		return websiteUrl;
	}

	public void setWebsiteUrl(URL websiteUrl)
	{
		this.websiteUrl = websiteUrl;
	}
}
