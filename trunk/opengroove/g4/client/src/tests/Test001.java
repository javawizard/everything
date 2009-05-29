package tests;

import java.io.File;

import org.opengroove.g4.client.dynamics.map.MapEngine;

public class Test001
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        File storage = new File("test-storage/test001/db");
        storage.mkdirs();
        MapEngine engine = new MapEngine();
        engine.init(storage);
    }
    
}
