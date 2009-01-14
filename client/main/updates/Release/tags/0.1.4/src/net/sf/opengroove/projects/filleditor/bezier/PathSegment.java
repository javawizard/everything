package net.sf.opengroove.projects.filleditor.bezier;

import java.lang.*;
import java.awt.*;
import java.applet.*;

/* Each path is an array of segmentsa.
 Each segment is an array of coordinates.
 Stroking a path means drawing lines along the pieces.
 Filling it means turning each piece into a polygon. */

class PathSegment
{
    int maxn = 64;
    int x[];
    int y[];
    int n;
    
    public PathSegment()
    {
        x = new int[maxn];
        y = new int[maxn];
        n = 0;
    }
    
    void add(Point P)
    {
        add(P.x, P.y);
    }
    
    void add(int a, int b)
    {
        if (n >= maxn)
        {
            int newx[] = new int[maxn + 32];
            int newy[] = new int[maxn + 32];
            maxn += 32;
            for (int i = 0; i < n; i++)
            {
                newx[i] = x[i];
                newy[i] = y[i];
            }
            x = newx;
            y = newy;
        }
        x[n] = a;
        y[n++] = b;
    }
    
    Polygon toPolygon()
    {
        return (new Polygon(x, y, n));
    }
    
}
