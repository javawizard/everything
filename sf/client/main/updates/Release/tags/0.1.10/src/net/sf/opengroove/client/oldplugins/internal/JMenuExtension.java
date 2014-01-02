package net.sf.opengroove.client.oldplugins.internal;

import javax.swing.JMenu;

import net.sf.opengroove.client.oldplugins.Extension;

/**
 * An extension interface that can be used for extension points that should be
 * supplied with a JMenu. This could be used for an extension point that would
 * add a menu item to a particular menu, for example.
 * 
 * @author Alexander Boyd
 * 
 */
public interface JMenuExtension extends Extension
{
    /**
     * Creates a menu to add. This will only be called once in the lifetime of
     * the plugin.
     * 
     * @return
     */
    public JMenu getMenu();
}
