package net.sf.opengroove.client.messaging;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
import net.sf.opengroove.common.concurrent.ConditionalTimer;
import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.security.RSA;
import net.sf.opengroove.common.utils.StringUtils;

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
                    for (OutboundMessage message2 : messages)
                    {
                        try
                        {
                            OutboundMessage message = message2;
                            File messagePlaintextFile = new File(
                                storage
                                    .getOutboundMessagePlaintextStore(),
                                message.getId());
                            File messageEncodedFile = new File(
                                storage
                                    .getOutboundMessageEncodedStore(),
                                message.getId());
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
                                BigInteger keyInteger = new BigInteger(
                                    aesKey);
                                BigInteger encryptedKeyInteger = RSA
                                    .encrypt(
                                        contact
                                            .getRsaEncPub(),
                                        contact
                                            .getRsaEncMod(),
                                        keyInteger);
                                byte[] encryptedKeyBytes = encryptedKeyInteger
                                    .toByteArray();
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
                            BigInteger hashInt = new BigInteger(
                                hash);
                            BigInteger signatureInt = RSA
                                .encrypt(localUser
                                    .getRsaSigPrv(),
                                    localUser
                                        .getRsaSigMod(),
                                    hashInt);
                            byte[] signature = signatureInt
                                .toByteArray();
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
                                    new byte[8]));
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
                             * chunk size down to even 1KB, until it keeps
                             * succeeding and then it will scale back up.
                             */
                            try
                            {
                                StoredMessage messageInfo = communicator
                                    .getMessageInfo(message
                                        .getId());
                                if (messageInfo.isSent())
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
                    String[] messageIds = communicator
                        .listInboundMessages();
                    boolean wereMessages = false;
                    for (String messageId : messageIds)
                    {
                        InboundMessage inboundMessage = localUser
                            .getInboundMessageById(messageId);
                        if (inboundMessage != null)
                            /*
                             * The message is already present locally.
                             */
                            continue;
                        /*
                         * The message is not present locally. We'll create it
                         * and add it to the user's list of inbound messages.
                         */
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
                        /*
                         * The inbound message's target isn't known at this
                         * point (it only becomes known after decoding the
                         * message), so we won't set it here.
                         * 
                         * Now we add it to the list of stored messages and
                         * notify the downloader.
                         */
                        localUser.getInboundMessages().add(inboundMessage);
                    }
                }
                catch (Exception e)
                {
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
                            
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
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
                            
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
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
                            
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
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
                        try
                        {
                            
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
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
                        try
                        {
                            
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        
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
    
    protected StoredMessageRecipient[] translateToServerRecipients(
        ArrayList<OutboundMessageRecipient> isolate)
    {
        // TODO Auto-generated method stub
        return null;
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
