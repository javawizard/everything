package net.sf.opengroove.client.dynamics;

public interface EngineWriter
{
    /**
     * Indicates to the engine writer that the accessor is done writing to it.
     * Any methods that write to it should throw an IllegalStateException after
     * this is called. In addition, the date that this is called should be
     * marked as the date for the resulting delta that can be taken from this
     * writer.
     */
    public void finish();
    
    /**
     * Returns this writer's delta. This throws an IllegalStateException if
     * finish() has not been called yet. If it has, then this method returns the
     * delta...
     * 
     * TODO: should this method exist? Or should calling finish() flush the
     * commands over to the context directly? 
     * 
     * @return
     */
    public Delta getDelta();
}
