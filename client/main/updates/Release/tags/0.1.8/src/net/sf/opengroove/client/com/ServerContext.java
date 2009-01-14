package net.sf.opengroove.client.com;

/**
 * This class holds information about a server to connect to.
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerContext implements Cloneable
{
    private String realm;
    private int port;
    private String hostname;
    private int priority;
    private int weight;
    private Source source;
    
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public ServerContext copy()
    {
        return (ServerContext) clone();
    }
    
    /**
     * Enum for where a particular server was obtained from.
     * 
     * @author Alexander Boyd
     * 
     */
    public static enum Source
    {
        /**
         * This indicates that this server was found as a particular result from
         * an SRV lookup against the realm's hostname.
         */
        SRV,
        /**
         * This indicates that this server is the realm's hostname. This should
         * usually only be tried as a last resort, and should usually not be
         * used at all if there were any SRV records for the realm specified.
         */
        TARGET
    }
    
    public String getRealm()
    {
        return realm;
    }
    
    public String getHostname()
    {
        return hostname;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public int getPriority()
    {
        return priority;
    }
    
    public int getWeight()
    {
        return weight;
    }
    
    public Source getSource()
    {
        return source;
    }
    
    public void setRealm(String realm)
    {
        this.realm = realm;
    }
    
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }
    
    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    public void setWeight(int weight)
    {
        this.weight = weight;
    }
    
    public void setSource(Source source)
    {
        this.source = source;
    }
    
    public ServerContext(String realm, String hostname,
        int port, int priority, int weight, Source source)
    {
        super();
        this.realm = realm;
        this.hostname = hostname;
        this.port = port;
        this.priority = priority;
        this.weight = weight;
        this.source = source;
    }
    
    public ServerContext()
    {
        super();
    }
}
