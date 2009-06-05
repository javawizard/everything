package org.opengroove.g4.client.dynamics.map;

import java.net.URLEncoder;

import org.opengroove.g4.client.dynamics.AbstractListWriter;
import org.opengroove.g4.client.dynamics.Command;
import org.opengroove.g4.client.dynamics.EngineWriter;
import org.opengroove.g4.common.data.ByteBlock;

public class MapWriter extends AbstractListWriter
{
    public void setProperty(String name, String value)
    {
        if (name == null)
            throw new IllegalArgumentException("name must not be null.");
        if (value == null)
            throw new IllegalArgumentException(
                "value must not be null. Use removeProperty to remove a property instead.");
        addCommand(new Command("SET", new ByteBlock(URLEncoder.encode(name) + "="
            + URLEncoder.encode(value))));
    }
    
    public void removeProperty(String name)
    {
        if (name == null)
            throw new IllegalArgumentException("name must not be null");
        addCommand(new Command("REMOVE", new ByteBlock(URLEncoder.encode(name))));
    }
}
