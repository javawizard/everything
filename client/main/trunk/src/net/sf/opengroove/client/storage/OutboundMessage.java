package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
@ProxyBean
public interface OutboundMessage
{
    public static final int STAGE_INITIALIZED = 1;
    public static final int STAGE_ENCODED = 2;
    public static final int STAGE_ENCRYPTED = 3;
    public static final int STAGE_UPLOADED = 4;
    public static final int STAGE_SENT = 5;
    @Property
    public String[] getTarget();
}
