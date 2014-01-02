package org.opengroove.g4.client.dynamics.map;

import org.opengroove.g4.client.dynamics.EngineReader;

public class MapReader implements EngineReader
{
    private MapEngine engine;
    
    MapReader(MapEngine engine)
    {
        this.engine = engine;
    }
    
    public String getProperty(String name)
    {
        return engine.props.getProperty(name);
    }
    
    public String[] listProperties()
    {
        return engine.props.keySet().toArray(new String[0]);
    }
    
    public void lock()
    {
        engine.lock.readLock().lock();
    }
    
    public void unlock()
    {
        engine.lock.readLock().unlock();
    }
}
