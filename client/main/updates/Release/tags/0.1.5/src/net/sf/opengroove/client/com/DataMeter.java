package net.sf.opengroove.client.com;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.opengroove.client.OpenGroove;

/**
 * A class for watching how much data is being transferred. Right now, it's just
 * a thin wrapper around AtomicLong, but additional functionality is planned.
 * 
 * @author Alexander Boyd
 * 
 */
public class DataMeter
{
    private AtomicLong amount = new AtomicLong();
    
    private ListenerManager<DataMeterListener> listeners = new ListenerManager<DataMeterListener>();
    
    public void add(long amount)
    {
        this.amount.addAndGet(amount);
        listeners.notify(new Notifier<DataMeterListener>()
        {
            
            @Override
            public void notify(DataMeterListener listener)
            {
                listener.meterChanged(DataMeter.this);
            }
        });
    }
    
    public long getAmount()
    {
        return this.amount.get();
    }
    
    public String format()
    {
        return OpenGroove.formatDataSize(getAmount());
    }
    
    public void addListener(DataMeterListener listener)
    {
        listeners.addListener(listener);
    }
    
    public void removeListener(DataMeterListener listener)
    {
        listeners.removeListener(listener);
    }
}
