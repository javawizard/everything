package net.sf.opengroove.projects.filleditor;

import java.awt.Graphics2D;
import java.io.Serializable;

/**
 * Each type of fill will have a class that implements this interface. One of
 * these is created for each region that uses it.
 * 
 * @author Alexander Boyd
 * 
 */
public interface FillPlugin extends Serializable
{
    /**
     * Gets a list of fill parameters that this particular fill needs. All of
     * the fields should be initialized to the requirements needed for that
     * parameter, and the value should be initially set to the default for that
     * parameter. Subsequent invocations of this method should return the exact
     * same array, and should not reset the values of parameters to their
     * defaults.<br/><br/>
     * 
     * The plugin should store the fill parameters in such a way that they get
     * stored, along with their values, when the plugin is serialized. Usually,
     * storing them as an array in a non-transient field will work.
     * 
     * @return
     */
    public FillParameter[] getParameters();
    
    /**
     * Draws this particular fill onto the graphics specified, given the region
     * specified. Fill parameter values should be looked up from an
     * internally-stored instance of the array that would be returned from
     * getParameters(). Drawing operations on the graphics can be performed that
     * modify areas outside of the region specified; the resulting draw
     * operations will be trimmed to fit the region area. In fact, plugins are
     * encouraged to draw outside of their region, since the user may choose to
     * apply a transform such as a bezier curve to the region.<br/><br/>
     * 
     * Implementations of this method should not set the clip on the graphics
     * object passed in, unless they know for sure that they are not drawing out
     * of the region's bounds. The graphics passed in comes with a clip that
     * restricts drawing to the region's bounds.
     * 
     * @param region
     *            The region that this fill is to be applied to
     * @param g
     *            The graphics to draw this fill on
     */
    public void draw(Region region, Graphics2D g,
        int width, int height);
}
