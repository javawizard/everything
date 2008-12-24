package org.bzflag.jzapi.examples.swingposition;

import javax.swing.JFrame;

/**
 * This plugin opens a Swing window, with a square in it that shows the current
 * location of each player. It refreshes itself once every second. Players that
 * are paused are drawn as light-gray, and dead players are not drawn.
 * 
 * @author Alexander Boyd
 * 
 */
public class SwingPositionPlugin
{
    private static JFrame frame;
    
    private static SwingPositionComponent component;
    
    public static void load(String args)
    {
        
    }
    
    public static void unload()
    {
        frame.dispose();
    }
    
}
