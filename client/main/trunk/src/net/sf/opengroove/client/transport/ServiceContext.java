package net.sf.opengroove.client.transport;

public interface ServiceContext
{
    /**
     * Used by Services to tell OpenGroove that it should check to see if the
     * service needs re-configuring. If the service is currently in the process
     * of being configured, then this does nothing. TODO: maybe a configure
     * method should be added that shows config, and a setup method on service
     * added that is called the first time the service is added. That way, the
     * service can choose when the configuration dialog is shown (for example,
     * when the user sets up the service, or when it connects and finds out
     * additional information).
     */
    public void rescanConfigurationNeeded();
    
    public void checkConnectivity();
}
