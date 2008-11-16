package net.sf.opengroove.client.messaging;

/**
 * This class manages messaging for OpenGroove users. It essentially implements
 * <a
 * href="http://www.opengroove.org/dev/things-to-consider/things-to-consider-1">
 * this document</a>. When it is started (via a call to start()), it creates
 * one thread for each stage described in the aforementioned document that uses
 * a thread. It provides methods to tell a particular stage to wake up and check
 * for messages that it needs to process. Currently, each stage will only
 * process one message at a time; this has the disadvantage that a particularly
 * large message could hold up smaller messages after it from being sent or
 * received for quite some time. This will be changed in the future.
 * 
 * When initially started, it checks to see if there are any message storage
 * files without backing messages, or message storage files where the message
 * has passed that particular stage, and deletes these. It also deletes messages
 * in the SENT or READ stages.
 * 
 * Before the message manager is started, it needs to be provided with a
 * CommandCommunicator and a root MessageHierarchyListener. After messages have
 * been downloaded, decrypted, and decoded, it will inject them into the
 * MessageHierarchyListener provided. The listeners
 * 
 * @author Alexander Boyd
 * 
 */
public class MessageManager
{
    
}
