package org.opengroove.realmserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import nanohttpd.NanoHTTPD;
import nanohttpd.NanoHTTPD.Response;

public class HandlerServer extends NanoHTTPD
{
    @Override
    public Response serve(String uri, String method,
        Properties header, Properties parms)
    {
        Handler handler = handlers.get(uri);
        if (handler != null)
            return handler.serve(this, uri, method, header,
                parms);
        for (Map.Entry<String, Handler> entry : handlers
            .entrySet())
        {
            if (uri.startsWith(entry.getKey())
                && entry.getValue().serveStartsWith())
            {
                return entry.getValue().serve(this, uri,
                    method, header, parms);
            }
        }
        return new Response(
            HTTP_NOTFOUND,
            "text/html",
            "<html><body><b>The page you were looking for cannot be found. "
                + "Try visiting the <a href=\"/\">home page</a>, or visit us at "
                + "<a href=\"http://www.opengroove.org\">www.opengroove.org</a> "
                + "to contact us if you think there's a problem.</b><br/><br/>"
                + "<small>OpenGroove HandlerServer/NanoHTTPD</small><body></html>");
    }
    
    private HashMap<String, Handler> handlers = new HashMap<String, Handler>();
    
    /**
     * adds a handler. Handlers are organized by their uri, so if you add a
     * handler and there is already a handler with this one's uri, the old one
     * is replaced by this one.
     * 
     * @param handler
     */
    public void addHandler(Handler handler)
    {
        handlers.put(handler.getUri(), handler);
    }
    
    public HandlerServer(int port) throws IOException
    {
        super(port);
    }
    
}
