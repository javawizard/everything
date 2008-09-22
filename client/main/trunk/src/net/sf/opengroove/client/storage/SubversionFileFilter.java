package net.sf.opengroove.client.storage;

import java.io.File;
import java.io.FileFilter;

public class SubversionFileFilter implements FileFilter
{

	public boolean accept(File pathname)
	{
		return !pathname.getName().equals(".svn");
	}

}
