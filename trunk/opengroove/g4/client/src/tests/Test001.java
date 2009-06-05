package tests;

import java.io.File;

import net.sf.opengroove.common.utils.DataUtils;
import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.g4.client.dynamics.Command;
import org.opengroove.g4.client.dynamics.map.MapEngine;
import org.opengroove.g4.client.dynamics.map.MapWriter;
/**
 * A class to test out the functionality of a MapEngine.
 * @author Alexander Boyd
 *
 */
public class Test001
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        File storage = new File("test-storage/test001/db");
        DataUtils.recursiveDelete(storage);
        storage.mkdirs();
        MapEngine engine = new MapEngine();
        engine.init(storage);
    }
    
}
