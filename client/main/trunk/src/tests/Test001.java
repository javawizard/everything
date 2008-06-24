package tests;

import net.sf.opengroove.client.com.Communicator;
import net.sf.opengroove.client.com.LowLevelCommunicator;

public class Test001
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable
	{
		LowLevelCommunicator lcom = new LowLevelCommunicator(false);
		Communicator com = new Communicator(lcom);
		lcom.authenticate("test1", "password");
		String md = com.getUserMetadata("test2");
	}

}
