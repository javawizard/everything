package tests;

import java.io.Serializable;

import net.sf.convergia.client.tools.games.j3dpencils.renderer.SerializingCloner;


public class Test011
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Serializable object = new Serializable()
		{
		};
		Serializable object2 = SerializingCloner.clone(object);
		System.out.println("" + object.hashCode());
		System.out.println("" + object2.hashCode());
		System.out.println(object == object2);
	}

}
