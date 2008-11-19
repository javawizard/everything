package net.sf.opengroove.client.messaging;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.sf.opengroove.client.TimerField;
import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.storage.InboundMessage;
import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.OutboundMessage;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.common.concurrent.ConditionalTimer;

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
    private boolean isRunning = true;
    private LocalUser localUser;
    private String userid;
    private MessageHierarchy hierarchyElement;
    private CommandCommunicator communicator;
    private Storage storage;
    private final Object runObject = new Object();
    private final Object quitObject = new Object();
    /**
     * This is how long each stage will wait before checking items, added to
     * delayVariance.
     */
    private final int delay = 1000 * 60 * 5;
    /**
     * The delay variance. This will be multiplied with Math.random() to produce
     * a number between 0 and this. This number will then be added to
     * <code>delay</code> for the delay in all threads. For example, if delay
     * is 5 minutes and delayVariance is 1 minute (those are the default
     * values), then each stage thread will wait between 5 or 6 (it changes each
     * time) minutes before scanning items. This is used to stagger when the
     * stage threads run, so as to not put a huge load on the ProxyStorage
     * system all at once.
     */
    private final int delayVariance = 1000 * 60;
    
    // stage threads
    @StageThread
    private Thread outboundEncoderThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = outboundEncoderQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    OutboundMessage[] messages = localUser
                        .listOutboundMessagesForStage(OutboundMessage.STAGE_INITIALIZED);
                    for (OutboundMessage message : messages)
                    {
                        File messagePlaintextFile = new File(
                            storage
                                .getOutboundMessagePlaintextStore(),
                            message.getId());
                        File messageEncodedFile = new File(
                            storage
                                .getOutboundMessageEncodedStore(),
                            message.getId());
                        if (!messagePlaintextFile.exists())
                        {
                            
                        }
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    @StageThread
    private Thread outboundEncrypterThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = outboundEncrypterQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    OutboundMessage[] messages = localUser
                        .listOutboundMessagesForStage(OutboundMessage.STAGE_ENCODED);
                    for (OutboundMessage message : messages)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    @StageThread
    private Thread outboundUploaderThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = outboundUploaderQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    OutboundMessage[] messages = localUser
                        .listOutboundMessagesForStage(OutboundMessage.STAGE_ENCRYPTED);
                    for (OutboundMessage message : messages)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    @StageThread
    private Thread outboundSenderThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = outboundSenderQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    OutboundMessage[] messages = localUser
                        .listOutboundMessagesForStage(OutboundMessage.STAGE_UPLOADED);
                    for (OutboundMessage message : messages)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    @StageThread
    private Thread inboundImporterThread = new Thread()
    {
        public void run()
        {
        }
    };
    @StageThread
    private Thread inboundDownloaderThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = inboundDownloaderQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    InboundMessage[] messages = localUser
                        .listInboundMessagesForStage(InboundMessage.STAGE_IMPORTED);
                    for (InboundMessage message : messages)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    @StageThread
    private Thread inboundLocalizerThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = inboundLocalizerQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    InboundMessage[] messages = localUser
                        .listInboundMessagesForStage(InboundMessage.STAGE_DOWNLOADED);
                    for (InboundMessage message : messages)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    @StageThread
    private Thread inboundDecrypterThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = inboundDecrypterQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    InboundMessage[] messages = localUser
                        .listInboundMessagesForStage(InboundMessage.STAGE_LOCALIZED);
                    for (InboundMessage message : messages)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    @StageThread
    private Thread inboundDecoderThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = inboundDecoderQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    InboundMessage[] messages = localUser
                        .listInboundMessagesForStage(InboundMessage.STAGE_DECRYPTED);
                    for (InboundMessage message : messages)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    @StageThread
    private Thread inboundDispatcherThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = inboundDispatcherQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    InboundMessage[] messages = localUser
                        .listInboundMessagesForStage(InboundMessage.STAGE_DECODED);
                    for (InboundMessage message : messages)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    };
    // stage notifier queues
    @StageQueue
    private BlockingQueue<Object> outboundEncoderQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> outboundEncrypterQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> outboundUploaderQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> outboundSenderQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> inboundImporterQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> inboundDownloaderQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> inboundLocalizerQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> inboundDecrypterQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> inboundDecoderQueue = new ArrayBlockingQueue<Object>(
        1);
    @StageQueue
    private BlockingQueue<Object> inboundDispatcherQueue = new ArrayBlockingQueue<Object>(
        1);
    
    // stage notifier methods
    public void notifyOutboundEncoder()
    {
        outboundEncoderQueue.offer(runObject);
    }
    
    public void notifyOutboundEncrypter()
    {
        outboundEncrypterQueue.offer(runObject);
    }
    
    public void notifyOutboundUploader()
    {
        outboundUploaderQueue.offer(runObject);
    }
    
    public void notifyOutboundSender()
    {
        outboundSenderQueue.offer(runObject);
    }
    
    public void notifyInboundImporter()
    {
        inboundImporterQueue.offer(runObject);
    }
    
    public void notifyInboundDownloader()
    {
        inboundDownloaderQueue.offer(runObject);
    }
    
    public void notifyInboundLocalizer()
    {
        inboundLocalizerQueue.offer(runObject);
    }
    
    public void notifyInboundDecrypter()
    {
        inboundDecrypterQueue.offer(runObject);
    }
    
    public void notifyInboundDecoder()
    {
        inboundDecoderQueue.offer(runObject);
    }
    
    public void notifyInboundDispatcher()
    {
        inboundDispatcherQueue.offer(runObject);
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
        storage = Storage.get(userid);
        hierarchyElement.setMessageDeliverer(this);
    }
    
    public OutboundMessage createMessage()
    {
        OutboundMessage message = localUser
            .createOutboundMessage();
        message.setId(userid + "-"
            + Storage.createIdentifier());
        return message;
    }
    
    public void sendMessage(OutboundMessage message)
    {
        /*
         * All we have to do is add the message to the local user's list of
         * messages (if it's not already present), and notify the message
         * encoder that it has messages available
         */
    }
    
    public void start()
    {
        /*
         * Start all of the threads running, and scan for messages to delete.
         * Then feed all queues the runObject so that they will perform an
         * initial pass.
         */
    }
    
    public void stop()
    {
        /*
         * Feed all queues the quitObject so that the threads will quit. This
         * needs to be forced on all of the queues.
         */
        isRunning = false;
        for (final BlockingQueue queue : getStageQueues())
        {
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        queue.put(quitObject);
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }.start();
        }
    }
    
    public File getInboundMessageFile(String messageId)
    {
        return new File(storage
            .getInboundMessagePlaintextStore(), messageId
            .replace(":", "$"));
    }
    
    public File getOutboundMessageFile(String messageId)
    {
        return new File(storage
            .getOutboundMessagePlaintextStore(), messageId
            .replace(":", "$"));
    }
    
    private BlockingQueue[] getStageQueues()
    {
        Field[] fields = getClass().getDeclaredFields();
        ArrayList<BlockingQueue> timers = new ArrayList<BlockingQueue>();
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(StageQueue.class)
                && BlockingQueue.class
                    .isAssignableFrom(field.getType()))
            {
                try
                {
                    if (field.get(this) != null)
                        timers.add((BlockingQueue) field
                            .get(this));
                }
                catch (IllegalAccessException e)
                {
                    // should never be thrown
                    e.printStackTrace();
                }
            }
            else if (field
                .isAnnotationPresent(StageQueue.class))
            {
                System.err
                    .println("Field "
                        + field.getName()
                        + "declared in UserContext as a timer field but is "
                        + "not an instance of ConditionalTimer");
            }
        }
        return timers.toArray(new BlockingQueue[0]);
    }
    
    private Thread[] getStageThreads()
    {
        Field[] fields = getClass().getDeclaredFields();
        ArrayList<Thread> timers = new ArrayList<Thread>();
        for (Field field : fields)
        {
            if (field
                .isAnnotationPresent(StageThread.class)
                && Thread.class.isAssignableFrom(field
                    .getType()))
            {
                try
                {
                    if (field.get(this) != null)
                        timers
                            .add((Thread) field.get(this));
                }
                catch (IllegalAccessException e)
                {
                    // should never be thrown
                    e.printStackTrace();
                }
            }
            else if (field
                .isAnnotationPresent(StageThread.class))
            {
                System.err
                    .println("Field "
                        + field.getName()
                        + "declared in UserContext as a timer field but is "
                        + "not an instance of ConditionalTimer");
            }
        }
        return timers.toArray(new Thread[0]);
    }
    
}
