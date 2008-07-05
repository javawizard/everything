package net.sf.opengroove.realmserver;

import java.io.File;
import java.util.Properties;

import nanohttpd.NanoHTTPD.Response;

public class ResourceHandler implements Handler
{
    private String uri;
    private File resFolder;
    private boolean serveFolders;
    
    public ResourceHandler(String uri, File resFolder,
        boolean serveFolders)
    {
        super();
        this.uri = uri;
        this.resFolder = resFolder;
        this.serveFolders = serveFolders;
    }

    @Override
    public String getUri()
    {
        // TODO Auto-generated method stub
        return uri;
    }
    
    @Override
    public Response serve(HandlerServer server, String uri,
        String method, Properties header, Properties parms)
    {
        return server.serveFile(uri, header, resFolder, serveFolders);
    }
    
    @Override
    public boolean serveStartsWith()
    {
        // TODO Auto-generated method stub
        return true;
    }
    
}
