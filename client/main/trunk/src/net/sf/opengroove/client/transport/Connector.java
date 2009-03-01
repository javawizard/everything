package net.sf.opengroove.client.transport;

import java.net.URI;

public interface Connector
{
    public void init();
    
    public Service getService(URI uri);
    
    public String getId();
}
