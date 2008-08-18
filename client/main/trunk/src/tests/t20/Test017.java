package tests.t20;

import java.io.File;

public class Test017
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		File file = new File("tests/t017f2.txt");
		file.deleteOnExit();
		file.renameTo(new File("tests/t017f1.txt"));
		new File("tests/t017f1.txt").deleteOnExit();
	}

}
