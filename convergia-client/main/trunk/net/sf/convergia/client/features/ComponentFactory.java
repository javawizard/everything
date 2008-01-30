package net.sf.convergia.client.features;

public interface ComponentFactory<E>
{
	public E create(ComponentFactoryContext context);
}
