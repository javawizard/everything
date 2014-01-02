package net.sf.opengroove.client.messaging;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import net.sf.opengroove.client.storage.InboundMessage;
import net.sf.opengroove.client.storage.OutboundMessage;
import net.sf.opengroove.common.utils.StringUtils;

/**
 * A class for allowing hierarchical organization of message sending and
 * receiving. It extends the listener concept to provide hierarchical listening
 * and message sending.<br/><br/>
 * 
 * When paths are referenced as ArrayLists, the first element is the highest in
 * the hierarchy and the last element is the lowest (IE path element n denotes a
 * hierarchy element who's parent is path element n - 1)<br/><br/>
 * 
 * When a message is received, the data contents of the message are stored in
 * the file obtained by getInboundMessageFile(). When an inbound message is
 * marked as read, it is recommended that the corresponding file be deleted.
 * This is not required, however, as OpenGroove will periodically scan and
 * delete files where the message is marked as read or where the message doesn't
 * exist.<Br/><br/>
 * 
 * When a message is to be sent, A message object is created by a call to
 * createMessage(). Then the data is written to the file returned by
 * getOutboundMessage().
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class MessageHierarchy
{
    /**
     * A map that maps names to child elements. When a child is added to this
     * hierarchy element, it is placed in this map with it's name as the key.
     */
    private HashMap<String, MessageHierarchy> children = new HashMap<String, MessageHierarchy>();
    /**
     * The MessageDeliverer associated with this hierarchy. Generally, only the
     * top level hierarchy element should have a deliverer. The rest of them
     * will delegate to their parent. In fact, the deliverer will not be used if
     * this hierarchy element has a parent.
     */
    private MessageDeliverer deliverer;
    private MessageReceiver receiver;
    
    public void setReceiver(MessageReceiver receiver)
    {
        this.receiver = receiver;
    }
    
    /**
     * The name of this hierarchy element. This is the string that appears in a
     * hierarchy path to denote this hierarchy element. This must not be changed
     * after this element is added to another hierarchy element, and generally
     * shouldn't change anyway after construction.
     * 
     * This is irrelevant for the toplevel hierarchy element, and won't be used
     * if present. The toplevel hierarchy element is addressed using the empty
     * string, and it's children are addressed by using a pathname consisting
     * only of that particular child's name.
     */
    private String name;
    /**
     * The parent hierarchy element of this hierarchy element. received messages
     * should always come from it, and sent messages will always be routed to
     * it. If this is null (meaning that this hierarchy element has no parent),
     * then {@link #deliverer} must not be null. If they are both null, then an
     * exception will be thrown when any of the sendMessage methods are called,
     * or when the sendAbsoluteMessage method is called.
     */
    private MessageHierarchy parent;
    
    public MessageHierarchy(String name)
    {
        this.name = name;
    }
    
    public void add(MessageHierarchy element)
    {
        children.put(element.name, element);
        element.parent = this;
    }
    
    /**
     * Called as a message propegates down through the hierarchy, before the
     * message propegates. This will be called before the target listener's
     * handleMessage method is called. This will also be called before
     * handleInvalidLowerMessage is called. Subclasses can override this method.
     * 
     * @param message
     */
    public void beforeHandleLowerMessage(
        InboundMessage message)
    {
        /*
         * Do nothing. Subclasses can override this method if they want.
         */
    }
    
    /**
     * Called after a message has been handled by it's target, as the message
     * propegates back up. This will also be called after the closest target's
     * handleInvalidLowerMessage if the message is invalid. Subclasses can
     * override this method.
     * 
     * @param message
     */
    public void afterHandleLowerMessage(
        InboundMessage message)
    {
        /*
         * Do nothing. Subclasses can override this method if they want.
         */
    }
    
    /**
     * Handles a message. If this is called, it means that the message targets
     * this hierarchy element specifically, not an element lower down in the
     * hierarchy.
     * 
     * @param message
     */
    public abstract void handleMessage(
        InboundMessage message);
    
    /**
     * Called if a message cannot propegate further down in the hierarchy
     * because the element that it targets does not exist. This will only be
     * called on the hierarchy element closest to where the message was supposed
     * to go; Hierarchy elements higher up will only receive calls to
     * afterHandleLowerMessage and beforeHandleLowerMessage. Subclasses are
     * strongly encouraged to override this method.
     * 
     * @param message
     */
    public void handleInvalidLowerMessage(
        InboundMessage message)
    {
        /*
         * Do nothing. Subclasses can override this method if they want.
         */
    }
    
    /**
     * Injects a message into this hierarchy. Generally, implementors of this
     * class don't need to call this. This is called when some other mechanism
     * receives a message and wishes this hierarchy to process it. This class
     * will then take care of figuring out where the message is supposed to go.
     * 
     * @param message
     */
    public void injectMessage(InboundMessage message)
    {
        sendDownward(message,
            parsePath(message.getTarget()));
    }
    
    /**
     * Sends a message downward. currentPath should not contain this hierarchy's
     * name. If currentPath is empty, then this hierarchy element is the target
     * and will dispatch the message accordingly.
     * 
     * @param message
     * @param currentPath
     */
    private void sendDownward(InboundMessage message,
        ArrayList<String> currentPath)
    {
        if (currentPath.size() == 0
            || (currentPath.size() == 1 && currentPath.get(
                0).equals("")))
        {
            /*
             * Addressed to us.
             */
            handleMessage(message);
        }
        else
        {
            /*
             * Addressed to a child of us.
             */
            String nextElement = currentPath.get(0);
            currentPath.remove(0);
            beforeHandleLowerMessage(message);
            MessageHierarchy child = children
                .get(nextElement);
            if (child == null)
            {
                handleInvalidLowerMessage(message);
            }
            else
            {
                child.sendDownward(message, currentPath);
            }
            afterHandleLowerMessage(message);
        }
    }
    
    /**
     * Sets the message deliverer for this hierarchy. It takes care of actually
     * sending a message. It also takes care of creating new message objects
     * when a message is to be created (should this be the case or should a
     * message to send just have recipient info and such, probably, so that it's
     * target can be built up as it propegates up the hierarchy, or should a new
     * message object be created by the MessageDeliverer and propegated down or
     * something...)
     * 
     * @param sender
     */
    public void setMessageDeliverer(MessageDeliverer sender)
    {
        this.deliverer = sender;
    }
    
    /**
     * Creates a new outbound message, which, by default, has none of it's
     * options set besides it's stage. It's stage should not be modified. After
     * configuring the resulting message, it can be passed to one of the
     * sendMessage methods. The target for the message does not need to be set
     * before sending. Actually, sending it will overwrite the target.
     * 
     * Here's an explanation of how
     * 
     * @return
     */
    public OutboundMessage createMessage()
    {
        if (deliverer == null)
        {
            if (parent == null)
                throw new IllegalStateException(
                    "This hierarchy does not have a deliverer or a parent. At least one of these is required to create a new message.");
            else
                return parent.createMessage();
        }
        return deliverer.createMessage();
    }
    
    public File getOutboundMessageFile(String messageId)
    {
        if (deliverer == null)
        {
            if (parent == null)
                throw new IllegalStateException(
                    "This hierarchy does not have a deliverer or a parent. At least one of these is required to get a message's file.");
            else
                return parent
                    .getOutboundMessageFile(messageId);
        }
        return deliverer.getOutboundMessageFile(messageId);
    }
    
    public File getInboundMessageFile(String messageId)
    {
        if (deliverer == null)
        {
            if (parent == null)
                throw new IllegalStateException(
                    "This hierarchy does not have a deliverer or a parent. At least one of these is required to get a message's file.");
            else
                return parent
                    .getInboundMessageFile(messageId);
        }
        return deliverer.getInboundMessageFile(messageId);
    }
    
    /**
     * Sends the message specified. It's recipient information and metadata
     * should have already been set. It's target will be set to this hierarchy
     * element (following it's path up to the toplevel hierarchy element). The
     * message should not be modified after this call.
     * 
     * @param message
     */
    public void sendMessage(OutboundMessage message)
    {
        sendUpward(message, new ArrayList<String>(), true);
    }
    
    /**
     * Sends the message specified to a path relative to this hierarchy element.
     * The path string can contain "." and ".." sequences. A path string of "."
     * means this hierarchy element. A path string of ".." means the parent
     * hierarchy element. A path string of "something" would mean a hierarchy
     * element that is a sibling of this one (not a child) by the name
     * "something". For a child named "something" of this element, you would
     * have to use the path string "./something".
     * 
     * @param message
     * @param relativePath
     */
    public void sendMessage(OutboundMessage message,
        String relativePath)
    {
        throw new IllegalStateException(
            "This hasn't been implemented yet. Only sendMessage "
                + "and sendAbsoluteMessage have.");
    }
    
    /**
     * Sends a message to an absolute path. An empty path references the
     * toplevel hierarchy element. A path of "something" would be the hierarchy
     * element under the toplevel hierarchy element, named "something".
     * 
     * @param message
     * @param absolutePath
     */
    public void sendAbsoluteMessage(
        OutboundMessage message, String absolutePath)
    {
        sendUpward(message, parsePath(absolutePath), false);
    }
    
    /**
     * Sends a message upward, optionally prepending this hierarchy element's
     * path before sending it. If this element has no parent (and the message
     * will be dispatched to the deliverer), then the path will not be
     * prepended, even if addPath is true.
     * 
     * @param message
     * @param currentPath
     * @param addPath
     */
    private void sendUpward(OutboundMessage message,
        ArrayList<String> currentPath, boolean addPath)
    {
        if (parent == null)
        {
            /*
             * We're at the toplevel, so we need to send this to the deliverer
             * if there is one.
             */
            if (deliverer == null)
                throw new IllegalStateException(
                    "No deliverer and no parent");
            message.setTarget(buildPath(currentPath));
            deliverer.sendMessage(message);
        }
        else
        {
            /*
             * if addPath is true, then we'll prepend this element's path, then
             * send the message to this element's parent.
             */
            if (addPath)
                currentPath.add(0, name);
            parent
                .sendUpward(message, currentPath, addPath);
        }
    }
    
    private InboundMessage[] sendMessageRequestUpward(
        ArrayList<String> currentPath, boolean addPath,
        boolean floating)
    {
        if (parent == null)
        {
            /*
             * We're at the toplevel, so we need to send this to the deliverer
             * if there is one.
             */
            if (receiver == null)
                throw new IllegalStateException(
                    "No deliverer and no parent");
            if (floating)
                return receiver
                    .listChildMessages(buildPath(currentPath));
            else
                return receiver
                    .listMessages(buildPath(currentPath));
        }
        else
        {
            /*
             * if addPath is true, then we'll prepend this element's path, then
             * send the message to this element's parent.
             */
            if (addPath)
                currentPath.add(0, name);
            return parent.sendMessageRequestUpward(
                currentPath, addPath, floating);
        }
    }
    
    public InboundMessage[] listMessages()
    {
        return sendMessageRequestUpward(
            new ArrayList<String>(), true, false);
    }
    
    public InboundMessage[] listChildMessages()
    {
        return sendMessageRequestUpward(
            new ArrayList<String>(), true, true);
    }
    
    private static String buildPath(
        ArrayList<String> pathComponents)
    {
        return StringUtils.delimited(pathComponents
            .toArray(new String[0]), "/");
    }
    
    private static ArrayList<String> parsePath(String path)
    {
        ArrayList<String> pathComponents = new ArrayList<String>();
        String[] tokens = path.split("\\/");
        if (tokens.length == 1 && tokens[0].equals(""))
            return new ArrayList<String>();
        pathComponents.addAll(Arrays.asList(tokens));
        return pathComponents;
    }
}
