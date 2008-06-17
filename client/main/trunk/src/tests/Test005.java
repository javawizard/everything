package tests;

import java.util.ArrayList;

public class Test005
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ArrayList<String> lista = new ArrayList<String>();
		ArrayList<String> listb = new ArrayList<String>();
		lista.add("a1");
		lista.add("a2");
		lista.add("a3");
		lista.add("a4");
		listb.add("a2");
		listb.add("a4");
		listb.add("a5");
		lista.addAll(listb);
		lista.retainAll(listb);
		for (String s : lista)
		{
			System.out.println(s);
		}
	}

}
