package net.sf.opengroove.projects.filleditor;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A class that holds the representation of a fill image. A .fdsc file is a
 * serialized FillImage.
 * 
 * @author Alexander Boyd
 * 
 */
public class FillImage implements java.io.Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 5984362356975907793L;
    public Color background;
    public int width;
    public int height;
    
    /**
     * Draws this FillImage onto the graphics object specified.
     * 
     * @param g
     *            The graphics object to draw this image onto
     */
    public void draw(Graphics2D g)
    {
        
    }
    
    private Region[] regions;
}
