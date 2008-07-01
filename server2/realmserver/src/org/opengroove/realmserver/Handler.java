package org.opengroove.realmserver;

import java.util.Properties;

import nanohttpd.NanoHTTPD.Response;

public interface Handler
{
    public Response serve(HandlerServer server, String uri,
        String method, Properties header, Properties parms);
    
    public boolean serveStartsWith();
    
    public String getUri();
}
