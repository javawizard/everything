package net.sf.opengroove.client.storage;

import java.awt.FlowLayout;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.sf.opengroove.common.proxystorage.ProxyStorage;
import net.sf.opengroove.common.security.Hash;
import net.sf.opengroove.common.utils.DataUtils;
import net.sf.opengroove.common.utils.Userids;

import base64.Base64Coder;

/**
 * This class is used for most access to persistant data.
 * 
 * @author Alexander Boyd
 * 
 */
public class Storage
{
    private static File base;
    
    private static File logFolder;
    
    private static DataStore dataStore;
    
    private static ProxyStorage<DataStore> proxyStorage;
    /**
     * Generally only used for debugging. If this is true, a new frame will be
     * opened that will refresh itself once per second with the current proxy
     * storage opcount.
     */
    private static final boolean showDebugProxyOpFrame = false;
    
    /**
     * Initializes the Storage class. This should only be called once per JVM
     * instance. This method may take a long time to return, since it creates a
     * ProxyStorage instance to use as the backing data store, and ProxyStorage
     * vacuums itself when first constructed (proxystorage vacuuming uses a
     * mark-and-sweep algorithm, which is inherently slow for a large data set).
     * 
     * @param file
     *            The file that the storage class should use to store all of
     *            it's data
     */
    public static void initStorage(File file)
    {
        if (base != null)
            throw new RuntimeException(
                "Storage is already initialized");
        base = file;
        logFolder = new File(base, "logs");
        if (!logFolder.exists())
            logFolder.mkdirs();
        proxyStorage = new ProxyStorage<DataStore>(
            DataStore.class, new File(base, "proxystorage"));
        if (showDebugProxyOpFrame)
        {
            JFrame frame = new JFrame(
                "ProxyStorage opcounts");
            frame.setSize(300, 150);
            final JLabel opcountLabel = new JLabel(""
                + proxyStorage.getOpcount());
            frame.getContentPane().setLayout(
                new FlowLayout());
            frame.getContentPane().add(opcountLabel);
            new Thread()
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            opcountLabel
                                .setText(""
                                    + proxyStorage
                                        .getOpcount());
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                    }
                }
            }.start();
            frame.setLocationRelativeTo(null);
            frame.show();
        }
        dataStore = proxyStorage.getRoot();
    }
    
    private LocalUser user;
    
    /**
     * Creates a storage object for the specified user id. The Storage class
     * maintains singleton instances of storage instances which are lazily
     * initialized.
     * 
     * @param userid
     *            the userid of the user to create the storage object for.
     *            Userids are of the form realm:username
     */
    protected Storage(String userid)
    {
        this.user = dataStore.getUser(userid);
        File tbase = new File(
            new File(base, "userspecific"), URLEncoder
                .encode(userid));
        if (!tbase.exists())
            tbase.mkdirs();
        pluginStore = iItem(tbase, "plugins");
        helpStore = iItem(tbase, "help");
        inboundMessageStore = iItem(tbase,
            "inboundmessages");
        outboundMessageStore = iItem(tbase,
            "outboundmessages");
        inboundMessagePlaintextStore = iItem(
            inboundMessageStore, "plaintext");
        inboundMessageEncodedStore = iItem(
            inboundMessageStore, "encoded");
        inboundMessageEncryptedStore = iItem(
            inboundMessageStore, "encrypted");
        outboundMessagePlaintextStore = iItem(
            outboundMessageStore, "plaintext");
        outboundMessageEncodedStore = iItem(
            outboundMessageStore, "encoded");
        outboundMessageEncryptedStore = iItem(
            outboundMessageStore, "encrypted");
        messageAttachmentStore = iItem(tbase,
            "messageattachments");
        ArrayList<UserMessage> messagesList = getLocalUser()
            .getUserMessages().isolate();
        HashSet<File> messageFileSet = new HashSet<File>();
        for (UserMessage message : messagesList)
        {
            messageFileSet
                .add(getMessageAttachmentFolder(message
                    .getId()));
        }
        File[] messageAttachmentFolders = messageAttachmentStore
            .listFiles();
        for (File f : messageAttachmentFolders)
        {
            if (!messageFileSet.contains(f))
            {
                DataUtils.recursiveDelete(f);
            }
        }
        for (UserMessage message : messagesList)
        {
            File[] attachmentFiles = getMessageAttachmentFolder(
                message.getId()).listFiles();
            HashSet<File> attachmentFileSet = new HashSet<File>();
            for (UserMessageAttachment attachment : message
                .getAttachments().isolate())
            {
                attachmentFileSet
                    .add(getMessageAttachmentFile(message
                        .getId(), attachment.getName()));
            }
            if (attachmentFiles != null)
            {
                for (File f : attachmentFiles)
                {
                    if (!attachmentFileSet.contains(f))
                        f.delete();
                }
            }
        }
    }
    
    private static final Hashtable<String, Storage> singletons = new Hashtable<String, Storage>();
    
    /**
     * Gets the singleton storage object for the specified user. Storage objects
     * are lazily initialized.
     * 
     * @param realm
     *            The realm of the user
     * @param username
     *            The user's username
     * @return A storage object for the user specified
     */
    public static synchronized Storage get(String userid)
    {
        Storage storage = singletons.get(userid);
        if (storage == null)
        {
            storage = new Storage(userid);
            singletons.put(userid, storage);
        }
        return storage;
    }
    
    private static File iItem(File tbase, String itemname)
    {
        File file = new File(tbase, itemname);
        if (!file.exists())
            file.mkdirs();
        return file;
    }
    
    /**
     * This folder contains the plugin jar files representing the plugins that
     * the user has installed, with each file bearing for it's name the id of
     * the plugin, and with no file extension, similar to the rest of the
     * storage files.
     */
    private File pluginStore;
    
    private File helpStore;
    
    private File inboundMessageStore;
    
    private File outboundMessageStore;
    
    private File inboundMessagePlaintextStore;
    
    private File inboundMessageEncodedStore;
    
    private File inboundMessageEncryptedStore;
    
    private File outboundMessagePlaintextStore;
    
    private File outboundMessageEncodedStore;
    
    private File outboundMessageEncryptedStore;
    /**
     * The message attachment store. For every user message on this computer,
     * there is a folder under here, who's name is the message's id, but
     * url-encoded, which has one file under it for each attachment present on
     * the message.
     */
    private File messageAttachmentStore;
    
    public File getMessageAttachmentStore()
    {
        return messageAttachmentStore;
    }
    
    /**
     * Gets the folder that stores message attachments for the specified message
     * id. The folder will be created if it doesn't exist.
     * 
     * @param messageId
     * @return
     */
    public File getMessageAttachmentFolder(String messageId)
    {
        File folder = new File(getMessageAttachmentStore(),
            URLEncoder.encode(messageId));
        if (!folder.exists())
            folder.mkdirs();
        if (!folder.isDirectory())
            throw new RuntimeException(
                "folder already exists as a file, "
                    + messageId);
        return folder;
    }
    
    /**
     * Returns a file where the specified attachment can be stored for the
     * specified message. If the folder for storing the file does not exist, it
     * is created. The file itself, however, is not created if it does not
     * exist.
     * 
     * @param messageId
     * @param attachmentName
     * @return
     */
    public File getMessageAttachmentFile(String messageId,
        String attachmentName)
    {
        return new File(
            getMessageAttachmentFolder(messageId),
            URLEncoder.encode(attachmentName));
    }
    
    public File getHelpStore()
    {
        return helpStore;
    }
    
    /**
     * Gets the list of users.
     * 
     * @return The list of users.
     */
    public static LocalUser[] getUsers()
    {
        return dataStore.getUsers().toArray(
            new LocalUser[0]);
    }
    
    public static LocalUser[] getUsersLoggedIn()
    {
        LocalUser[] users = getUsers();
        ArrayList<LocalUser> result = new ArrayList<LocalUser>();
        for (LocalUser user : users)
        {
            if (user.isLoggedIn())
                result.add(user);
        }
        return result.toArray(new LocalUser[0]);
    }
    
    public static LocalUser[] getUsersNotLoggedIn()
    {
        LocalUser[] users = getUsers();
        ArrayList<LocalUser> result = new ArrayList<LocalUser>();
        for (LocalUser user : users)
        {
            if (!user.isLoggedIn())
                result.add(user);
        }
        return result.toArray(new LocalUser[0]);
    }
    
    /**
     * Creates a new local user. The user must be added before it will appear in
     * the list of local users.
     * 
     * @return
     */
    public LocalUser createUser()
    {
        return dataStore.createUser();
    }
    
    public static DataStore getStore()
    {
        return dataStore;
    }
    
    /**
     * Shorthand for <code>getLocalUser().getContact(id)</code>.
     * 
     * @param id
     * @return
     */
    public Contact getContact(String id)
    {
        return user.getContact(id);
    }
    
    public static void addUser(LocalUser user)
    {
        dataStore.getUsers().add(user);
    }
    
    /**
     * Gets the user object for the user that this Storage instance is for.
     * 
     * @return
     */
    public LocalUser getLocalUser()
    {
        return user;
    }
    
    /**
     * reads the file specified in to a string. the file must not be larger than
     * 5 MB.
     * 
     * @param file.
     * @return
     */
    public static String readFile(File file)
    {
        try
        {
            if (file.length() > (5 * 1000 * 1000))
                throw new RuntimeException(
                    "the file is "
                        + file.length()
                        + " bytes. that is too large. it can't be larger than 5000000 bytes.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            copy(fis, baos);
            fis.close();
            baos.flush();
            baos.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Writes the string specified to the file specified.
     * 
     * @param string
     *            A string to write
     * @param file
     *            The file to write <code>string</code> to
     */
    public static void writeFile(String string, File file)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(
                string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(
                file);
            copy(bais, fos);
            bais.close();
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Copies the contents of one stream to another. Bytes from the source
     * stream are read until it is empty, and written to the destination stream.
     * Neither the source nor the destination streams are flushed or closed.
     * 
     * @param in
     *            The source stream
     * @param out
     *            The destination stream
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void copy(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
    }
    
    /**
     * Returns the local user for the realm and user specified.
     * 
     * @param realm
     *            The realm of the user to look up
     * @param user
     *            The username of the user to look up
     * @return The user's information
     */
    public static LocalUser getLocalUser(String userid)
    {
        return dataStore.getUser(userid);
    }
    
    /**
     * Checks to make sure that the specified user's password matches the
     * specified password. This can be used to authenticate a user.
     * 
     * @param realm
     *            the user's realm
     * @param username
     *            the user's username
     * @param pass
     *            the password that the user entered
     * @return <code>true</code> if the password entered matches the password
     *         stored to the file system, false otherwise.
     */
    public static boolean checkPassword(String userid,
        String pass)
    {
        LocalUser user = getLocalUser(userid);
        return user.getEncPassword()
            .equals(Hash.hash(pass));
    }
    
    /**
     * If the specified value is a userid, returns it. If it is a username,
     * returns a userid made up of the realm of this storage instance's user and
     * the username specified.
     * 
     * @param useridOrUsername
     * @return
     */
    private String resolve(String useridOrUsername)
    {
        return Userids.resolveTo(useridOrUsername, user
            .getUserid());
    }
    
    /**
     * Writes the specified object to the specified file, using the class
     * {@link java.io.ObjectOutputStream}
     * 
     * @param object
     *            The object to write
     * @param file
     *            The file to write the object to
     */
    private static void writeObjectToFile(
        Serializable object, File file)
    {
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(file));
            oos.writeObject(object);
            oos.flush();
            oos.close();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Reads an object from the file specified.
     * 
     * @param file
     *            The file to read an object from
     * @return An object, read from the file specified. Only the first object is
     *         read, so if the file contains multiple objects, they will not be
     *         returned.
     */
    private static Serializable readObjectFromFile(File file)
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file));
            Object object = ois.readObject();
            ois.close();
            return (Serializable) object;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(
                "Error while reading "
                    + file.getAbsolutePath(), ex);
        }
    }
    
    /**
     * Recursively deletes a folder. This method first iterates over the
     * folder's contents, deleting each file and folder within that folder,
     * recursively. Then, it deletes the folder passed in. If a particular file
     * or folder couldn't be deleted, a RuntimeException is thrown. If this
     * happens, then some of the tree may still have been deleted.
     * 
     * @param transmissionFolder
     */
    public static void recursiveDelete(
        File transmissionFolder)
    {
        if (transmissionFolder.isDirectory())
        {
            for (File file : transmissionFolder.listFiles())
            {
                recursiveDelete(file);
            }
        }
        if (!transmissionFolder.delete())
            throw new RuntimeException(
                "Couldn't delete the file "
                    + transmissionFolder.getAbsolutePath());
    }
    
    private static AtomicInteger cIdVar = new AtomicInteger(
        1);
    
    public static synchronized String createIdentifier()
    {
        return "i" + System.currentTimeMillis() + "z"
            + cIdVar.getAndIncrement();
    }
    
    /**
     * lists all unread messages, messages that are in mInCache.
     * 
     * @return
     */
    /*
     * public static synchronized InstantMessage[] listUnreadMessages() { return
     * listObjectContentsAsArray(mInCache, InstantMessage.class); }
     */
    /**
     * lists all messages in the archive. these are both incoming and outgoing
     * messages. it is best to trim the archive when it gets excessively large.
     * the messages are listed in chronological order, oldest first. you may
     * want to reverse the ordering (see Collections.reverse()) of the messages,
     * to show newest first, before presenting the archive to the end user.
     * 
     * @return
     */
    /*
     * public static synchronized InstantMessage[] listArchivedMessages() {
     * return listObjectContentsAsArray(mArchive, InstantMessage.class); }
     */
    /**
     * marks this message as read, in otherwords, moves this message from
     * mInCache to mArchive.
     * 
     * @param messageId
     */
    /*
     * public static synchronized void markRead(String messageId) { if (!new
     * File(mInCache, messageId).renameTo(new File(mArchive, messageId))) throw
     * new RuntimeException("could not move message " + messageId + " to the
     * message archive."); }
     */
    /**
     * deletes the message. the message must be in mArchive, in otherwords, if
     * it is an outgoing message, it must already have been sent, and if it is
     * an incoming message, it must already have been marked read.
     * 
     * @param messageId
     */
    /*
     * public static synchronized void deleteMessage(String messageId) {
     * InstantMessage message = (InstantMessage) readObjectFromFile(new File(
     * mArchive, messageId)); if (message.isHasAttachments()) {
     * recursiveDelete(new File(mAttachments, message
     * .getAttachmentSetIdentifier())); } recursiveDelete(new File(mArchive,
     * messageId)); }
     * 
     * public static synchronized InstantMessage[] listOutgoingMessages() {
     * return listObjectContentsAsArray(mOutCache, InstantMessage.class); }
     * 
     * public static synchronized void markSent(String messageId) { if (!new
     * File(mOutCache, messageId).renameTo(new File(mArchive, messageId))) throw
     * new RuntimeException("could not move message " + messageId + " to the
     * message archive."); }
     */
    /**
     * returns the real storage locations of the attachments on this message.
     * NOTE that the filenames ARE NOT the actual names of the attachments. the
     * filenames are the names of the attachments, prefixed with a "d" if the
     * file is a zip file and was originally sent as a directory, or "f" if it
     * is a regular attachment file.
     * 
     * @param attachmentSetIdentifier
     * @return
     */
    /*
     * public static synchronized File[] listAttachmentStorageFiles( String
     * attachmentSetIdentifier) { return new File(mAttachments,
     * attachmentSetIdentifier).listFiles(); }
     */
    public static <T> HashMap<String, T> listObjectContents(
        File folder, Class<T> c)
    {
        HashMap<String, T> map = new HashMap<String, T>();
        for (File file : folder
            .listFiles(new SubversionFileFilter()))
        {
            if (file.isFile())
                map.put(file.getName(),
                    (T) readObjectFromFile(file));
        }
        return map;
    }
    
    /**
     * lists the contents of the specified folder. first, all files of this
     * folder are listed. then, an array is created, that is the same length as
     * those files. the array element type is the class specified in this
     * method. then, for each file, the file is deserialized (the contents of
     * the file are read as an object through an ObjectInputStream created on
     * that file), and added to the array. then the array is returned.
     * 
     * @param folder
     *            the folder to list
     * @param c
     *            the class of the objects to be deserialized, also the class of
     *            the return array.
     * @return an array of deserialized objects, one for each file in this
     *         folder.
     * @throws ClassCastException
     *             if the deserialized objects from any of the files are not
     *             instances of class c.
     */
    public static <T> T[] listObjectContentsAsArray(
        File folder, Class<T> c)
    {
        return listObjectContents(folder, c).values()
            .toArray((T[]) Array.newInstance(c, 0));
    }
    
    /**
     * This method is used to get the folder that holds all of this user's
     * installed plugins.
     * 
     * @return
     */
    public File getPluginStore()
    {
        return pluginStore;
    }
    
    public synchronized String getConfigProperty(String key)
    {
        ConfigProperty property = user.getProperty(key);
        if (property == null)
            return null;
        return property.getValue();
    }
    
    /**
     * same as getConfigProperty, but if getConfigProperty(key) returns null,
     * then setConfigProperty(key,defaultValue) is called, and defaultValue is
     * returned.
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public synchronized String getConfigProperty(
        String key, String defaultValue)
    {
        ConfigProperty property = user.getProperty(key);
        if (property == null)
        {
            property = user.createProperty();
            property.setName(key);
            property.setValue(defaultValue);
            user.getProperties().add(property);
            return defaultValue;
        }
        return property.getValue();
    }
    
    public synchronized void setConfigProperty(String key,
        String value)
    {
        ConfigProperty property = user.getProperty(key);
        if (property != null && key == null)
        {
            user.getProperties().remove(property);
            return;
        }
        if (property == null)
        {
            property = user.createProperty();
            property.setName(key);
            property.setValue(value);
            user.getProperties().add(property);
            return;
        }
        property.setValue(value);
    }
    
    /**
     * different from getConfigProperty only that these properties are system
     * wide, whereas getConfigProperty properties are user-specific.
     * 
     * @param key
     * @return
     */
    public static synchronized String getSystemConfigProperty(
        String key)
    {
        ConfigProperty property = dataStore
            .getProperty(key);
        if (property == null)
            return null;
        return property.getValue();
    }
    
    public static synchronized void setSystemConfigProperty(
        String key, String value)
    {
        ConfigProperty property = dataStore
            .getProperty(key);
        if (property != null && key == null)
        {
            dataStore.getProperties().remove(property);
            return;
        }
        if (property == null)
        {
            property = dataStore.createProperty();
            property.setName(key);
            property.setValue(value);
            dataStore.getProperties().add(property);
            return;
        }
        property.setValue(value);
    }
    
    public static TrustedCertificate createTrustedCertificate(
        String encoded)
    {
        TrustedCertificate tcert = proxyStorage
            .create(TrustedCertificate.class);
        tcert.setEncoded(encoded);
        return tcert;
    }
    
    public File getInboundMessagePlaintextStore()
    {
        return inboundMessagePlaintextStore;
    }
    
    public File getInboundMessageEncodedStore()
    {
        return inboundMessageEncodedStore;
    }
    
    public File getInboundMessageEncryptedStore()
    {
        return inboundMessageEncryptedStore;
    }
    
    public File getOutboundMessagePlaintextStore()
    {
        return outboundMessagePlaintextStore;
    }
    
    public File getOutboundMessageEncodedStore()
    {
        return outboundMessageEncodedStore;
    }
    
    public File getOutboundMessageEncryptedStore()
    {
        return outboundMessageEncryptedStore;
    }
    
}
