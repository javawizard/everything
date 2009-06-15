package net.sf.opengroove.client.messaging;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.opengroove.g4.common.messaging.Message;
import org.opengroove.g4.common.protocol.InboundMessagePacket;
import org.opengroove.g4.common.protocol.LoginResponse;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.common.utils.ObjectUtils;

import net.sf.opengroove.client.com.Communicator;
import net.sf.opengroove.client.com.PacketListener;
import net.sf.opengroove.client.com.StatusListener;

public class MessageManager implements StatusListener,
    PacketListener<InboundMessagePacket>
{
    private Communicator communicator;
    private File inboundMessageFolder;
    private File outboundMessageFolder;
    
    private Map<Class, MessageHandler> handlerMap =
        new HashMap<Class, MessageHandler>();
    
    private ThreadPoolExecutor stackedThreadPool =
        new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    
    /**
     * A class that finds a handler for the message type specified and
     * dispatches the message to that handler. Once the message has been
     * dispatched to the handler, it will be deleted from the inbound message
     * folder.
     * 
     * @author Alexander Boyd
     * 
     */
    private class MessageDispatcher implements Runnable
    {
        private InboundMessagePacket messagePacket;
        
        public MessageDispatcher(InboundMessagePacket messagePacket)
        {
            this.messagePacket = messagePacket;
        }
        
        public void run()
        {
            /*
             * First we'll find a handler that can handle this message type.
             * 
             * FIXME: this only searches for a handler of the specific class
             * that the message is of; it doesn't search for a handler that
             * handles a supertype of this message. This needs to be added in
             * the future.
             */
            Object message = messagePacket.getMessage();
            MessageHandler handler = handlerMap.get(message.getClass());
            if (handler != null)
            {
                /*
                 * We have a handler. We'll hand it the message.
                 */
                handler.handle(message, messagePacket.getSender());
            }
            else
            {
                /*
                 * No handler for the message; we'll just discard it
                 */
                System.err.println("No handler for message type "
                    + message.getClass().getName() + ", discarding message");
            }
            /*
             * The message has been handled. Now we'll go off and delete the
             * message's backing file.
             */
            if (!new File(inboundMessageFolder, encodeId(messagePacket.getMessageId()))
                .delete())
                System.err.println("ERROR: couldn't delete message file "
                    + messagePacket.getMessageId()
                    + ", the message will be delivered multiple times. "
                    + "This is a critical issue.");
        }
    }
    
    /**
     * Encodes the specified message id into a format that can be used as a file
     * name. Currently, this just urlencodes the id.
     * 
     * @param id
     *            The id to encode
     * @return The encoded form of the id
     */
    private static String encodeId(String id)
    {
        return URLEncoder.encode(id);
    }
    
    public MessageManager(Communicator communicator, File inboundMessageFolder,
        File outboundMessageFolder)
    {
        this.communicator = communicator;
        communicator.addPacketListener(this);
        communicator.addStatusListener(this);
        this.inboundMessageFolder = inboundMessageFolder;
        this.outboundMessageFolder = outboundMessageFolder;
        inboundMessageFolder.mkdirs();
        outboundMessageFolder.mkdirs();
    }
    
    /**
     * Starts this message manager. This should be called after all appropriate
     * handlers are registered, but before the communicator that this message
     * manager uses is started.
     */
    public void start()
    {
        new Thread()
        {
            public void run()
            {
                File[] files = inboundMessageFolder.listFiles();
                for (File file : files)
                {
                    InboundMessagePacket packet =
                        (InboundMessagePacket) ObjectUtils.readObject(file);
                    stackedThreadPool.execute(new MessageDispatcher(packet));
                }
            }
        }.start();
    }
    
    public void authenticationFailed(LoginResponse response)
    {
        /*
         * Ignored
         */
    }
    
    public void connectionReady()
    {
        /*
         * Send off all messages cached on the file system for sending outward
         */
    }
    
    public void lostConnection()
    {
        /*
         * Ignored
         */
    }
    
    public void packetReceived(InboundMessagePacket packet)
    {
        /*
         * We store this in a file, in case
         */
    }
    
    public void processedPacketReceived(InboundMessagePacket packet)
    {
        /*
         * Ignored
         */
    }
    
    /**
     * Sends the specified message to the specified recipients. This queues the
     * message for sending, and then attempts to send it immediately if there is
     * an active server connection.
     * 
     * @param recipients
     *            The list of recipients for the message
     * @param message
     *            The message itself
     */
    public void sendMessage(Userid[] recipients, Object message)
    {
    }
    
    /**
     * Sends the specified message to the specified recipients. This method
     * first wraps the object in a PassThroughObject, so that the server doesn't
     * need to have the class definition of the message object in order for the
     * message to be successfully transferred. The message is wrapped in such a
     * way that the recipient's MessageManager will unwrap it and pass the
     * unwrapped object to the message handler. This method, therefore, behaves
     * exactly the same way as {@link #sendMessage(Userid[], Object)}, except
     * that it won't cause problems when the server doesn't have the class
     * definition of the message object.<br/>
     * <br/>
     * 
     * This method is, however, slower than sendMessage, so sendMessage should
     * be used in preference to this method.
     * 
     * @param recipients
     * @param message
     */
    public void sendWrappedMessage(Userid[] recipients, Object message)
    {
        ExtraCompilerModifiers
    }
    
}
