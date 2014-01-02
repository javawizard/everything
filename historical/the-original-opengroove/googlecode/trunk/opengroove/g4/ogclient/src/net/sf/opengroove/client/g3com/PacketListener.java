package net.sf.opengroove.client.g3com;

/**
 * A listener that receives and processes packets from the server.
 * 
 * @author Alexander Boyd
 * 
 */
public interface PacketListener
{
    /**
     * Receives a packet from the server. If the packet specified is not a
     * packet that this listener wishes to handle, then this method should do
     * nothing.
     * 
     * @param packet The packet to receive
     */
    public void receive(Packet packet);
}
