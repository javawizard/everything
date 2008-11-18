package net.sf.opengroove.client.messaging;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.OutboundMessage;
import net.sf.opengroove.client.storage.Storage;

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
 * CommandCommunicator, a string that represents, and a root
 * MessageHierarchyListener. It will set itself as a MessageDeliverer on the
 * MessageHierarchyListener. After messages have been downloaded, decrypted, and
 * decoded, it will inject them into the MessageHierarchyListener provided. It
 * will not, however, delete the message until the stage is set to READ. It will
 * also check upon startup for messages in the SENT and READ stage, as well as
 * messages files without a message in that stage, and delete them.
 * 
 * @author Alexander Boyd
 * 
 */
public class MessageManager implements MessageDeliverer
{
    private LocalUser localUser;
    private String userid;
    private MessageHierarchy hierarchyElement;
    private CommandCommunicator communicator;
    
    // stage threads
    private Thread thread = new Thread()
    {
        public void run()
        {
        }
    };
    // stage notifier queues
    private BlockingQueue<Object> outboundEncoderQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> outboundEncrypterQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> outboundUploaderQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> outboundSenderQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> inboundImporterQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> inboundDownloaderQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> inboundLocalizerQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> inboundDecrypterQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> inboundDecoderQueue = new ArrayBlockingQueue<Object>(
        1);
    private BlockingQueue<Object> inboundDispatcherQueue = new ArrayBlockingQueue<Object>(
        1);
    
    // stage notifier methods
    public void notifyOutboundEncoder()
    {
        outboundEncoderQueue.offer(new Object());
    }
    
    public void notifyOutboundEncrypter()
    {
        outboundEncrypterQueue.offer(new Object());
    }
    
    public void notifyOutboundUploader()
    {
        outboundUploaderQueue.offer(new Object());
    }
    
    public void notifyOutboundSender()
    {
        outboundSenderQueue.offer(new Object());
    }
    
    public void notifyInboundImporter()
    {
        inboundImporterQueue.offer(new Object());
    }
    
    public void notifyInboundDownloader()
    {
        inboundDownloaderQueue.offer(new Object());
    }
    
    public void notifyInboundLocalizer()
    {
        inboundLocalizerQueue.offer(new Object());
    }
    
    public void notifyInboundDecrypter()
    {
        inboundDecrypterQueue.offer(new Object());
    }
    
    public void notifyInboundDecoder()
    {
        inboundDecoderQueue.offer(new Object());
    }
    
    public void notifyInboundDispatcher()
    {
        inboundDispatcherQueue.offer(new Object());
    }
    
    // methods
    
    public MessageManager(String userid,
        CommandCommunicator communicator,
        MessageHierarchy hierarchyElement)
    {
        this.userid = userid;
        this.communicator = communicator;
        this.hierarchyElement = hierarchyElement;
        localUser = Storage.getLocalUser(userid);
        hierarchyElement.setMessageDeliverer(this);
    }
    
    public OutboundMessage createMessage()
    {
        return localUser.createOutboundMessage();
    }
    
    public void sendMessage(OutboundMessage message)
    {
        /*
         * All we have to do is add the message to the local user's list of
         * messages (if it's not already present), and notify the message
         * encoder that it has messages available
         */
    }
    
}
