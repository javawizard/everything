package net.sf.opengroove.common.concurrent;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class ConditionalTimer extends Thread
{
    /**
     * The interval at which this task is to run, in milliseconds. When the task
     * completes, this amount of time will be waited, and the task will again be
     * executed if the condition is true.
     */
    private int period;
    /**
     * The time at which this timer will wake up to check the conditional. This
     * is purely for information purposes, and serves no useful purpose in
     * running the actual timer. Whenever the timer is about to sleep (both for
     * <code>period</code> and <code>checkPeriod</code>), it adds the
     * amount of time that it will sleep to System.currentTimeMillis() and puts
     * it in this variable.
     */
    private volatile long nextUnlockTime = 0;
    private volatile boolean waitingForCondition = false;
    private volatile boolean waitingForPeriod = false;
    /**
     * The condition on which to execute this task.
     */
    private Conditional condition;
    /**
     * If the condition is false, this amount of time will be waited, and the
     * conditional will be checked again.
     */
    private int checkPeriod = 15 * 1000;
    /**
     * True if this timer should still run, false if not. The shutdown() method
     * sets this to false, and the timer will terminate after it finishes
     * waiting the next wait period.
     */
    private volatile boolean isRunning = true;
    
    public ConditionalTimer(int period,
        Conditional condition)
    {
        super();
        this.period = period;
        this.condition = condition;
    }
    
    public void shutdown()
    {
        isRunning = false;
    }
    
    public abstract void execute();
    
    public void run()
    {
        try
        {
            Thread.sleep(1000);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        while (isRunning)
        {
            boolean waitCheck = false;
            try
            {
                if (condition.query())
                    execute();
                else
                    waitCheck = true;
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
            nextUnlockTime = System.currentTimeMillis()
                + (waitCheck ? checkPeriod : period);
            if (waitCheck)
                waitingForCondition = true;
            else
                waitingForPeriod = true;
            try
            {
                Thread.sleep(waitCheck ? checkPeriod
                    : period);
            }
            catch (InterruptedException e)
            {
                System.out.println("timer interrupted");
            }
            waitingForCondition = false;
            waitingForPeriod = false;
        }
    }
    
}
