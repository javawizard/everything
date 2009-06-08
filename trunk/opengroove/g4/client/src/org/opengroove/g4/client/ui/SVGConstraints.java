package org.opengroove.g4.client.ui;

public class SVGConstraints
{
    private boolean full;
    /**
     * 0 is left, 1 is right, 0.5 is center, etc.
     */
    private double horizontalAlignment;
    /**
     * 0 is top, 1 is bottom, 0.5 is center, etc.
     */
    private double verticalAlignment;
    
    public SVGConstraints(boolean full,
        double horizontalAlignment, double verticalAlignment)
    {
        super();
        this.full = full;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
    }
    
    boolean isFull()
    {
        return full;
    }
    
    double getHorizontalAlignment()
    {
        return horizontalAlignment;
    }
    
    double getVerticalAlignment()
    {
        return verticalAlignment;
    }
}
