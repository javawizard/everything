package net.sf.opengroove.client.messaging;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.opengroove.g4.common.messaging.Message;
import org.opengroove.g4.common.object.ExtractedPassThroughObject;
import org.opengroove.g4.common.protocol.InboundMessagePacket;
import org.opengroove.g4.common.protocol.LoginResponse;
import org.opengroove.g4.common.protocol.MessageResponse;
import org.opengroove.g4.common.protocol.OutboundMessagePacket;
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
    
    public void registerMessageHandler(Class c, MessageHandler handler)
    {
        handlerMap.put(c, handler);
    }
    
    private ThreadPoolExecutor stackedThreadPool =
        new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    
    private class MessageResponseReceiver implements PacketListener<MessageResponse>
    {
        
        public void packetReceived(MessageResponse packet)
        {
            /*
             * Delete the local file for the message, if we still have it.
             */
            File file =
                new File(outboundMessageFolder, encodeId(packet.getMessageId()));
            if (!file.delete())
                System.err.println("ERROR: Couldn't delete outbound message file "
                    + packet.getMessageId() + ", the message will be sent twice. "
                    + "This is a critical error.");
        }
        
        public void processedPacketReceived(MessageResponse packet)
        {
            /*
             * Ignored.
             */
        }
        
    }
    
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
            /*
             * If the message was sent by the sender with sendWrappedMessage, we
             * need to unwrap the message before we handle it
             */
            if (message instanceof ExtractedPassThroughObject)
            {
                message = ((ExtractedPassThroughObject) message).getObject();
            }
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
    
    private Userid localUser;
    
    public MessageManager(Userid localUser, Communicator communicator,
        File inboundMessageFolder, File outboundMessageFolder)
    {
        this.localUser = localUser;
        this.communicator = communicator;
        communicator.addPacketListener(this);
        communicator.addPacketListener(new MessageResponseReceiver());
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
        new Thread()
        {
            public void run()
            {
                File[] files = outboundMessageFolder.listFiles();
                for (File file : files)
                {
                    if (!communicator.isConnected())
                        /*
                         * We lost our connection, so we'll just return.
                         */
                        return;
                    OutboundMessagePacket packet =
                        (OutboundMessagePacket) ObjectUtils.readObject(file);
                    if (communicator.isConnected())
                    {
                        communicator.send(packet);
                    }
                    else
                    {
                        return;
                    }
                    /*
                     * FIXME: if the communicator disconnects and then
                     * reconnects while we're reading off and sending messages,
                     * then we might send a message twice.
                     */
                }
            }
        }.start();
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
         * First, we store the message to a file in the inbound message folder.
         * If there's already a file with the name of the message id, then we've
         * already received the message so we'll discard it.
         */
        File file = new File(inboundMessageFolder, encodeId(packet.getMessageId()));
        if (file.exists())
        {
            /*
             * Message already received, so we'll just return.
             */
            return;
        }
        /*
         * Message hasn't been received. We'll write it to disk.
         */
        ObjectUtils.writeObject(packet, file);
        /*
         * We've got the message safely on disk. Now we'll tell the server that
         * we have the message.
         */
        MessageResponse response = new MessageResponse();
        response.setMessageId(packet.getMessageId());
        response.setPacketThread(packet.getPacketThread());
        if (!communicator.isConnected())
        {
            /*
             * Lost connection. We'll discard the message, since we'll be
             * getting it the next time we connect.
             */
            file.delete();
            return;
        }
        try
        {
            communicator.send(packet);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        /*
         * We've told the server that we received the message. Now we'll
         * dispatch it.
         */
        stackedThreadPool.execute(new MessageDispatcher(packet));
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
        if (message == null)
            throw new IllegalArgumentException("Can't send a null message");
        OutboundMessagePacket packet = new OutboundMessagePacket();
        packet.setMessageId(localUser.withoutComputer().toString() + "$"
            + Communicator.generateThreadId());
        packet.setRecipients(recipients);
        packet.setMessage(message);
        /*
         * Now we'll write the message to disk.
         */
        File file = new File(outboundMessageFolder, encodeId(packet.getMessageId()));
        ObjectUtils.writeObject(packet, file);
        /*
         * We've written the message to disk. Now we'll send it to the
         * communicator, if the communicator currently has a connection.
         */
        if (communicator.isConnected())
        {
            try
            {
                communicator.send(packet);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
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
        if (message == null)
            throw new IllegalArgumentException("Can't send a null message");
        ExtractedPassThroughObject wrapper = new ExtractedPassThroughObject();
        wrapper.setObject(message);
        sendMessage(recipients, wrapper);
    }
    
}
