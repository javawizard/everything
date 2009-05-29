package com.googlecode.opengroove.g4.client.dynamics;

import java.io.File;

public interface Engine
{
    /**
     * Initializes this engine with the storage folder specified. This will be a
     * storage folder that this engine previously modified, or a blank folder.
     * This must, however, remain a folder; it cannot be deleted and then
     * recreated as a file.
     * 
     * @param storage
     */
    public void init(File storage);
    
    /**
     * Creates a new EngineWriter for writing to the engine. This must create a
     * new writer each time it is called.
     * 
     * @return
     */
    public EngineWriter createWriter();
    
    /**
     * Creates a new EngineReader for reading from this engine.
     * 
     * @return
     */
    public EngineReader createReader();
    
    public DataBlock
}
