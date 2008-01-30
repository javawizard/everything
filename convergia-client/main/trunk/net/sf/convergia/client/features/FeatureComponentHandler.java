package net.sf.convergia.client.features;

public interface FeatureComponentHandler<E>
{
	public FeatureContext registerComponent(String id, E component);
}
