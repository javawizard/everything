package tests;

import net.sf.opengroove.client.Convergia;

public class Test003
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// generates an id.
		Convergia.username = "testuser";
		System.out.println(Convergia.generateId());
	}

}
