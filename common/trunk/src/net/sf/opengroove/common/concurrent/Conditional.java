package net.sf.opengroove.common.concurrent;

/**
 * An interface for checking if something is true. This is used along with
 * ScheduledConditionalTask to repeatedly execute a task, but wait until a
 * specified condition is true.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Conditional
{
    public boolean query();
}
