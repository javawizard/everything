package tests;

import java.io.File;

import org.opengroove.g4.client.dynamics.ByteBlock;
import org.opengroove.g4.client.dynamics.Command;
import org.opengroove.g4.client.dynamics.DataBlock;
import org.opengroove.g4.client.dynamics.map.MapEngine;
import org.opengroove.g4.client.dynamics.map.MapWriter;

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
        // test-specific stuff
        MapWriter writer = engine.createWriter();
        writer.removeProperty("testprop");
        writer.commit();
        System.out.println(engine.applyCommands(new DataBlock[] {}, writer
            .getCommands())[0].getString());
    }
    
}
