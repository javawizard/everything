package net.sf.opengroove.client.com;

/**
 * A listener that can be used to listen for when a data meter is changed. This
 * could be registered in order to update a GUI component that displays the
 * amount of data transferred.
 * 
 * @author Alexander Boyd
 * 
 */
public interface DataMeterListener
{
    /**
     * Indicates that the meter specified has changed it's value.
     * 
     * @param source
     *            The meter that changed
     */
    public void meterChanged(DataMeter source);
}
