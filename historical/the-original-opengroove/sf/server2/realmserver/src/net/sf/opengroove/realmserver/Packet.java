package net.sf.opengroove.realmserver;

import java.io.InputStream;

public class Packet
{
    private String description;
    private InputStream stream;
    
    public Packet(InputStream stream)
    {
        this.stream = stream;
    }
    
    public InputStream getStream()
    {
        return this.stream;
    }
    
    String getDescription()
    {
        return description;
    }
    
    void setDescription(String description)
    {
        this.description = description;
    }
}
