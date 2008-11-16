package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface InboundMessage
{
    /**
     * Indicates that basic information about this message, mainly it's id and
     * sender, has been downloaded.
     */
    public static final int STAGE_IMPORTED = 1;
    /**
     * Indicates that the encrypted contents of the message have been
     * downloaded.
     */
    public static final int STAGE_DOWNLOADED = 2;
    /**
     * Indicates that the message has been deleted from the server. The message
     * is not decrypted yet.
     */
    public static final int STAGE_LOCAL = 3;
    /**
     * Indicates that the message has been decrypted and it's signature
     * verified. Messages with an invalid signature or encryption will never
     * reach this stage; instead, they will be deleted.
     */
    public static final int STAGE_DECRYPTED = 4;
    /**
     * Indicates that the message has been decoded. This means that it's
     * destination info has been extracted into the message's InboundMessage
     * object and the actual message contents
     */
    public static final int STAGE_DECODED = 5;
    public static final int STAGE_READ = 6;
}
