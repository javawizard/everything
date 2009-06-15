package net.sf.opengroove.client.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TextManager
{
	private static Properties keys;

	private static boolean isKeyNameDisplay = false;
	static
	{
		reloadKeys();
	}

	public static void setKeyNameDisplay(boolean k)
	{
		isKeyNameDisplay = k;
	}

	public static String text(String key)
	{
		return text(key, new String[0]);
	}

	public static synchronized String text(String key, String... params)
	{
		String value = keys.getProperty(key);
		if (value == null)
		{
			value = "???" + key + "???";
			keys.setProperty(key, value);
			try
			{
				keys.store(new FileOutputStream("lkeys.properties"),
						"written by TextManager");

			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
		} else if (isKeyNameDisplay)
			value += " (???" + key + "???)";
		for(int i = 0; i < params.length; i++)
		{
			value = value.replace("{" + i + "}", params[i]);
		}
		return value;
	}

	private static synchronized void reloadKeys()
	{
		keys = new Properties();
		try
		{
			keys.load(new FileInputStream(new File("lkeys.properties")));
		} catch (FileNotFoundException e)
		{
			// TODO Dec 1, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Dec 1, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		} catch (IOException e)
		{
			// TODO Dec 1, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Dec 1, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}
}
