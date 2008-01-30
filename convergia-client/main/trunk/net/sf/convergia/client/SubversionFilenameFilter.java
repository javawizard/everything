package net.sf.convergia.client;

import java.io.File;
import java.io.FilenameFilter;

public class SubversionFilenameFilter implements FilenameFilter
{
	private SubversionFileFilter filefilter;

	public SubversionFilenameFilter()
	{
		this.filefilter = new SubversionFileFilter();
	}

	public boolean accept(File dir, String name)
	{
		// TODO Auto-generated method stub
		return filefilter.accept(new File(dir, name));
	}

}
