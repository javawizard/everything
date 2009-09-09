package jw.bznetwork.client.data;

import java.io.Serializable;

public class NamedTarget implements Serializable
{
    private int id;
    private String label;
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
}
