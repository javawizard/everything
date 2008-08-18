package net.sf.opengroove.projects.filleditor;

public class FillParameter
{
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
