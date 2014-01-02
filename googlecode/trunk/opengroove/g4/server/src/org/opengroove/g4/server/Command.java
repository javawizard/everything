package org.opengroove.g4.server;

import org.opengroove.g4.common.Packet;

public interface Command<E extends Packet>
{
    public void process(E packet);
}
