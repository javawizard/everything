package net.sf.opengroove.realmserver;

public class InterServerPacket
{
    /*
     * TODO: add stuff to this class. There should be stuff for the packet's id,
     * something that would handle the packet's response if this command is the
     * initiating one and needs a reponse, a handler that should be called if
     * the packet fails to make it to the intended server (perhaps, then,
     * inter-server communication should send a response to a packet solely to
     * acknowledge receipt of the packet, and send another packet (with the
     * origiating server sending it's own ack response) when the actual response
     * is ready. The main reason for that is that with a client → realm server
     * connection, if the connection is closed then a response is guaranteed not
     * to be forthcoming in a future connection. This is not true, however, with
     * inter-server communications, which may drop the connection during packet
     * processing and then re-establish a connection when packet processing is
     * done), the packet's actual contents, and possibly another server (or list
     * of servers) that the packet should be dispatched to, in the case of
     * multiple servers serving a single realm.
     */
}
