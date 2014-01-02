package org.opengroove.g4.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class that can spool packets asynchronously onto an ObjectOutputStream. The
 * code primarily comes from the 6jet project (http://6jet.googlecode.com).
 * 
 * @author Alexander Boyd
 * 
 */
public class PacketSpooler extends Thread
{
    private ObjectOutputStream out;
    private BlockingQueue<Packet> queue;
    
    public PacketSpooler(ObjectOutputStream out, int queueSize)
    {
        this.out = out;
        this.queue = new LinkedBlockingQueue<Packet>(queueSize);
    }
    
    public synchronized boolean send(Packet packet)
    {
        boolean canOffer = queue.offer(packet);
        return canOffer;
    }
    
    private boolean closed = false;
    private final Object sendLock = new Object();
    
    public void close() throws IOException
    {
        closed = true;
        out.close();
    }
    
    public void run()
    {
        try
        {
            while (true)
            {
                Packet packet = queue.take();
                synchronized (sendLock)
                {
                    out.writeObject(packet);
                    out.flush();
                }
            }
        }
        catch (Exception e)
        {
            if (closed)
            {
                System.out.println("Closed packet spooler with normal close exception");
            }
            else
            {
                e.printStackTrace();
                System.out
                    .println("Closed packet spooler with the above abnormal exception");
            }
        }
        finally
        {
            closed = true;
        }
    }
    
    /**
     * Returns true if this packet spooler has been closed, either by a call to
     * close() or by an exception being thrown when writing a packet.
     * 
     * @return
     */
    public boolean isClosed()
    {
        return closed;
    }
    
    /**
     * Returns true if this packet spooler has unsent packets, false if the
     * command spooler has flushed all packets and is waiting for more to send.
     * 
     * @return
     */
    public boolean hasPackets()
    {
        return queue.size() > 0;
    }
    
}
