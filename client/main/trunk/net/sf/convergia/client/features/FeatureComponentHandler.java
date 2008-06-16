package net.sf.convergia.client.features;

public interface FeatureComponentHandler<E>
{
	public void registerComponent(String id, E component);
}
