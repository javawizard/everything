package net.sf.opengroove.client.plugins;

public interface ExtensionPoint<E extends Extension>
{
    public void registerExtension(E extension);
}
