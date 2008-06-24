package net.sf.opengroove.client.features;

public interface ComponentFactory<E>
{
	public E create(ComponentFactoryContext context);
}
