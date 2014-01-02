package org.opengroove.g4.client.com;

/**
 * A communicator that can connect to a G4 server. It supports automatic
 * re-connecting of dropped connections, automatic authentication on connection,
 * listening for incoming commands, and synchronous reply detection.<br/>
 * <br/>
 * 
 * This class combines G3's LowLevelCommunicator and Communicator into one
 * class. G3 had to have two separate classes because of the way the protocol
 * was written. LowLevelCommunicator handled sending strings of bytes to the
 * server as commands, and receiving those and notifying listeners, and
 * Communicator handled converting various commands to objects that represented
 * those commands. This class is more like LowLevelCommunicator, and the object
 * serialization used by the G4 protocol takes the place of G3's Communicator
 * class.
 * 
 * @author Alexander Boyd
 * 
 */
public class Communicator
{
    
}
