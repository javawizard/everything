package net.sf.opengroove.client.com;

import org.opengroove.g4.common.protocol.LoginResponse;

public interface StatusListener
{
    /**
     * Called when the communicator successfully establishes a connection to the
     * server.
     */
    public void connectionReady();
    
    /**
     * Called when the connection to the server is lost.
     */
    public void lostConnection();
    
    /**
     * Called when the communicator tried to authenticate with the server but
     * the server reported that incorrect login information was received. The
     * communicator immediately drops the connection when this happens, but it
     * will continue to attempt to reconnect.
     * 
     * @param response
     *            The login response that was received from the server, which
     *            indicates why the login failed
     */
    public void authenticationFailed(LoginResponse response);
}
