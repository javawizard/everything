package net.sf.opengroove.common.utils;

import net.sf.opengroove.client.com.ListenerManager;
import net.sf.opengroove.client.com.Notifier;

public class Progress
{
    private double value = 0;
    private ListenerManager<ProgressListener> listeners = new ListenerManager<ProgressListener>();
    
    public void set(double value)
    {
        if (value < 0)
            value = 0;
        if (value > 1)
            value = 1;
        this.value = value;
        listeners.notify(new Notifier<ProgressListener>()
        {
            
            @Override
            public void notify(ProgressListener listener)
            {
                listener.progressUpdated(Progress.this);
            }
        });
    }
}
