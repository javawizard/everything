package net.sf.opengroove.projects.filleditor.bezier;

import java.lang.String;
import java.lang.StringBuffer;
import java.util.Stack;
import java.lang.Math;
import java.awt.*;
import java.applet.*;

/* Copyright 1997 Bill Casselman
 University of British Columbia
 cass@math.ubc.ca */

/* These classes are to be used in Bezier curve construction 
 the point is that good Bezier curve drawing requires 
 a finer scale than pixels */

/* but they are internal to Curve - how do I arrange that? */
public class BezierUtils
{
    /**
     * Creates a quadratic bezier curve. The resulting array contains the points
     * for the curve.
     * 
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return
     */
    public static Point[] createQuadCurve(Point p1,
        Point p2, Point p3, Point p4)
    {
        PathSegment segment = new PathSegment();
        segment.add(p1);
        Curve.addCurve(segment, new RealPoint(p2.x, p2.y),
            new RealPoint(p3.x, p3.y), new RealPoint(p4.x,
                p4.y));
        Point[] points = new Point[segment.n];
        for (int i = 0; i < points.length; i++)
        {
            points[i] = new Point(segment.x[i],
                segment.y[i]);
        }
        return points;
    }
    
    /**
     * Creates a cubic bezier curve. Currently, this just calls
     * <code>createQuadCurve(p1,p2,p2,p3)</code>
     * 
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    public static Point[] createCubicCurve(Point p1,
        Point p2, Point p3)
    {
        return createQuadCurve(p1, p2, p2, p3);
    }
    
    public static class ScaledInt
    {
        // scaled integers interpreted as m.n where n has 6 bits
        static int scale = 6;
        // mask = (1 << scale) - 1: masks off the fractional part
        static int mask = ((1 << scale) - 1);
        // res = 1/2^scale
        static double resolution = 1.0 / (double) (1 << scale);
        
        // use floats instead of doubles?
        // toReal instead of toDouble?
        
        public static int toScaledInt(int n)
        {
            return (n << scale);
        }
        
        public static int toScaledInt(double f)
        {
            f = f * ((double) (1 << scale));
            return ((int) f);
        }
        
        public static int average(int x, int y)
        {
            int n = (x + y) >> 1;
            return (n);
        }
        
        // t of the way from x to y
        public static int average(int x, int y, double t)
        {
            double x0 = toDouble(x), y0 = toDouble(y);
            double z0 = (1.0 - t) * x0 + t * y0;
            int z = toScaledInt(z0);
            return (z);
        }
        
        public static int toInteger(int x)
        {
            return (x >> scale);
        }
        
        public static double toDouble(int y)
        {
            double x = ((double) y)
                / ((double) (1 << scale));
            return (x);
        }
        
        public static float toFloat(int y)
        {
            float x = ((float) y) / ((float) (1 << scale));
            return (x);
        }
        
        public static String toString(int n)
        {
            return (Real.toString(toDouble(n)));
        }
    }
    
    public static class ScaledPoint
    {
        int x, y;
        
        public ScaledPoint(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
        
        public ScaledPoint(RealPoint P)
        {
            x = ScaledInt.toScaledInt(P.x);
            y = ScaledInt.toScaledInt(P.y);
        }
        
        public ScaledPoint(double x, double y)
        {
            this.x = ScaledInt.toScaledInt(x);
            this.y = ScaledInt.toScaledInt(y);
        }
        
        public String toString()
        {
            return ("(" + ScaledInt.toString(x) + ", "
                + ScaledInt.toString(y) + ")");
        }
        
        static public ScaledPoint average(ScaledPoint p,
            ScaledPoint q)
        {
            int x = ScaledInt.average(p.x, q.x);
            int y = ScaledInt.average(p.y, q.y);
            ScaledPoint r = new ScaledPoint(x, y);
            return (r);
        }
        
        // t of the way from p to q
        static public ScaledPoint average(ScaledPoint p,
            ScaledPoint q, double t)
        {
            int x = ScaledInt.average(p.x, q.x, t);
            int y = ScaledInt.average(p.y, q.y, t);
            ScaledPoint r = new ScaledPoint(x, y);
            return (r);
        }
    }
    
    public static class ControlSet
    {
        ScaledPoint point0, point1, point2, point3;
        
        // for debugging:
        // double t0;
        // double t1;
        public ControlSet(ScaledPoint p0, ScaledPoint p1,
            ScaledPoint p2, ScaledPoint p3)
        {
            // t0 = 0.0;
            // t1 = 1.0;
            point0 = p0;
            point1 = p1;
            point2 = p2;
            point3 = p3;
        }
        
        public double breadth()
        {
            double A, B, C;
            float x0, x1, x2, x3, y0, y1, y2, y3, y4;
            float d, d1, tmp;
            
            x0 = ScaledInt.toFloat(point0.x);
            y0 = ScaledInt.toFloat(point0.y);
            x1 = ScaledInt.toFloat(point1.x);
            y1 = ScaledInt.toFloat(point1.y);
            x2 = ScaledInt.toFloat(point2.x);
            y2 = ScaledInt.toFloat(point2.y);
            x3 = ScaledInt.toFloat(point3.x);
            y3 = ScaledInt.toFloat(point3.y);
            
            if (Math.abs(x0 - x3) < ScaledInt.resolution
                && Math.abs(y0 - y0) < ScaledInt.resolution)
            {
                // end points coincide for practical purposes
                d = Math.abs(x1 - x0) + Math.abs(y1 - y0);
                d1 = Math.abs(x2 - x0) + Math.abs(y2 - y0);
                if (d1 > d)
                    return (d1);
                else
                    return (d);
            }
            A = y0 - y3;
            B = x3 - x0;
            tmp = (float) Math.sqrt(A * A + B * B);
            // now tmp != 0
            A = A;
            B = B;
            C = (x3 * y0 - x0 * y3);
            d = (float) Math.abs(A * x2 + B * y2 - C) / tmp;
            d1 = (float) Math.abs(A * x1 + B * y1 - C)
                / tmp;
            if (d > d1)
                return (d);
            else
                return (d1);
        }
        
        // returns the second half, resetting this to first half
        public ControlSet bisect()
        {
            ControlSet c;
            
            ScaledPoint P01, P12, P23, P012, P123, P0123;
            P01 = ScaledPoint.average(point0, point1);
            P12 = ScaledPoint.average(point1, point2);
            P23 = ScaledPoint.average(point2, point3);
            P012 = ScaledPoint.average(P01, P12);
            P123 = ScaledPoint.average(P12, P23);
            P0123 = ScaledPoint.average(P012, P123);
            c = new ControlSet(P0123, P123, P23, point3);
            // c.t1 = t1;
            // c.t0 = 0.5*t0 + 0.5*t1;
            point1 = P01;
            point2 = P012;
            point3 = P0123;
            // t1 = c.t0;
            return (c);
        }
        
        // in this case returns first half, sets this = second half
        public ControlSet bisect(double t)
        {
            ControlSet c;
            ScaledPoint P01, P12, P23, P012, P123, P0123;
            P01 = ScaledPoint.average(point0, point1, t);
            P12 = ScaledPoint.average(point1, point2, t);
            P23 = ScaledPoint.average(point2, point3, t);
            P012 = ScaledPoint.average(P01, P12, t);
            P123 = ScaledPoint.average(P12, P23, t);
            P0123 = ScaledPoint.average(P012, P123, t);
            
            c = new ControlSet(point0, P01, P012, P0123);
            // c.t0 = t0;
            // c.t1 = (1.0 - t)*t0 + t*t1;
            // t0 = c.t1;
            point0 = P0123;
            point1 = P123;
            point2 = P23;
            
            return (c);
        }
        
        public String toString()
        {
            // return("(" + this.t0 + ":" + this.t1 + ") " +
            return ("[" + point0.toString()
                + point1.toString() + point2.toString()
                + point3.toString() + "]");
        }
    }
    
    /* this is public */
    public static class Curve
    {
        static double resolution = 0.50;
        
        // adds points to the already existing path segment
        public static void addCurve(PathSegment path,
            RealPoint p1, RealPoint p2, RealPoint p3)
        {
            ScaledPoint P0, P1, P2, P3;
            int x0, y0;
            int h0 = 0, h1 = 0;
            ControlSet s0[] = new ControlSet[64];
            ControlSet s1[] = new ControlSet[64];
            
            // long t0 = System.currentTimeMillis();
            x0 = ScaledInt.toScaledInt(path.x[path.n - 1]);
            y0 = ScaledInt.toScaledInt(path.y[path.n - 1]);
            P0 = new ScaledPoint(x0, y0);
            P1 = new ScaledPoint(p1);
            P2 = new ScaledPoint(p2);
            P3 = new ScaledPoint(p3);
            
            ControlSet c = new ControlSet(P0, P1, P2, P3);
            
            int n;
            ControlSet d;
            double t;
            
            c = new ControlSet(c.point0, c.point1,
                c.point2, c.point3);
            // System.out.println("pushing " + c.toString());
            s1[h1++] = c;
            // s1.push(c);
            // while (!s1.empty()) {
            while (h1 > 0)
            {
                // c = s1.pop();
                c = s1[--h1];
                // System.out.println("popping " + c.toString());
                if (c.breadth() > resolution)
                {
                    d = c.bisect();
                    // System.out.println("pushing " + c.toString());
                    // System.out.println("pushing " + d.toString());
                    // put second half on top
                    
                    // s1.push(c);
                    // s1.push(d);
                    s1[h1++] = c;
                    s1[h1++] = d;
                }
                else
                {
                    // put c on list for polygon construction
                    s0[h0++] = c;
                }
            }
            
            // now s0 holds the stuff to draw
            // adding to a polygon already there
            n = 0;
            // while (!s0.empty()) {
            while (h0 > 0)
            {
                // c = s0.pop();
                c = s0[--h0];
                int p = ScaledInt.toInteger(c.point3.x);
                int q = ScaledInt.toInteger(c.point3.y);
                path.add(p, q);
                n++;
            }
        }
    }
    
}
