package net.sf.opengroove.common.utils;

import net.sf.opengroove.client.com.ListenerManager;

public class Progress
{
    private double value = 0;
    private ListenerManager<ProgressListener> listeners = new ListenerManager<ProgressListener>();
}
