package net.sf.opengroove.common.concurrent;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ConditionalTimer extends Thread
{
    public abstract void execute();
}
