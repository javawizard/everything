package net.sf.opengroove.client.com;

public interface NewStatusListener
{
    /**
     * Called when the communicator specified loses it's connection to the
     * server.
     * 
     * @param c
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
     */
    public void connectionReady(Communicator c);
    
    /**
     * Called when the communicator receives a response to an
     * auto-authentication packet it sent, which indicates that authenticating
     * failed.
     * 
     * @param c
     */
    public void authenticationFailed(Communicator c,
        Packet packet);
    
    /**
     * Called when the comunicator receives a response to an auto-authentication
     * packet it sent, which indicates that authenticating was successful.
     * 
     * @param c
     */
    public void authenticationSuccessful(Communicator c);
}
