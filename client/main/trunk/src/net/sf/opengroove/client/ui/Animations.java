package net.sf.opengroove.client.ui;

import java.awt.Point;

/**
 * This class contains utilities for doing general animations, as well as some
 * methods for preforming animations on swing components.
 * 
 * @author Alexander Boyd
 * 
 */
public class Animations
{
    /**
     * Generates a set of points that move the object at <code>start</code> to
     * <code>end</code> smoothly, in the number of steps specified.
     * 
     * @param start
     *            The start point
     * @param end
     *            The end point
     * @param steps
     *            The number of steps to generate, which must be at least 2
     * @return a set of points that can be followed to animate the transfer of
     *         the object at the start point to the end point. This will have a
     *         length equal to <code>steps</code>.
     */
    public static Point[] generateLineAnimation(
        Point start, Point end, int steps)
    {
        if (steps < 2)
            throw new IllegalArgumentException(
                "steps must be at least 2, but it was "
                    + steps);
        Point[] result = new Point[steps];
        double difX = end.x - start.x;
        double difY = end.y - start.y;
        double inc = 90.0 / ((steps - 1.0) * 1.0);
        for (int i = 0; i < steps; i++)
        {
            double pos = Math.sin(Math.toRadians(i * inc));
            result[i] = new Point(
                (int) (start.x + (difX * pos)),
                (int) (start.y + (difY * pos)));
        }
        return result;
    }
}
