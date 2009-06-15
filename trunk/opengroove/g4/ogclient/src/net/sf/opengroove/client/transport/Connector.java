package net.sf.opengroove.client.transport;

import java.net.URI;

import net.sf.opengroove.client.UserContext;

/**
 * A connector.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Connector
{
    /**
     * Initializes this connector. 
     * @param context
     */
    public void init(UserContext context);
    
    public Service getService(URI uri) throws NoSuchServiceException;
    
    public String getId();
    
    /**
     * Lists the services that this connector knows about, or null if this
     * connector does not support listing of services. These would typically be
     * services that had previously been retrieved from {@link #getService(URI)}
     * , or services added via some external UI.<br/>
     * <br/>
     * 
     * For example, the p2p service would probably return one URI from this
     * method, since it only has one service associated with it. The relay
     * connector would probably return one URI for each relay server that has
     * been used with it, although it might choose not to return a URI for relay
     * servers that do not require authentication.
     * 
     * @return
     */
    public URI[] listKnownServices();
    
    /**
     * Tells this connector that it should remove information about the service
     * denoted by the URI specified. This will only have an effect for
     * connectors that store information about the services obtained by them. In
     * otherwords, this generally will only have an effect for services returned
     * from {@link #listKnownServices()}, and should have the effect of removing
     * the service from that list and discarding any configuration information
     * related to it.
     * 
     * @param uri
     */
    public void removeService(URI uri);
}
