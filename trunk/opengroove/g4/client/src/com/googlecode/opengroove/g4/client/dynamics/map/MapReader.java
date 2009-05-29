package com.googlecode.opengroove.g4.client.dynamics.map;

import com.googlecode.opengroove.g4.client.dynamics.EngineReader;

public class MapReader implements EngineReader
{
    private MapEngine engine;
    
    MapReader(MapEngine engine)
    {
        this.engine = engine;
    }
    
    public String getProperty(String name)
    {
        synchronized (engine)
        {
            return engine.props.getProperty(name);
        }
    }
    
    public String[] listProperties()
    {
        synchronized (engine)
        {
            return engine.props.keySet().toArray(new String[0]);
        }
    }
}
