package net.sf.opengroove.projects.filleditor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;

import net.sf.opengroove.projects.filleditor.bezier.BezierUtils;

public class Region implements java.io.Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 3334537932557520969L;
    /**
     * The points in this region
     */
    public ArrayList<Point> points = new ArrayList<Point>();
    /**
     * The starting indexes of any sets of 3 points that should define a bezier
     * curve. For example, if there are 6 points, and this array contains one
     * element, namely the number 1, then the second, third, and fourth points
     * will define a bezier curve, while the rest of the points will just define
     * straight lines.
     */
    public ArrayList<Integer> cubicBezier = new ArrayList<Integer>();
    public FillPlugin plugin;
    
    public void draw(Graphics2D g)
    {
        Polygon bounds = getBounds();
        g.setClip(bounds);
        plugin.draw(this, g);
    }
    
    /**
     * Creates a polygon that represents the bounds of this region. Bezier
     * curves within this region are replaced by a set of points that define
     * them before returning from this method.
     * 
     * @return The points that define this region, with bezier curves resolved
     */
    private Polygon getBounds()
    {
        ArrayList<Point> newPoints = new ArrayList<Point>();
        for (int i = 0; i < points.size(); i++)
        {
            if (cubicBezier.contains(i)
                && (i + 2) < points.size())
            {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                Point p3 = points.get(i + 2);
                i += 2;
                newPoints.addAll(Arrays.asList(BezierUtils
                    .createCubicCurve(p1, p2, p3)));
            }
            else
            {
                newPoints.add(points.get(i));
            }
        }
        Polygon polygon = new Polygon(new int[newPoints
            .size()], new int[newPoints.size()], 0);
        for (Point point : newPoints)
        {
            polygon.addPoint(point.x, point.y);
        }
        return polygon;
    }
}
