package org.opengroove.g4.common;

import java.util.concurrent.atomic.AtomicLong;

import org.opengroove.g4.common.protocol.NopPacket;

/**
 * A class that will repeatedly add nop packets to a packet spooler, as long as
 * it does not have any packets on it. If the packet spooler closes, this thread
 * will die.
 * 
 * @author Alexander Boyd
 * 
 */
public class NopThread extends Thread
{
    private PacketSpooler spooler;
    
    private int delay;
    
    private static AtomicLong threadSequence = new AtomicLong();
    
    public NopThread(PacketSpooler spooler, int delay)
    {
        super("NopThread-" + threadSequence.getAndIncrement());
        this.spooler = spooler;
        this.delay = delay;
    }
    
    public void run()
    {
        while (!spooler.isClosed())
        {
            try
            {
                Thread.sleep(delay);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
            if (!spooler.hasPackets())
                spooler.send(new NopPacket());
        }
    }
}
