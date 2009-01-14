package net.sf.opengroove.utils;

import java.io.File;

import net.sf.opengroove.client.storage.Storage;

public class LiveProperties
{
	private File file;

	/**
	 * creates a new LiveProperties. the file specified must either be a folder,
	 * or not exist.
	 * 
	 * @param file
	 */
	public LiveProperties(File file)
	{
		this.file = file;
		if (file.exists() && file.isFile())
			throw new IllegalArgumentException(
					"the file passed in must either be a folder, or not "
							+ "exist at all. the file object passed in references a file, not a folder.");
		if (!file.exists())
			file.mkdirs();
	}

	public String getProperty(String name)
	{
		try
		{
			return Storage.readFile(new File(file, name));
		} catch (Exception e)
		{
			return null;
		}
	}

	public void setProperty(String name, String value)
	{
		if (value == null)
			new File(file, name).delete();
		else
			Storage.writeFile(value, new File(file, name));
	}
}
