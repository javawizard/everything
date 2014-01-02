package net.sf.opengroove.projects.filleditor.bezier;

import java.awt.*;

class PlotCTM
{
    int width, height;
    double sx, sy;
    double tx, ty;
    // tells min, max values
    double xmin, xmax, ymin, ymax;
    // the width of one pixel
    double pixwd = 1.0, pixht = 1.0;
    final static double INFTY = 32783.0;
    
    public PlotCTM(int w, int h)
    {
        width = w;
        height = h;
        sx = 1.0;
        tx = 0.0;
        sy = -1.0;
        ty = (double) h;
        xmin = 0.0;
        xmax = (double) w;
        ymin = 0.0;
        ymax = (double) h;
    }
    
    public PlotCTM(PlotCTM ctm)
    {
        copy(ctm);
    }
    
    void setCorners(int w, int h, double llx, double lly,
        double urx, double ury)
    {
        width = w;
        height = h;
        setCorners(llx, lly, urx, ury);
    }
    
    void setCorners(double llx, double lly, double urx,
        double ury)
    {
        sx = (double) width / (urx - llx);
        sy = -(double) height / (ury - lly);
        pixwd = Math.abs(llx - urx) / (double) width;
        pixht = Math.abs(lly - ury) / (double) height;
        tx = -sx * llx;
        ty = -sy * ury;
        
        if (llx < urx)
        {
            xmin = llx;
            xmax = urx;
        }
        else
        {
            xmin = urx;
            xmax = llx;
        }
        if (lly < ury)
        {
            ymin = lly;
            ymax = ury;
        }
        else
        {
            ymin = ury;
            ymax = lly;
        }
    }
    
    RealPoint ll()
    {
        return (toRealPoint(0, height));
    }
    
    RealPoint ur()
    {
        return (toRealPoint(width, 0));
    }
    
    void resize(int w, int h)
    {
        RealPoint ll = toRealPoint(0, height);
        RealPoint ur = toRealPoint(width, 0);
        // centre it without scaling
        RealPoint LL = toRealPoint(0, h);
        RealPoint UR = toRealPoint(w, 0);
        double dx = UR.x - ur.x;
        double dy = ll.y - LL.y;
        ll.x = ll.x - 0.5 * dx;
        ll.y = ll.y - 0.5 * dy;
        ur.x = ur.x + 0.5 * dx;
        ur.y = ur.y + 0.5 * dy;
        setCorners(w, h, ll.x, ll.y, ur.x, ur.y);
    }
    
    void copy(PlotCTM ctm)
    {
        width = ctm.width;
        height = ctm.height;
        sx = ctm.sx;
        sy = ctm.sy;
        tx = ctm.tx;
        ty = ctm.ty;
        xmin = ctm.xmin;
        xmax = ctm.xmax;
        ymin = ctm.ymin;
        ymax = ctm.ymax;
        pixwd = ctm.pixwd;
        pixht = ctm.pixht;
    }
    
    // coords in can be infty but not NaN
    // all points returned have real numbers - +infty etc.
    
    // pixels to coords
    RealPoint toRealPoint(Point p)
    {
        double x0 = (double) p.x - tx;
        double y0 = (double) p.y - ty;
        RealPoint q = new RealPoint(x0 / sx, y0 / sy);
        return (q);
    }
    
    // pixels to coords
    RealPoint toRealPoint(int x, int y)
    {
        double x0 = (double) x - tx;
        double y0 = (double) y - ty;
        RealPoint q = new RealPoint(x0 / sx, y0 / sy);
        return (q);
    }
    
    // coords to pixels
    Point toPoint(RealPoint p)
    {
        double x0 = sx * p.x + tx;
        double y0 = sy * p.y + ty;
        int x1, y1;
        // never let it wrap around
        if (-INFTY < x0 && x0 < INFTY)
        {
            x1 = (int) x0;
        }
        else if (x0 <= -INFTY
            || x0 == Double.NEGATIVE_INFINITY)
        {
            x1 = (int) -INFTY;
        }
        else
        {
            x1 = (int) INFTY;
        }
        if (-INFTY < y0 && y0 < INFTY)
        {
            y1 = (int) y0;
        }
        else if (y0 <= -INFTY
            || y0 == Double.NEGATIVE_INFINITY)
        {
            y1 = (int) -INFTY;
        }
        else
        {
            y1 = (int) INFTY;
        }
        Point q = new Point(x1, y1);
        return (q);
    }
    
    // coords to pixels but in real numbers
    RealPoint toFinePoint(RealPoint p)
    {
        double x = sx * p.x + tx;
        double y = sy * p.y + ty;
        RealPoint q = new RealPoint(x, y);
        return (q);
    }
    
    // coords to pixels
    Point toPoint(double x, double y)
    {
        double x0 = sx * x + tx;
        double y0 = sy * y + ty;
        int x1, y1;
        // never let it wrap around
        if (-INFTY < x0 && x0 < INFTY)
        {
            x1 = (int) x0;
        }
        else if (x0 <= -INFTY
            || x0 == Double.NEGATIVE_INFINITY)
        {
            x1 = (int) -INFTY;
        }
        else
        {
            x1 = (int) INFTY;
        }
        if (-INFTY < y0 && y0 < INFTY)
        {
            y1 = (int) y0;
        }
        else if (y0 <= -INFTY
            || y0 == Double.NEGATIVE_INFINITY)
        {
            y1 = (int) -INFTY;
        }
        else
        {
            y1 = (int) INFTY;
        }
        Point q = new Point(x1, y1);
        return (q);
    }
    
    RealPoint toFinePoint(double x, double y)
    {
        double x0 = sx * x + tx;
        double y0 = sy * y + ty;
        RealPoint q = new RealPoint(x0, y0);
        return (q);
    }
    
    // returns false if x or y is NaN
    // since all comparisons with NaN are false
    boolean isVisible(double x, double y)
    {
        if (x >= xmin && x <= xmax && y >= ymin
            && y <= ymax)
        {
            return (true);
        }
        else
        {
            return (false);
        }
    }
    
    boolean isVisible(RealPoint p)
    {
        if (p.x >= xmin && p.x <= xmax && p.y >= ymin
            && p.y <= ymax)
        {
            return (true);
        }
        else
        {
            return (false);
        }
    }
    
    boolean isVisible(Point p)
    {
        // doesn't use xmin etc.
        if (p.x >= 0 && p.x < width && p.y >= 0
            && p.y < height)
        {
            return (true);
        }
        else
        {
            return (false);
        }
    }
    
    // following the PostScript model
    // but using column vectors
    // all of these postmultiply the CTM by the relevant matrix
    
    void scale(double a, double b)
    {
        sx *= a;
        sy *= b;
        
        pixwd /= a;
        pixht /= b;
        
        /*
         * old way no good at high res: xmin /= a; xmax /= a; ymin /= b; ymax /=
         * b;
         */

        xmin = -tx / sx;
        xmax = (width - tx) / sx;
        if (xmax < xmin)
        {
            double tmp = xmax;
            xmax = xmin;
            xmin = tmp;
        }
        ymin = -ty / sy;
        ymax = (width - ty) / sy;
        if (ymax < ymin)
        {
            double tmp = ymax;
            ymax = ymin;
            ymin = tmp;
        }
    }
    
    void translate(double a, double b)
    {
        tx += sx * a;
        ty += sy * b;
        xmin -= a;
        ymin -= b;
        xmax -= a;
        ymax -= b;
    }
    
    /*
     * public void resize(Dimension d, int w, int h) { double sx = (double)
     * d.width/(double) w; double sy = (double) d.height/(double) h; width =
     * d.width; height = d.height; scale(sx, sy); }
     */

    void zoom(double z, RealPoint t)
    {
        Point s;
        RealPoint r;
        
        // calculate new matrix
        scale(z, z);
        s = new Point(width / 2, height / 2);
        r = toRealPoint(s);
        translate(r.x - t.x, r.y - t.y);
    }
    
    void zoom(double mx, double my, RealPoint t)
    {
        Point s;
        RealPoint r;
        
        // calculate new matrix
        scale(mx, my);
        s = new Point(width / 2, height / 2);
        r = toRealPoint(s);
        translate(r.x - t.x, r.y - t.y);
    }
    
    public String toString()
    {
        return ("ctm: " + "\n ll " + ll().toString()
            + "\n ur " + ur().toString() + "\n x range ["
            + xmin + "," + xmax + "]" + "\n y range ["
            + ymin + "," + ymax + "]");
    }
}
