package tests;

import java.net.JarURLConnection;
import java.net.URL;

public class Test016
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable
	{
		// tests if getting the mf of a jar url downloads the entire jar or just
		// the portion with the mf
		//
		// the url that is used is the url for the convergia update jar file
		URL url = new URL("jar:http://trivergia.com:8080/convergiaupdates.jar!/");
		System.out.println("creating connection");
		JarURLConnection con = (JarURLConnection) url.openConnection();
		System.out.println("getting manifest");
		System.out.println(con.getManifest());
		System.out.println("done");
	}

}
