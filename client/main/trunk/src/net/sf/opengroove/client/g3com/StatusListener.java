package net.sf.opengroove.client.g3com;

/**
 * An interface that can be used to listen for various status changes in a
 * Communicator or a CommandCommunicator.
 * 
 * @author Alexander Boyd
 * 
 */
public interface StatusListener
{
    /**
     * Called when the communicator specified loses it's connection to the
     * server.
     * 
     * @param c
     *            The communicator on which this event occured.
     */
    public void connectionLost(Communicator c);
    
    /**
     * Called when the communicator specified establishes a connection to the
     * server, but before the handshake has been performed. The communicator
     * will still appear disconnected at this time.<br/><br/>
     * 
     * It is possible for this method to be called multiple consecutive times.
     * This would occur if an error occures while performing the handshake with
     * the server specified, in which case the communicator will move on to the
     * next server, connect to it, and call this method again.
     * 
     * @param c
     *            The communicator on which this event occured.
     */
    public void connectionEstablished(Communicator c,
        ServerContext server);
    
    /**
     * Called when the communicator specified connects to the server and
     * successfully performs a handshake. The authenticate command, if auto
     * authentication is enabled, will have already been sent, so other commands
     * can be sent to the communicator at this time without worrying that they
     * will arrive before the authentication packet.
     * 
     * @param c
     *            The communicator on which this event occured.
     */
    public void connectionReady(Communicator c);
    
    /**
     * Called when the communicator receives a response to an
     * auto-authentication packet it sent, which indicates that authenticating
     * failed.
     * 
     * @param c
     *            The communicator on which this event occured.
     */
    public void authenticationFailed(Communicator c,
        Packet packet);
    
    /**
     * Called when the comunicator receives a response to an auto-authentication
     * packet it sent, which indicates that authenticating was successful.
     * 
     * @param c
     *            The communicator on which this event occured.
     */
    public void authenticationSuccessful(Communicator c);
    
    /**
     * Called when the communicator successfully shuts down. Calling this method
     * on all of the registered StatusListeners is the last step before the
     * communicator's coordinator thread shuts down. Note that the communicator
     * will still report that it is alive when this method is called. isAlive()
     * will not return false until after all of the communicatorShutdown methods
     * have been called for the commnuicator in question.
     * 
     * @param c
     *            The communicator on which this event occured.
     */
    public void communicatorShutdown(Communicator c);
}
