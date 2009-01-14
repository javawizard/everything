package net.sf.opengroove.projects.filleditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

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
    public ArrayList<Region> regions = new ArrayList<Region>();
    
    /**
     * Draws this FillImage onto the graphics object specified, scaling to the
     * width and height specified if necessary.
     * 
     * @param g
     *            The graphics object to draw this image onto
     */
    public void draw(Graphics2D g, int width, int height)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        double scaleX = (width * 1.0) / (this.width * 1.0);
        double scaleY = (height * 1.0)
            / (this.height * 1.0);
        // TODO: create a transform off of the scale values just computed to
        // scale the graphics
        AffineTransform oldTransform = g.getTransform();
        AffineTransform transform = AffineTransform
            .getScaleInstance(scaleX, scaleY);
        g.transform(transform);
        g.setColor(background);
        g.fillRect(0, 0, this.width, this.height);
        for (Region region : regions)
        {
            region.draw(g, this.width, this.height);
        }
        g.setTransform(oldTransform);
    }
}
