package net.sf.opengroove.client.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CustomLoaderObjectReader extends ObjectInputStream
{
	private ClassLoader loader;

	public CustomLoaderObjectReader(InputStream in, ClassLoader loader)
			throws IOException
	{
		super(in);
		this.loader = loader;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException
	{
		return Class.forName(desc.getName(), true, loader);
	}

}
