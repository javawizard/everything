package net.sf.opengroove.client;

public enum Symbol
{
    UP(""), LEFT, RIGHT, DOWN;
    private String value;
    
    private Symbol(String value)
    {
        this.value = value;
    }
    
    public String toString()
    {
        return value;
    }
}
