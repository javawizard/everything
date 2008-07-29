package net.sf.opengroove.client.com;

import java.util.ArrayList;
import java.util.Arrays;
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
            Record[] records = new Lookup(
                "_opengroove._tcp." + realm, Type.SRV)
                .run();
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
    public static int cacheTime = 1000 * 60 * 60;
    
    public static synchronized void clearCache()
    {
        cache.clear();
    }
    
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
        return results.toArray(new ServerContext[0]);
    }
}
