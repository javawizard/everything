package net.sf.opengroove.client.messaging;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import base64.Base64Coder;

import net.sf.opengroove.client.TimerField;
import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.com.FailedResponseException;
import net.sf.opengroove.client.com.model.StoredMessage;
import net.sf.opengroove.client.com.model.StoredMessageRecipient;
import net.sf.opengroove.client.storage.Contact;
import net.sf.opengroove.client.storage.InboundMessage;
import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.MessageProperty;
import net.sf.opengroove.client.storage.OutboundMessage;
import net.sf.opengroove.client.storage.OutboundMessageRecipient;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.UserMessage;
import net.sf.opengroove.common.concurrent.ConditionalTimer;
import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.security.RSA;
import net.sf.opengroove.common.utils.DataUtils;
import net.sf.opengroove.common.utils.StringUtils;

/**
 * This class manages messaging for OpenGroove users. It essentially implements
 * <a href=
 * "http://www.opengroove.org/dev/things-to-consider/things-to-consider-1"> this
 * document</a>. When it is started (via a call to start()), it creates one
 * thread for each stage described in the aforementioned document that uses a
 * thread. It provides methods to tell a particular stage to wake up and check
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
public class MessageManager implements MessageDeliverer,
    MessageReceiver
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
    private final int delay = 1000 * 60 * 3;
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
                    System.out
                        .println("scanning for initialized outbound messages");
                    for (OutboundMessage message2 : messages)
                    {
                        try
                        {
                            OutboundMessage message = message2;
                            System.out
                                .println("processing stage 1 outbound message "
                                    + message.getId());
                            File messagePlaintextFile = new File(
                                storage
                                    .getOutboundMessagePlaintextStore(),
                                message.getFileId());
                            File messageEncodedFile = new File(
                                storage
                                    .getOutboundMessageEncodedStore(),
                                message.getFileId());
                            if (!messagePlaintextFile
                                .exists())
                            {
                                System.err
                                    .println("No data for message "
                                        + message.getId()
                                        + ". The message will be deleted.");
                                localUser
                                    .getOutboundMessages()
                                    .remove(message);
                                continue;
                            }
                            if (messageEncodedFile.exists())
                                /*
                                 * Something interrupted the previous encoding,
                                 * so we'll just delete it and start over.
                                 */
                                if (!messageEncodedFile
                                    .delete())
                                    throw new RuntimeException(
                                        "Couldn't delete previous encoding.");
                            messageEncodedFile
                                .createNewFile();
                            /*
                             * We now have the message plaintext file, and an
                             * empty file to encode it into. We'll begin
                             * encoding.
                             */
                            FileInputStream in = new FileInputStream(
                                messagePlaintextFile);
                            FileOutputStream out = new FileOutputStream(
                                messageEncodedFile);
                            DataOutputStream dout = new DataOutputStream(
                                out);
                            /*
                             * First, we write the target path.
                             */
                            String targetPath = message
                                .getTarget();
                            dout.writeInt(targetPath
                                .getBytes().length);
                            dout.write(targetPath
                                .getBytes());
                            /*
                             * Now we write the message properties.
                             */
                            dout.writeInt(message
                                .getProperties().size());
                            for (MessageProperty property : message
                                .getProperties().isolate())
                            {
                                String name = property
                                    .getName();
                                String value = property
                                    .getValue();
                                byte[] nameBytes = name
                                    .getBytes();
                                byte[] valueBytes = value
                                    .getBytes();
                                dout
                                    .writeInt(nameBytes.length);
                                dout.write(nameBytes);
                                dout
                                    .writeInt(valueBytes.length);
                                dout.write(valueBytes);
                            }
                            /*
                             * The message properties have been written. Now
                             * we'll copy the actual message data.
                             */
                            StringUtils.copy(in, dout);
                            in.close();
                            dout.flush();
                            dout.close();
                            message
                                .setStage(OutboundMessage.STAGE_ENCODED);
                            messagePlaintextFile.delete();
                            notifyOutboundEncrypter();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
                        try
                        {
                            /*
                             * We'll only proceed if we have all of the security
                             * keys for all of the recipients.
                             */
                            ArrayList<String> recipientUsers = new ArrayList<String>();
                            for (OutboundMessageRecipient recipient : message
                                .getRecipients().isolate())
                            {
                                if (!recipientUsers
                                    .contains(recipient
                                        .getUserid()))
                                    recipientUsers
                                        .add(recipient
                                            .getUserid());
                            }
                            Contact[] userContacts = new Contact[recipientUsers
                                .size()];
                            boolean hasAllContacts = true;
                            for (int i = 0; i < userContacts.length; i++)
                            {
                                userContacts[i] = storage
                                    .getContact(recipientUsers
                                        .get(i));
                                if (userContacts[i] == null)
                                {
                                    hasAllContacts = false;
                                    break;
                                }
                                if (!userContacts[i]
                                    .isHasKeys())
                                {
                                    hasAllContacts = false;
                                    break;
                                }
                            }
                            if (!hasAllContacts)
                                /*
                                 * This message doesn't have all of it's
                                 * contacts or contact security keys, so we'll
                                 * move on to the next message to send.
                                 */
                                continue;
                            /*
                             * If we get here then we have a contact object for
                             * each user in the list and the contacts all have
                             * security keys. Now we do the actual encrypting.
                             */
                            File messageEncoded = new File(
                                storage
                                    .getOutboundMessageEncodedStore(),
                                message.getFileId());
                            File messageEncrypted = new File(
                                storage
                                    .getOutboundMessageEncryptedStore(),
                                message.getFileId());
                            if (!messageEncoded.exists())
                            {
                                System.err
                                    .println("Encoded version of message "
                                        + message.getId()
                                        + " does not exist. The message will be "
                                        + "sent back one stage for "
                                        + "re-encoding.");
                                message
                                    .setStage(OutboundMessage.STAGE_INITIALIZED);
                                continue;
                            }
                            /*
                             * We have the encoded version of the message. Now
                             * we'll see if there's already an encrypted version
                             * of the message, and if there is, we'll delete it.
                             */
                            if (messageEncrypted.exists())
                                if (!messageEncrypted
                                    .delete())
                                    throw new RuntimeException(
                                        "failed delete");
                            messageEncrypted
                                .createNewFile();
                            FileOutputStream fout = new FileOutputStream(
                                messageEncrypted);
                            FileInputStream in = new FileInputStream(
                                messageEncoded);
                            DataOutputStream out = new DataOutputStream(
                                fout);
                            /*
                             * The first thing we need to do is generate a
                             * symmetric key.
                             */
                            byte[] aesKey = CertificateUtils
                                .generateSymmetricKey()
                                .getEncoded();
                            /*
                             * We have our key for encrypting our message. Now
                             * we write the number of user recipients. Then, we
                             * write each one, followed by the aes key encoded
                             * using that user's public encryption key.
                             */
                            out
                                .writeInt(userContacts.length);
                            for (Contact contact : userContacts)
                            {
                                out
                                    .writeInt(contact
                                        .getUserid()
                                        .getBytes().length);
                                out
                                    .write(contact
                                        .getUserid()
                                        .getBytes());
                                byte[] encryptedKeyBytes = RSA
                                    .encrypt(
                                        contact
                                            .getRsaEncPub(),
                                        contact
                                            .getRsaEncMod(),
                                        aesKey);
                                out
                                    .writeInt(encryptedKeyBytes.length);
                                out
                                    .write(encryptedKeyBytes);
                            }
                            /*
                             * We've written the encryption keys for all the
                             * contacts. Now we'll write the message signature.
                             */
                            byte[] hash = CertificateUtils
                                .hash(new FileInputStream(
                                    messageEncoded));
                            byte[] signature = RSA.encrypt(
                                localUser.getRsaSigPrv(),
                                localUser.getRsaSigMod(),
                                hash);
                            out.writeInt(signature.length);
                            out.write(signature);
                            /*
                             * We've written the signature now. All that's left
                             * is to encrypt the message.
                             */
                            out.flush();
                            Cipher cipher = Cipher
                                .getInstance("AES/CBC/PKCS7Padding");
                            cipher.init(
                                Cipher.ENCRYPT_MODE,
                                new SecretKeySpec(aesKey,
                                    "AES"),
                                new IvParameterSpec(
                                    new byte[16]));
                            CipherOutputStream cout = new CipherOutputStream(
                                out, cipher);
                            StringUtils.copy(in, cout);
                            cout.flush();
                            cout.close();
                            out.flush();
                            out.close();
                            /*
                             * The message has now been encrypted! We'll mark it
                             * as such in the database and send it on to the
                             * next stage.
                             */
                            message
                                .setStage(OutboundMessage.STAGE_ENCRYPTED);
                            messageEncoded.delete();
                            notifyOutboundUploader();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    
    private byte[] outboundUploaderBuffer = new byte[32768];
    
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
                        try
                        {
                            /*
                             * We need to see if the message exists. If it
                             * doesn't, we'll create it. Then we'll get the
                             * current size of the message from the server, skip
                             * that many bytes in the stream that we're
                             * uploading, and upload the rest in chunks of
                             * 32,768 bytes.
                             * 
                             * TODO: this chunk upload size should be
                             * configurable in the future, so that if it's too
                             * large (IE a user's network fails after they've
                             * transferred, say, 20KB), then they can shrink it.
                             * Or perhaps this class should adapt to that and if
                             * it keeps getting failures then it will scale it's
                             * chunk size down to 1KB or even 256B, until it
                             * keeps succeeding and then it will scale back up.
                             */
                            try
                            {
                                StoredMessage messageInfo = communicator
                                    .getMessageInfo(message
                                        .getId());
                                if (messageInfo != null
                                    && messageInfo.isSent())
                                {
                                    /*
                                     * The message has already been sent. This
                                     * isn't supposed to happen, so we'll alert
                                     * the user of the error.
                                     */
                                    System.err
                                        .println("message already exists");
                                    continue;
                                }
                                if (messageInfo == null)
                                    throw new FailedResponseException(
                                        "NOSUCHMESSAGE");
                            }
                            catch (FailedResponseException e)
                            {
                                if (e.getResponseCode()
                                    .equalsIgnoreCase(
                                        "NOSUCHMESSAGE"))
                                {
                                    communicator
                                        .createMessage(
                                            message.getId(),
                                            translateToServerRecipients(message
                                                .getRecipients()
                                                .isolate()));
                                }
                                else
                                {
                                    System.err
                                        .println("couldn't create message because: "
                                            + e
                                                .getResponseCode());
                                    continue;
                                }
                            }
                            /*
                             * At this point the message exists and has not yet
                             * been sent. Now we'll get the message's current
                             * size and begin uploading from that point.
                             */
                            File messageEncrypted = new File(
                                storage
                                    .getOutboundMessageEncryptedStore(),
                                message.getFileId());
                            if (!messageEncrypted.exists())
                            {
                                System.err
                                    .println("nonexistant encryption for message "
                                        + message.getId()
                                        + ", sending back to the encryptor");
                                message
                                    .setStage(OutboundMessage.STAGE_ENCODED);
                                continue;
                            }
                            FileInputStream in = new FileInputStream(
                                messageEncrypted);
                            int messageSize = communicator
                                .getMessageSize(message
                                    .getId());
                            long bytesToSkip = messageSize;
                            while (bytesToSkip > 0)
                                bytesToSkip -= in
                                    .skip(bytesToSkip);
                            /*
                             * We've skipped the stream to where we need to
                             * start uploading. Now we begin uploading actual
                             * data. The way we'll do this is by reading into an
                             * array of bytes from the file, and then uploading
                             * those. The buffer will only be 32,768 bytes in
                             * size.
                             */
                            int l;
                            int position = messageSize;
                            while ((l = in
                                .read(outboundUploaderBuffer)) != -1)
                            {
                                /*
                                 * We'll create a new buffer now to hold exactly
                                 * the number of bytes read.
                                 */
                                byte[] buffer = new byte[l];
                                System.arraycopy(
                                    outboundUploaderBuffer,
                                    0, buffer, 0, l);
                                /*
                                 * Now we'll write the data to the message.
                                 */
                                communicator
                                    .writeMessageData(
                                        message.getId(),
                                        position, l, buffer);
                                position += l;
                            }
                            /*
                             * The message data has now been uploaded. We'll
                             * send the message onto the next stage now.
                             */
                            message
                                .setStage(OutboundMessage.STAGE_UPLOADED);
                            messageEncrypted.delete();
                            notifyOutboundSender();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
                        try
                        {
                            try
                            {
                                communicator
                                    .sendMessage(message
                                        .getId());
                            }
                            catch (FailedResponseException e)
                            {
                                /*
                                 * Do nothing, the only feasible way for this to
                                 * happen is if the message has already been
                                 * sent
                                 */
                                System.out
                                    .println("sending message "
                                        + message.getId()
                                        + " appears to have already been done with code "
                                        + e
                                            .getResponseCode());
                            }
                            message
                                .setStage(OutboundMessage.STAGE_SENT);
                            localUser.getOutboundMessages()
                                .remove(message);
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    @StageThread
    private Thread inboundImporterThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = inboundImporterQueue
                        .poll(
                            (long) (delay + (delayVariance * Math
                                .random())),
                            TimeUnit.MILLISECONDS);
                    if (object == quitObject)
                        return;
                    /*
                     * Now we download a list of inbound messages from the
                     * server, and add those that aren't already present
                     * locally. If there are any messages at all (whether on the
                     * server or already local), we'll notify the inbound
                     * downloader. In the future, this should probably be
                     * changed to only notify the downloader if the message is
                     * in the IMPORTED state.
                     */
                    boolean wereMessagesImported = false;
                    String[] messageIds = communicator
                        .listInboundMessages();
                    System.out.println("listed "
                        + messageIds.length
                        + " inbound message(s)");
                    for (String messageId : messageIds)
                    {
                        InboundMessage inboundMessage = localUser
                            .getInboundMessageById(messageId);
                        if (inboundMessage != null)
                        {
                            /*
                             * The message is already present locally.
                             */
                            System.out
                                .println("server message already present locally");
                            continue;
                        }
                        /*
                         * The message is not present locally. We'll create it
                         * and add it to the user's list of inbound messages.
                         */
                        wereMessagesImported = true;
                        StoredMessage storedMessage = communicator
                            .getMessageInfo(messageId);
                        inboundMessage = localUser
                            .createInboundMessage();
                        inboundMessage.setId(messageId);
                        inboundMessage
                            .setSender(storedMessage
                                .getSender());
                        inboundMessage
                            .setSendingComputer(storedMessage
                                .getComputer());
                        inboundMessage
                            .setStage(InboundMessage.STAGE_IMPORTED);
                        System.out
                            .println("created inbound message");
                        /*
                         * The inbound message's target isn't known at this
                         * point (it only becomes known after decoding the
                         * message), so we won't set it here.
                         * 
                         * Now we add it to the list of stored messages and
                         * notify the downloader.
                         */
                        localUser.getInboundMessages().add(
                            inboundMessage);
                        System.out
                            .println("added inbound message to local user store");
                    }
                    if (wereMessagesImported)
                    {
                        /*
                         * Messages were imported. We should therefore notify
                         * the message downloader.
                         */
                        System.out
                            .println("messages were imported; notifying the downloader");
                        notifyInboundDownloader();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
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
                        try
                        {
                            String messageId = message
                                .getId();
                            /*
                             * First, we'll get the message's size. Then, we'll
                             * get the size of the local message file. If the
                             * local message file's size is greater than the
                             * size of the message on the server, then we'll
                             * just let it go for now (but we'll print an error
                             * message), although in the future we should warn
                             * the user that a message download has been
                             * corrupted, or delete the message file and start
                             * over again.
                             */
                            int messageSize = communicator
                                .getMessageSize(messageId);
                            File messageFile = new File(
                                storage
                                    .getInboundMessageEncryptedStore(),
                                URLEncoder.encode(message
                                    .getId()));
                            if (!messageFile.exists())
                                messageFile.createNewFile();
                            int localMessageSize = (int) messageFile
                                .length();
                            if (localMessageSize >= messageSize)
                            {
                                /*
                                 * The message has already been downloaded but
                                 * hasn't been sent to the next stage. We'll
                                 * send it to the localizer.
                                 */
                                message
                                    .setStage(InboundMessage.STAGE_DOWNLOADED);
                                notifyInboundLocalizer();
                                continue;
                            }
                            /*
                             * The message hasn't been fully downloaded. We'll
                             * start at the position indicated by the local
                             * message file's size, since that size means we
                             * already have that much of the message's data.
                             */
                            RandomAccessFile out = new RandomAccessFile(
                                messageFile, "rw");
                            int nextReadIndex = localMessageSize;
                            while (nextReadIndex < messageSize)
                            {
                                byte[] bytes = communicator
                                    .readMessageData(
                                        messageId,
                                        nextReadIndex,
                                        Math
                                            .min(
                                                32768,
                                                messageSize
                                                    - nextReadIndex));
                                out.seek(nextReadIndex);
                                out.write(bytes);
                                nextReadIndex += bytes.length;
                            }
                            /*
                             * The message has now been completely downloaded.
                             * We'll close the file and send the message onto
                             * the localizer.
                             */
                            out.close();
                            message
                                .setStage(InboundMessage.STAGE_DOWNLOADED);
                            notifyInboundLocalizer();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    /**
     * The name of this thread is somewhat misleading. Most developers would
     * interpret this to be some sort of language modifier or something, and I
     * kind of am thinking of renaming it because of this exact conflict. What
     * this thread actually does do is deletes the message off of the server. In
     * this sense, it makes the message local (because it's no longer remote, or
     * on the server), or it localizes the message.<br/> <br/>
     * 
     * If this ends up causing enough confusion, I'll probably rename it or
     * something.
     */
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
                        try
                        {
                            /*
                             * Essentially, all this stage does is deletes the
                             * server-side message, so we'll do that now. If we
                             * get a FailedResponseException indicating either
                             * UNAUTHORIZED or NOSUCHMESSAGE, then the job has
                             * already been done and we'll forward onto the
                             * decrypter. If the call is successful then we have
                             * just deleted the message, so we'll forward
                             * anyway.
                             */
                            try
                            {
                                communicator
                                    .deleteMessage(message
                                        .getId());
                            }
                            catch (FailedResponseException exception)
                            {
                                if (exception
                                    .getResponseCode()
                                    .equalsIgnoreCase(
                                        "UNAUTHORIZED")
                                    || exception
                                        .getResponseCode()
                                        .equalsIgnoreCase(
                                            "NOSUCHMESSAGE"))
                                {
                                    /*
                                     * The message has already been deleted, so
                                     * we won't do anything.
                                     */
                                }
                                else
                                {
                                    /*
                                     * An error happened while deleting the
                                     * message.
                                     */
                                    throw exception;
                                }
                            }
                            /*
                             * If we get here then either the message didn't
                             * exist or we deleted it, so we'll forward to the
                             * next stage, the decrypter.
                             */
                            message
                                .setStage(InboundMessage.STAGE_LOCALIZED);
                            notifyInboundDecrypter();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
                        try
                        {
                            File messageEncryptedFile = new File(
                                storage
                                    .getInboundMessageEncryptedStore(),
                                URLEncoder.encode(message
                                    .getId()));
                            File messageEncodedFile = new File(
                                storage
                                    .getInboundMessageEncodedStore(),
                                URLEncoder.encode(message
                                    .getId()));
                            /*
                             * We're essentially decrypting from
                             * messageEncryptedFile to messageEncodedFile.
                             * 
                             * The first thing we need to do is check that we
                             * have the security keys for the contact that sent
                             * the message. If we don't, we'll skip the message,
                             * since we need the contact's keys to be able to
                             * verify the message's signature.
                             * 
                             * If messageEncodedFile already exists, we'll just
                             * start over with the decryption by deleting it. If
                             * it can't be deleted, then we'll throw an
                             * exception. Anyway, if messageEncryptedFile
                             * doesn't exist, we'll similarly throw an
                             * exception. At that point, messageEncryptedFile
                             * does exist and messageEncodedFile doesn't or has
                             * a size of 0. Then we'll search the header until
                             * we find the encrypted key that corresponds to
                             * this username. We can then skip the rest of the
                             * header, up until the message signature. We'll
                             * decrypt the signature using the sender's public
                             * signing key (which we will have already
                             * obtained). We'll then store this hash value until
                             * after the actual message decryption. Then, we'll
                             * create a cipher output stream that will decrypt
                             * the message for us, and read bytes off of the
                             * message and into this output stream, which will
                             * be pointed at the message's encoded file. Once
                             * this is complete, we'll hash the contents of the
                             * encoded message and compare it to the hash found
                             * in the message's signature. If they match, we'll
                             * forward the message onto the decoding stage. If
                             * they don't, we'll delete the message (by marking
                             * it as read and unlinking it) and print an error
                             * out that indicates that the message had an
                             * invalid signature. In the future, we might want
                             * to have some means of notifying the user of this
                             * besides just printing it out to the console.
                             */
                            String messageSender = message
                                .getSender();
                            Contact senderContact = storage
                                .getContact(messageSender);
                            if (senderContact == null)
                            {
                                senderContact = localUser
                                    .createContact();
                                senderContact
                                    .setHasKeys(false);
                                senderContact
                                    .setLastModifled(0);
                                senderContact
                                    .setUserContact(false);
                                senderContact
                                    .setUserid(messageSender);
                                senderContact
                                    .setUserVerified(false);
                                localUser.getContacts()
                                    .add(senderContact);
                                if (localUser.getContext() != null)
                                {
                                    new Thread()
                                    {
                                        public void run()
                                        {
                                            localUser
                                                .getContext()
                                                .updateContactStatus();
                                        }
                                    }.start();
                                }
                            }
                            if (!senderContact.isHasKeys())
                            {
                                /*
                                 * We don't have this contact's keys yet. Since
                                 * there currently isn't a queue-based approach
                                 * for recurring user tasks like there is for
                                 * message management (so it's not
                                 * straightforward to instruct the user context
                                 * to download the user's keys), we'll just skip
                                 * over the message and wait for the contact
                                 * status thread to download the keys.
                                 */
                                continue;
                            }
                            /*
                             * We have the sender's keys if we get here. Now
                             * we'll parse the message to get the encrypted
                             * security key that corresponds to our private
                             * encryption key.
                             */
                            if (messageEncodedFile.length() > 0)
                                messageEncodedFile.delete();
                            if (messageEncodedFile.length() > 0)
                                throw new RuntimeException(
                                    "couldn't delete existing encoded form.");
                            if (!messageEncryptedFile
                                .exists())
                            {
                                System.err
                                    .println("input encrpted message "
                                        + message.getId()
                                        + " doesn't exist, the message will be deleted.");
                                message
                                    .setStage(InboundMessage.STAGE_READ);
                                localUser
                                    .getInboundMessages()
                                    .remove(message);
                                continue;
                            }
                            FileInputStream fileIn = new FileInputStream(
                                messageEncryptedFile);
                            DataInputStream in = new DataInputStream(
                                fileIn);
                            int recipientCount = in
                                .readInt();
                            byte[] messageKey = null;
                            for (int i = 0; i < recipientCount; i++)
                            {
                                int recipientUseridSize = in
                                    .readInt();
                                byte[] recipientUseridBytes = new byte[recipientUseridSize];
                                in
                                    .readFully(recipientUseridBytes);
                                String recipientUserid = new String(
                                    recipientUseridBytes);
                                int encryptedKeySize = in
                                    .readInt();
                                byte[] encryptedKeyBytes = new byte[encryptedKeySize];
                                in
                                    .readFully(encryptedKeyBytes);
                                if (recipientUserid
                                    .equalsIgnoreCase(userid))
                                {
                                    /*
                                     * The key is encrypted with our encryption
                                     * key, so we'll use our private key to
                                     * decrypt it
                                     */
                                    messageKey = RSA
                                        .decrypt(
                                            localUser
                                                .getRsaEncPrv(),
                                            localUser
                                                .getRasEncMod(),
                                            encryptedKeyBytes);
                                }
                                
                            }
                            if (messageKey == null)
                            {
                                System.err
                                    .println("Message "
                                        + message.getId()
                                        + " does not contain a valid public-key "
                                        + "encryption, and will be deleted.");
                                message
                                    .setStage(InboundMessage.STAGE_READ);
                                localUser
                                    .getInboundMessages()
                                    .remove(message);
                            }
                            /*
                             * We have the message key at this point, and we've
                             * skipped over all of the other keys. Now we'll
                             * read in the message's signature.
                             */
                            byte[] signature = new byte[in
                                .readInt()];
                            in.readFully(signature);
                            byte[] receivedHash = RSA
                                .decrypt(senderContact
                                    .getRsaSigPub(),
                                    senderContact
                                        .getRsaSigMod(),
                                    signature);
                            /*
                             * After decryption, receivedHash should hold the
                             * same value as the hash we'll compute on the
                             * message data. Now we'll decrypt the message.
                             */
                            FileOutputStream fileOut = new FileOutputStream(
                                messageEncodedFile);
                            Cipher cipher = Cipher
                                .getInstance("AES/CBC/PKCS7Padding");
                            cipher.init(
                                Cipher.DECRYPT_MODE,
                                new SecretKeySpec(
                                    messageKey, "AES"),
                                new IvParameterSpec(
                                    new byte[16]));
                            CipherOutputStream out = new CipherOutputStream(
                                fileOut, cipher);
                            StringUtils.copy(in, out);
                            out.flush();
                            out.close();
                            /*
                             * We've decrypted the message. Now we'll verify
                             * it's signature, and if the signature is
                             * incorrect, we'll remove the message and print an
                             * error.
                             */
                            byte[] hash = CertificateUtils
                                .hash(new FileInputStream(
                                    messageEncodedFile));
                            if (!Arrays.equals(hash,
                                receivedHash))
                            {
                                System.err
                                    .println("message "
                                        + message.getId()
                                        + " has an invalid signature");
                                message
                                    .setStage(InboundMessage.STAGE_READ);
                                localUser
                                    .getInboundMessages()
                                    .remove(message);
                                messageEncryptedFile
                                    .delete();
                                messageEncodedFile.delete();
                                continue;
                            }
                            /*
                             * The message matches the signature. We'll send it
                             * on to the decoder.
                             */
                            message
                                .setStage(InboundMessage.STAGE_DECRYPTED);
                            messageEncryptedFile.delete();
                            notifyInboundDecoder();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    @StageThread
    private Thread inboundDecoderThread = new Thread(
        "mm-inbound-decoder")
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
                        try
                        {
                            File messageEncoded = new File(
                                storage
                                    .getInboundMessageEncodedStore(),
                                URLEncoder.encode(message
                                    .getId()));
                            File messagePlaintext = new File(
                                storage
                                    .getInboundMessagePlaintextStore(),
                                URLEncoder.encode(message
                                    .getId()));
                            if (messagePlaintext.exists())
                                if (!messagePlaintext
                                    .delete())
                                    throw new RuntimeException(
                                        "couldn't delete message plaintext form");
                            if (!messageEncoded.exists())
                            {
                                System.err
                                    .println("message "
                                        + message.getId()
                                        + " doesn't have an encoded form, sending back to the decrypter");
                                message
                                    .setStage(InboundMessage.STAGE_LOCALIZED);
                            }
                            /*
                             * The message exists and it's decoded form doesn't.
                             * Now we'll begin the actual decoding.
                             * 
                             * First up is the target path.
                             */
                            FileInputStream fileIn = new FileInputStream(
                                messageEncoded);
                            DataInputStream in = new DataInputStream(
                                fileIn);
                            FileOutputStream out = new FileOutputStream(
                                messagePlaintext);
                            byte[] targetPathBytes = new byte[in
                                .readInt()];
                            in.readFully(targetPathBytes);
                            message.setTarget(new String(
                                targetPathBytes));
                            /*
                             * Next we'll read in the message's parameters.
                             */
                            message.getProperties().clear();
                            int messagePropertyCount = in
                                .readInt();
                            for (int i = 0; i < messagePropertyCount; i++)
                            {
                                byte[] nameBytes = new byte[in
                                    .readInt()];
                                in.readFully(nameBytes);
                                byte[] valueBytes = new byte[in
                                    .readInt()];
                                in.readFully(valueBytes);
                                MessageProperty property = message
                                    .createProperty();
                                property
                                    .setName(new String(
                                        nameBytes));
                                property
                                    .setValue(new String(
                                        valueBytes));
                                message.getProperties()
                                    .add(property);
                            }
                            /*
                             * The rest of the data is the actual contents of
                             * the message, so we'll just perform a straight
                             * copy.
                             */
                            StringUtils.copy(in, out);
                            out.flush();
                            out.close();
                            in.close();
                            /*
                             * The message has nwo been decoded. We'll forward
                             * it on to the dispatcher.
                             */
                            message
                                .setStage(InboundMessage.STAGE_DECODED);
                            messageEncoded.delete();
                            System.out
                                .println("notifying inbound dispatcher upon available message");
                            notifyInboundDispatcher();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    @StageThread
    private Thread inboundDispatcherThread = new Thread(
        "mm-inbound-dispatcher")
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
                    System.out
                        .println("run object detected in dispatcher thread");
                    /*
                     * The object is the runObject or the timeout expired, so
                     * we'll do the actual processing.
                     */
                    /*
                     * Do actual processing here
                     */
                    System.out
                        .println("running dispatch thread");
                    InboundMessage[] messages = localUser
                        .listInboundMessagesForStage(InboundMessage.STAGE_DECODED);
                    for (InboundMessage message : messages)
                    {
                        try
                        {
                            /*
                             * The dispatcher thread is a relatively simple
                             * thread: it's job is to send the message to the
                             * message hierarchy. Unlike the other stages,
                             * however, this one marks the message as dispatched
                             * before it even dispatches it. The reason for this
                             * is that only messages that are in the dispatched
                             * stage will be returned from methods on the
                             * hierarchy that list messages, and it doesn't
                             * really matter if we don't dispatch the message if
                             * an error occurs, since the messsage hierarchy
                             * implementation should list all messages upon
                             * program startup to check for messages it needs to
                             * process anyway.
                             */
                            System.out
                                .println("dispatching message");
                            message
                                .setStage(InboundMessage.STAGE_DISPATCHED);
                            hierarchyElement
                                .injectMessage(message);
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    @StageThread
    private Thread inboundRemoverThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Object object = inboundRemoverQueue
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
                        .listInboundMessagesForStage(InboundMessage.STAGE_READ);
                    for (InboundMessage message : messages)
                    {
                        try
                        {
                            File messagePlaintextFile = new File(
                                storage
                                    .getInboundMessagePlaintextStore(),
                                URLEncoder.encode(message
                                    .getId()));
                            messagePlaintextFile.delete();
                            localUser.getInboundMessages()
                                .remove(message);
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
                    }
                    /*
                     * We've deleted all of the messages in the "read" stage.
                     * Next, we'll delete files for messages that don't exist at
                     * all. We'll combine the files for all three stage folders
                     * for each state (outbound vs inbound) together, since
                     * we're just checking to make sure that the message exists
                     * here, not that the stage is correct.
                     * 
                     * We'll check inbound messages first, and then outbound
                     * messages.
                     */
                    File[] existenceCheckInboundFiles = DataUtils
                        .concat(
                            File.class,
                            storage
                                .getInboundMessageEncryptedStore()
                                .listFiles(),
                            storage
                                .getInboundMessageEncodedStore()
                                .listFiles(),
                            storage
                                .getInboundMessagePlaintextStore()
                                .listFiles());
                    for (File file : existenceCheckInboundFiles)
                    {
                        Object message = localUser
                            .getInboundMessageById(URLDecoder
                                .decode(file.getName()));
                        if (message == null)
                            file.delete();
                    }
                    File[] existenceCheckOutboundFiles = DataUtils
                        .concat(
                            File.class,
                            storage
                                .getOutboundMessageEncryptedStore()
                                .listFiles(),
                            storage
                                .getOutboundMessageEncodedStore()
                                .listFiles(),
                            storage
                                .getOutboundMessagePlaintextStore()
                                .listFiles());
                    for (File file : existenceCheckOutboundFiles)
                    {
                        Object message = localUser
                            .getOutboundMessageById(URLDecoder
                                .decode(file.getName()));
                        if (message == null)
                            file.delete();
                    }
                    /*
                     * We've checked all of the message files. Now we'll check
                     * each stage. We'll only delete a file for a particular
                     * stage if the message has progressed beyond that stage; if
                     * the message is at a stage before the one in question, we
                     * won't delete the file.
                     */
                    /*
                     * First up, we have the outbound plaintext stage. The last
                     * stage at which the plaintext is used is
                     * STAGE_INITIALIZED, so if the stage is greater than that,
                     * we'll delete the message.
                     * 
                     * In these loops, if the message isn't present, then we'll
                     * assume that it has been deleted between these loops and
                     * the one above this comment. We won't bother to add the
                     * additional logic to delete the message; we'll just let
                     * the next pass of this thread delete it.
                     */
                    for (File file : storage
                        .getOutboundMessagePlaintextStore()
                        .listFiles())
                    {
                        OutboundMessage message = localUser
                            .getOutboundMessageById(URLDecoder
                                .decode(file.getName()));
                        if (message == null)
                            continue;
                        if (message.getStage() > OutboundMessage.STAGE_INITIALIZED)
                            file.delete();
                    }
                    /*
                     * Next up is the encoded message form, with STAGE_ENCODED
                     * being the last stage to use this form.
                     */
                    for (File file : storage
                        .getOutboundMessageEncodedStore()
                        .listFiles())
                    {
                        OutboundMessage message = localUser
                            .getOutboundMessageById(URLDecoder
                                .decode(file.getName()));
                        if (message == null)
                            continue;
                        if (message.getStage() > OutboundMessage.STAGE_ENCODED)
                            file.delete();
                    }
                    /*
                     * Last, but not least, we have the encrypted message form.
                     * The last stage to use this form is STAGE_ENCRYPTED.
                     */
                    for (File file : storage
                        .getOutboundMessageEncryptedStore()
                        .listFiles())
                    {
                        OutboundMessage message = localUser
                            .getOutboundMessageById(URLDecoder
                                .decode(file.getName()));
                        if (message == null)
                            continue;
                        if (message.getStage() > OutboundMessage.STAGE_ENCRYPTED)
                            file.delete();
                    }
                    /*
                     * Before we continue, we'll check for outbound messages in
                     * the SENT stage, and delete them.
                     */
                    for (OutboundMessage message : localUser
                        .listOutboundMessagesForStage(OutboundMessage.STAGE_SENT))
                    {
                        localUser.getOutboundMessages()
                            .remove(message);
                    }
                    /*
                     * Now we'll begin checking the inbound message stores.
                     * 
                     * First up is the encrypted store. The last stage to use
                     * this is STAGE_LOCALIZED.
                     */
                    for (File file : storage
                        .getInboundMessageEncryptedStore()
                        .listFiles())
                    {
                        InboundMessage message = localUser
                            .getInboundMessageById(URLDecoder
                                .decode(file.getName()));
                        if (message == null)
                            continue;
                        if (message.getStage() > InboundMessage.STAGE_LOCALIZED)
                            file.delete();
                    }
                    /*
                     * Last, we have the encoded store. The last stage to use
                     * this is STAGE_DECRYPTED.
                     * 
                     * We won't bother doing anything with the plaintext store,
                     * since the files in there are needed until the message has
                     * been marked as sent, at which point is will be deleted
                     * anyway and it's corresponding file deleted as well.
                     */
                    for (File file : storage
                        .getInboundMessageEncodedStore()
                        .listFiles())
                    {
                        InboundMessage message = localUser
                            .getInboundMessageById(URLDecoder
                                .decode(file.getName()));
                        if (message == null)
                            continue;
                        if (message.getStage() > InboundMessage.STAGE_DECRYPTED)
                            file.delete();
                    }
                    /*
                     * That's it for our work!
                     */
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
    @StageQueue
    private BlockingQueue<Object> inboundRemoverQueue = new ArrayBlockingQueue<Object>(
        1);
    
    // stage notifier methods
    public void notifyOutboundEncoder()
    {
        outboundEncoderQueue.offer(runObject);
    }
    
    protected StoredMessageRecipient[] translateToServerRecipients(
        ArrayList<OutboundMessageRecipient> outboundRecipients)
    {
        StoredMessageRecipient[] recipients = new StoredMessageRecipient[outboundRecipients
            .size()];
        System.out.println("translating "
            + recipients.length + " recipients");
        for (int i = 0; i < recipients.length; i++)
        {
            recipients[i] = new StoredMessageRecipient();
            recipients[i].setUserid(outboundRecipients.get(
                i).getUserid());
            recipients[i].setComputer(outboundRecipients
                .get(i).getComputer());
        }
        return recipients;
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
        System.out
            .println("notification of inbound dispatcher: "
                + inboundDispatcherQueue.offer(runObject));
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
        hierarchyElement.setReceiver(this);
    }
    
    public OutboundMessage createMessage()
    {
        OutboundMessage message = localUser
            .createOutboundMessage();
        message.setId(userid + "-"
            + Storage.createIdentifier());
        return message;
    }
    
    public synchronized void sendMessage(
        OutboundMessage message)
    {
        /*
         * All we have to do is add the message to the local user's list of
         * messages (if it's not already present), and notify the message
         * encoder that it has messages available.
         */
        if (localUser.getOutboundMessages().isolate()
            .contains(message))
            return;
        message.setStage(OutboundMessage.STAGE_INITIALIZED);
        localUser.getOutboundMessages().add(message);
        notifyOutboundEncoder();
    }
    
    public void start()
    {
        /*
         * TODO: scan for message files without a backing message in that stage
         * and delete them. Only do this when the manager starts to avoid
         * clobbering files for messages that are about to be advanced to that
         * stage.
         */
        /*
         * Start all of the threads running, and scan for messages to delete.
         * Then feed all queues the runObject so that they will perform an
         * initial pass.
         */
        Thread[] threads = getStageThreads();
        System.out.println("starting " + threads.length
            + " stage threads");
        for (Thread thread : threads)
        {
            thread.start();
        }
        BlockingQueue[] queues = getStageQueues();
        for (BlockingQueue queue : queues)
        {
            queue.offer(runObject);
        }
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
            .getInboundMessagePlaintextStore(), URLEncoder
            .encode(messageId));
    }
    
    public File getOutboundMessageFile(String messageId)
    {
        return new File(storage
            .getOutboundMessagePlaintextStore(), URLEncoder
            .encode(messageId));
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
    
    public void notifyAllThreads()
    {
        for (BlockingQueue queue : getStageQueues())
        {
            queue.offer(runObject);
        }
    }
    
    public InboundMessage[] listChildMessages(
        String floatingPath)
    {
        if (!floatingPath.endsWith("/"))
            floatingPath += "/";
        floatingPath += "*";
        return localUser
            .getInboundMessagesByFloatingTarget(floatingPath);
    }
    
    public InboundMessage[] listMessages(String fixedPath)
    {
        return localUser
            .getInboundMessagesByFixedTarget(fixedPath);
    }
    
}
