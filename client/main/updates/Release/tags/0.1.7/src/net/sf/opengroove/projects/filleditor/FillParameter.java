package net.sf.opengroove.projects.filleditor;

import java.io.Serializable;

public class FillParameter implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 2033861388607534050L;
    
    public enum Type
    {
        COLOR, POINT, FLOAT, BOOLEAN
    }
    
    /**
     * The name of this parameter.
     */
    public String name;
    /**
     * The description of this parameter.
     */
    public String description;
    /**
     * The type of this parameter.
     */
    public Type type;
    /**
     * For types that have a defined range, this is the minimum value of the
     * field.
     */
    public int min;
    /**
     * For types that have a defined range, this is the maximum value of the
     * field.
     */
    public int max;
    /**
     * The actual value of the field.
     */
    public Object value;
}
