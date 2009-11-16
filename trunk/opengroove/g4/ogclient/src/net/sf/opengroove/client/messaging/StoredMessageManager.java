package net.sf.opengroove.client.messaging;

import java.io.File;
import java.net.URLEncoder;

import org.opengroove.g4.common.TemporaryFileStore;
import org.opengroove.g4.common.messaging.Message;
import org.opengroove.g4.common.messaging.MessageHeader;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.common.utils.ObjectUtils;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.ui.frames.ComposeMessageFrame;

public class StoredMessageManager implements MessageHandler
{
    public static enum Type
    {
        Draft, Sent, Unread, Read
    }
    
    private MessageManager messageManager;
    private Storage storage;
    private Userid localUserid;
    private File headerFolder;
    private File draftsFolder;
    private File sentFolder;
    private File unreadFolder;
    private File readFolder;
    /**
     * A lock that most operations lock on. This is to prevent certain threading
     * issues that can arise when two operations run at the same time.
     */
    private final Object messageProcessingLock = new Object();
    
    /**
     * Creates a new StoredMessageManager. The new manager will register itself
     * as a handler for Message objects on the specified message manager, and it
     * will store data to and read data from the specified storage object. It
     * will also use the specified userid when adding tray notifications for
     * messages.
     * 
     * The message manager must not be started when the stored message manager
     * is created.
     * 
     * @param manager
     *            The message manager for this user. This stored message manager
     *            will register itself as a handler on this manager, and it will
     *            use this manager to send stored messages.
     * @param storage
     *            The storage object that this stored message manager should use
     *            to store messages
     * @param user
     *            The computer userid of the user that this manager is for
     */
    public StoredMessageManager(MessageManager manager, Storage storage, Userid user)
    {
        this.messageManager = manager;
        this.storage = storage;
        this.localUserid = user;
        this.draftsFolder = storage.getMessageDraftsFolder();
        this.headerFolder = storage.getMessageHeaderFolder();
        this.readFolder = storage.getMessageReadFolder();
        this.sentFolder = storage.getMessageSentFolder();
        this.unreadFolder = storage.getMessageUnreadFolder();
        manager.registerMessageHandler(Message.class, this);
    }
    
    /**
     * Performs startup steps that must be run when the backing message manager
     * has not yet been started.
     */
    public void startBeforeManager()
    {
        synchronized (messageProcessingLock)
        {
            /*
             * Add messages in the unread folder to the tray notification frame
             */

            /*
             * Scan the current message set. For headers without a message,
             * delete the header. For messages without a header, open the
             * message and save the header from it.
             */
        }
    }
    
    /**
     * Performs startup steps that must be run when the backing message manager
     * has been started.
     */
    public void startAfterManager()
    {
        synchronized (messageProcessingLock)
        {
            /*
             * Nothing to do here for now, although something's telling me there
             * was supposed to be code here...
             */
        }
    }
    
    public void handle(Object message, Userid sender)
    {
    }
    
    /**
     * Saves the specified message to the drafts folder, saves the message
     * header to the headers folder, and instructs the user's message history
     * frame to reload. Contrary to the current method name, which will probably
     * be changed, this method can be used to save an existing draft too.<br/>
     * <br/>
     * 
     * If there is already a header for this message id but there is no file in
     * the drafts folder for this message, which usually indicates that this
     * message was just sent, then this method throws an exception.
     * 
     * @param message
     *            The message to store
     */
    public void addNewDraftMessage(Message message)
    {
        synchronized (messageProcessingLock)
        {
            /*
             * Check for existence of header but not draft and throw exception
             */
            String filename = encodeMessageId(message.getHeader().getMessageId());
            File existingHeaderFile = new File(headerFolder, filename);
            File existingDraftFile = new File(draftsFolder, filename);
            if (existingHeaderFile.exists() && !existingDraftFile.exists())
                throw new RuntimeException("Missing draft file but present header file");
            /*
             * Save the header to a temporary file, and then the message.
             */
            File headerFile = TemporaryFileStore.createPersistentFile();
            File messageFile = TemporaryFileStore.createPersistentFile();
            ObjectUtils.writeObject(message.getHeader(), headerFile);
            ObjectUtils.writeObject(message, messageFile);
            /*
             * Now rename the files. The draft goes first, then the header,
             * since a header file without a draft is ok but a draft without a
             * header file will cause major problems (like stack traces) in a
             * lot of other code
             */
            if (existingDraftFile.exists())
                if (!existingDraftFile.delete())
                    throw new RuntimeException("Couldn'd delete existing draft");
            if (existingHeaderFile.exists())
                if (!headerFile.delete())
                    throw new RuntimeException("Couldn't delete header");
            if (!headerFile.renameTo(existingHeaderFile))
                throw new RuntimeException("Couldn't rename header file");
            if (!messageFile.renameTo(existingDraftFile))
                throw new RuntimeException("Couldn't delete backing draft file");
            /*
             * That's it! Now we reload the message history frame.
             */
            OpenGroove.getUserContext(localUserid).getMessageHistoryFrame().reload();
        }
    }
    
    /**
     * Sends a message that is currently saved as a draft. This deserializes the
     * message from the draft message file, sends it to the message manager for
     * processing, and then moves the message's file from the drafts folder to
     * the sent folder. It also instructs the user's message history frame to
     * reload.
     * 
     * @param id
     *            The id of the message to send
     * @throws RuntimeException
     *             if the message id specified does not identify a draft message
     */
    public void sendDraftMessage(String id)
    {
        synchronized (messageProcessingLock)
        {
            File draftFile = new File(draftsFolder, encodeMessageId(id));
            if (!draftFile.exists())
                throw new RuntimeException("Nonexistent draft");
            /*
             * First, we read in the draft file.
             */
            Message message = (Message) ObjectUtils.readObject(draftFile);
            /*
             * Now we queue it for delivery via the message manager. The message
             * manager will handle storing the message until we're online.
             */
            messageManager.sendMessage(message.getHeader().getRecipients(), message);
            /*
             * The message has been sent. Now we delete the draft file, and then
             * delete the header.
             */
            File headerFile = new File(headerFolder, encodeMessageId(id));
            if (!draftFile.delete())
                throw new RuntimeException("Couldn't delete draft file");
            if (!headerFile.delete())
                throw new RuntimeException("Couldn't delete header file");
            /*
             * Now reload the history frame, and we're done.
             */
            OpenGroove.getUserContext(localUserid).getMessageHistoryFrame().reload();
        }
    }
    
    /**
     * Discards the draft message of the specified id.
     * 
     * @param id
     *            the message's id
     */
    public void discardDraft(String id)
    {
        synchronized (messageProcessingLock)
        {
            File draftFile = new File(draftsFolder, encodeMessageId(id));
            File headerFile = new File(headerFolder, encodeMessageId(id));
            if (!(draftFile.exists() && headerFile.exists()))
                throw new RuntimeException("Nonexistent message " + id);
            if (!draftFile.delete())
                throw new RuntimeException("Couldn't delete draft file " + id);
            if (!headerFile.delete())
                throw new RuntimeException("Couldn't delete header file " + id);
            OpenGroove.getUserContext(localUserid).getMessageHistoryFrame().reload();
        }
    }
    
    /**
     * Encodes a message id into a format suitable for using as a disk file name
     * without the operating system complaining about invalid characters. Right
     * now this just urlencodes it.
     * 
     * @param id
     * @return
     */
    public String encodeMessageId(String id)
    {
        return URLEncoder.encode(id);
    }
    
    /**
     * If a compose message frame for the specified message id is already open,
     * then does nothing. If not, opens a new compose message frame for the
     * message. If the message is an inbound, unread message, it will be marked
     * as read and then opened. If the message is an inbound, read message, it
     * will be opened as is. If the message is an outbound, draft message, it
     * will be opened in such a way that it can be edited and sent. If the
     * message is an outbound, sent message, it will be opened just like a read
     * message would.<br/>
     * <br/>
     * 
     * If the message is both inbound and outbound, the inbound version will be
     * preferentially opened.
     * 
     * @param messageId
     */
    public void openMessage(String messageId)
    {
        ComposeMessageFrame.showComposeMessageFrame()
    }
    
    public Object getLock()
    {
        return messageProcessingLock;
    }
    
    public Message getMessageObject(String messageId)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Returns the number of messages in the message store. This counts the
     * message headers, so there could be problems if a previous operation
     * failed and left a superfluous header behind. But that isn't supposed to
     * happen, and if it does there's a bug in the code that caused it in the
     * first place.
     * 
     * @return The number of messages, of any type, in this store
     */
    public int getMessageCount()
    {
        synchronized (getLock())
        {
            return headerFolder.list().length;
        }
    }
    
    /**
     * Gets the type of a message. For messages that are both inbound and
     * outbound, this returns the inbound version's type. For nonexistent
     * messages, null is returned.
     * 
     * @param id
     *            The message'd id
     * @return The message's type
     */
    public Type getMessageType(String id)
    {
        synchronized (getLock())
        {
            String encoded = encodeMessageId(id);
            if (new File(unreadFolder, encoded).exists())
                return Type.Unread;
            else if (new File(readFolder, encoded).exists())
                return Type.Read;
            else if (new File(draftsFolder, encoded).exists())
                return Type.Draft;
            else if (new File(sentFolder, encoded).exists())
                return Type.Sent;
            else
                return null;
        }
    }
    
    public MessageHeader getMessageHeaderByIndex(int rowIndex)
    {
        synchronized (getLock())
        {
            return (MessageHeader) ObjectUtils
                .readObject(headerFolder.listFiles()[rowIndex]);
        }
    }
}
