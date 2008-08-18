package tests.t20;

import net.sf.opengroove.client.com.OldCommunicator;
import net.sf.opengroove.client.com.LowLevelCommunicator;

public class Test001
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable
	{
		LowLevelCommunicator lcom = new LowLevelCommunicator(false);
		OldCommunicator com = new OldCommunicator(lcom);
		lcom.authenticate("test1", "password");
		String md = com.getUserMetadata("test2");
	}

}
