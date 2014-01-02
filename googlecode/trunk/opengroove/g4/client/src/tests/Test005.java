package tests;

import java.io.OutputStream;

import org.opengroove.g4.common.data.FileBlock;
import org.opengroove.g4.common.data.FileBlockBuilder;

/**
 * A class that tests out object finalization and the garbage collector. It
 * creates a FileBlock with some data in it, garbage collects, sets the
 * FileBlock's variable to null, then garbage collects again. Then it sleeps for
 * a few seconds, and then exits. There is a System.out.println statement in
 * FileBlock that prints out when a file block has been garbage collected, so
 * this class tests to see if calling gc() really does get rid of the file
 * object right away.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test005
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        FileBlockBuilder builder = new FileBlockBuilder();
        OutputStream stream = builder.getStream();
        stream.write(32);
        stream.flush();
        stream.close();
        FileBlock block = (FileBlock) builder.finish();
        System.out.println("Collecting...");
        System.gc();
        System.out.println("Removing block and builder...");
        builder = null;
        block = null;
        System.out.println("Collecting again...");
        System.gc();
        System.out.println("Waiting...");
        Thread.sleep(3000);
        System.exit(0);
    }
    
}
