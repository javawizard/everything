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
        COLOR,POINT,FLOAT
    }
    private String name;
    private Type type;
    private int min;
    private int max;
    private Object value;
}
