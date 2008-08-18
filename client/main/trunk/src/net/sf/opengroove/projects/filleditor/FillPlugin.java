package net.sf.opengroove.projects.filleditor;

/**
 * Each type of fill will have a class that implements this interface. One of
 * these is created for each region that uses it.
 * 
 * @author Alexander Boyd
 * 
 */
public interface FillPlugin
{
    public FillParameter[] getParameters();
    
    public void draw(Graphics2D g);
}
