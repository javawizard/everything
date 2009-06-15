package net.sf.opengroove.client.transport;

import java.awt.Window;

/**
 * A service for routing pmessages and imessages.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Service
{
    public void init(ServiceContext context);
    
    /**
     * Returns true if this service makes configuration information available.
     * This will be called immediately after {@link #init(ServiceContext)} is
     * called, and before any other methods are called on this service. It will
     * also be called if the service requests a configuration rescan by calling
     * {@link ServiceContext#rescanConfigurationNeeded()}.
     * 
     * @return True if this service can be configured, false if it cannot. If it
     *         can be configured, then it must be returned in the list obtained
     *         from {@link Connector#listKnownServices()}. Otherwise, the user
     *         won't have any means of changing configuration information.
     */
    public boolean canConfigure();
    
    /**
     * Returns true if this service must be configured before being used for the
     * first time.
     * 
     * @return
     */
    public boolean needsFirstTimeConfiguration();
    
    /**
     * Configures this service. Typically, this method will pop open a dialog
     * over the window specified, which displays general configuration options.
     * The service is responsible for using some means to store configuration
     * options. Services that are part of a plugin would typically want to use
     * an OpenGroove extension point that makes available storage.<br/>
     * <br/>
     * 
     * This is called immediately
     * 
     * @param parent
     *            The window that any dialogs should be shown over
     */
    public void configure(Window parent);
}
