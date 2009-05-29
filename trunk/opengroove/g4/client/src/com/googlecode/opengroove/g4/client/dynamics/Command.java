package com.googlecode.opengroove.g4.client.dynamics;

/**
 * A command to an engine. This stores the command's name and the command's
 * data.
 * 
 * @author Alexander Boyd
 * 
 */
public class Command
{
    private String name;
    private DataBlock data;
    
    public Command(String name, DataBlock data)
    {
        super();
        this.name = name;
        this.data = data;
    }
    
    public String getName()
    {
        return name;
    }
    
    public DataBlock getData()
    {
        return data;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Command()
    {
        super();
    }
    
    public void setData(DataBlock data)
    {
        this.data = data;
    }
}
