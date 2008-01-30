package tests;

import net.sf.convergia.client.InTouch3;

public class Test003
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// generates an id.
		InTouch3.username = "testuser";
		System.out.println(InTouch3.generateId());
	}

}
