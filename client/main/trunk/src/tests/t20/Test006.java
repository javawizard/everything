package tests.t20;

import java.util.ArrayList;

public class Test006
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ArrayList listg = new ArrayList();
		ArrayList<String> list = listg;
		System.out.println("OK");
		list.add("hi");
		System.out.println("OK 2");
		listg.add(new Object());
		System.out.println("OK 3");
		String s = list.get(1);
		System.out.println("OK 4");
	}

}
