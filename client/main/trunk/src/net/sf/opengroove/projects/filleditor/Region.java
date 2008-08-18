package net.sf.opengroove.projects.filleditor;

import java.awt.Graphics2D;
import java.awt.Polygon;

import com.jhlabs.image.CellularFilter.Point;

public class Region implements java.io.Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 3334537932557520969L;
    /**
     * The points in this region
     */
    public Point[] points;
    /**
     * The starting indexes of any sets of 3 points that should define a bezier
     * curve. For example, if there are 6 points, and this array contains one
     * element, namely the number 1, then the second, third, and fourth points
     * will define a bezier curve, while the rest of the points will just define
     * straight lines.
     */
    public int[] cubicBezier;
    public FillPlugin plugin;
    
    public void draw(Graphics2D g, double scaleX,
        double scaleY)
    {
        
    }
    
    private Polygon getBounds()
    {
        Polygon polygon = new Polygon();
    }
}
