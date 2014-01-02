package net.sf.opengroove.client.g3com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * This class takes care of finding a server to connect to, based on the name of
 * the realm.
 * 
 * @author Alexander Boyd
 * 
 */
public class ConnectionResolver
{
    public static final int DEFAULT_PORT = 63745;
    
    static
    {
        Lookup.getDefaultCache(DClass.IN).setMaxNCache(600);
        Lookup.getDefaultCache(DClass.IN).setMaxCache(3600);
        Lookup.getDefaultResolver().setTimeout(2);
    }
    
    private static Map<String, Resolution> cache = new Hashtable<String, Resolution>();
    
    private static SRVRecord[] lookupSrv(String realm)
    {
        try
        {
            Lookup lookup = new Lookup("_opengroove._tcp."
                + realm, Type.SRV);
            Record[] records = lookup.run();
            if (records == null)
            {
                return new SRVRecord[0];
            }
            SRVRecord[] results = new SRVRecord[records.length];
            System.arraycopy(records, 0, results, 0,
                records.length);
            return results;
        }
        catch (Exception e)
        {
            System.out
                .println("Exception while resolving srv records, this probably "
                    + "means that the realm specified isn't a domain name "
                    + "(for example, it's localhost, or an ip address)");
            e.printStackTrace(System.out);
            return new SRVRecord[0];
        }
    }
    
    /**
     * This is the time that results will be cached for, in milliseconds.
     */
    public static int cacheTime = 1000 * 60 * 60 * 4;// 4 hours
    
    public static synchronized void clearCache()
    {
        cache.clear();
    }
    
    /**
     * Returns a list of server contexts, one for each of this realm's servers.
     * The items returned from this list are cached for the time specified by
     * the field cacheTime, which is, by default, four hours. The returned list
     * is ordered by server preference, IE, the server at index 0 of the
     * returned array should be tried first, then the server at index 1, etc.
     * 
     * FIXME: the caching caches failed responses as well. It probably shouldn't
     * do that, because that means that if the user isn't online when they start
     * opengroove, then opengroove won't connect until the cache expires.
     * Perhaps failed responses should be marked as such, and only cached for,
     * say, a minute.
     * 
     * @param realm
     *            The realm to look up
     * @return The servers for the realm specified.
     */
    public static synchronized ServerContext[] lookup(
        String realm)
    {
        Resolution rs = cache.get(realm);
        if (rs == null
            || (rs.getTime() + cacheTime) < System
                .currentTimeMillis())
        {
            rs = new Resolution();
            rs.setRealm(realm);
            rs.setResult(connectLookup(realm));
            rs.setTime(System.currentTimeMillis());
            cache.put(realm, rs);
        }
        ServerContext[] results = new ServerContext[rs
            .getResult().length];
        for (int i = 0; i < results.length; i++)
        {
            results[i] = rs.getResult()[i].copy();
        }
        return results;
    }
    
    /**
     * Actually does the lookup. This methods icalled by lookup(String) when it
     * has determined that there is no suitable cached result.
     * 
     * @param realm
     * @return
     */
    private static ServerContext[] connectLookup(
        String realm)
    {
        ArrayList<ServerContext> results = new ArrayList<ServerContext>();
        SRVRecord[] srvRecords = lookupSrv(realm);
        // FIXME: This list needs to be ordered by priority, and, within
        // priority, ordered according to a weighted random on the weights of
        // the records
        for (SRVRecord record : srvRecords)
        {
            String target = record.getTarget().toString();
            if (record.getTarget().isAbsolute())
                target = target.substring(0, target
                    .length() - 1);
            else
                target += "." + realm;
            results.add(new ServerContext(realm, target,
                record.getPort(), record.getPriority(),
                record.getWeight(),
                ServerContext.Source.SRV));
        }
        if (results.size() == 0)
        {
            results.add(new ServerContext(realm, realm,
                DEFAULT_PORT, Integer.MAX_VALUE, 1,
                ServerContext.Source.TARGET));
        }
        Collections.sort(results,
            new Comparator<ServerContext>()
            {
                
                @Override
                public int compare(ServerContext o1,
                    ServerContext o2)
                {
                    if (o1.getPriority() < o2.getPriority())
                        return -1;
                    if (o1.getPriority() > o2.getPriority())
                        return 1;
                    return 0;
                }
            });
        System.out.println("Resolved realm " + realm
            + " to these servers:");
        for (ServerContext context : results)
        {
            System.out.println(context.getHostname() + ":"
                + context.getPort());
        }
        return results.toArray(new ServerContext[0]);
    }
}
