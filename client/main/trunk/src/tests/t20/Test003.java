package tests.t20;

import net.sf.opengroove.client.OpenGroove;

public class Test003
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// generates an id.
		OpenGroove.username = "testuser";
		System.out.println(OpenGroove.generateId());
	}

}
