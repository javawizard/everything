package net.sf.opengroove.client;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import net.sf.opengroove.client.workspace.WorkspaceWrapper;
import net.sf.opengroove.security.Hash;

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
    
    private static ArrayList<String> deletedWorkspaces = new ArrayList<String>();
    
    private static File systemConfig;
    
    /**
     * Initializes the Storage class. This should only be called once per JVM
     * instance.
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
        auth = new File(base, "auth");
        if (!auth.exists())
            auth.mkdirs();
        systemConfig = new File(base, "systemconfig");
        if (!systemConfig.exists())
            systemConfig.mkdirs();
    }
    
    private String userid;
    
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
        this.userid = userid;
        File tbase = new File(
            new File(base, "userspecific"), userid);
        if (!tbase.exists())
            tbase.mkdirs();
        mOutCache = iItem(tbase, "moutcache");
        mInCache = iItem(tbase, "mincache");
        mArchive = iItem(tbase, "marchive");
        mAttachments = iItem(tbase, "mattachments");
        contacts = iItem(tbase, "contacts");
        workspaces = iItem(tbase, "workspaces");
        workspaceDataStore = iItem(tbase, "workspacedstore");
        config = iItem(tbase, "config");
        featureStorage = iItem(tbase, "featuremanager");
        pluginStore = iItem(tbase, "plugins");
        helpStore = iItem(tbase, "help");
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
    public static synchronized Storage get(String realm,
        String username)
    {
        String userid = realm + ":" + username;
        Storage storage = singletons.get(userid);
        if (storage == null)
        {
            storage = new Storage(userid);
            singletons.put(userid, storage);
        }
        return storage;
    }
    
    public File getFeatureStorage()
    {
        return featureStorage;
    }
    
    private static File iItem(File tbase, String itemname)
    {
        File file = new File(tbase, itemname);
        if (!file.exists())
            file.mkdirs();
        return file;
    }
    
    private static File auth;
    
    private File config;
    
    private File workspaceDataStore;
    
    private File workspaces;
    
    private File mInCache;
    
    private File mOutCache;
    
    private File mArchive;
    
    private File mAttachments;
    
    private File contacts;
    
    private File featureStorage;
    
    private File pluginStore;
    
    private File helpStore;
    
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
        LocalUser[] users = listObjectContentsAsArray(auth,
            LocalUser.class);
        for(LocalUser user : users)
        {
            if(OpenGroove.userContextMap.get(user.ge))
        }
        return users;
    }
    
    /**
     * Adds a new user, or updates an existing one if there is a user stored
     * with the same realm and username.
     * 
     * @param user
     *            The user to store
     */
    public static void storeUser(LocalUser user)
    {
        File userFile = new File(auth, user.getRealm()
            + ":" + user.getUsername());
        writeObjectToFile(user, new File(auth, user
            .getRealm()
            + ":" + user.getUsername()));
    }
    
    /**
     * Gets the user object for the user that this Storage instance is for.
     * 
     * @return
     */
    public LocalUser getLocalUser()
    {
        return (LocalUser) readObjectFromFile(new File(
            auth, this.userid));
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
        return (LocalUser) readObjectFromFile(new File(
            auth, userid));
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
    public static boolean checkPassword(String realm,
        String username, String pass)
    {
        LocalUser user = getLocalUser(realm, username);
        return user.getEncPassword()
            .equals(Hash.hash(pass));
    }
    
    /**
     * Gets a list of contacts for this user. This list may be a bit large, so
     * it's best not to over-use this method.
     * 
     * @return a list of contacts for this user.
     */
    public synchronized Contact[] getAllContacts()
    {
        File[] contactEntries = contacts.listFiles();
        Contact[] contactArray = new Contact[contactEntries.length];
        for (int i = 0; i < contactEntries.length; i++)
        {
            contactArray[i] = (Contact) readObjectFromFile(contactEntries[i]);
        }
        return contactArray;
    }
    
    /**
     * Gets a particular contact by the realm and username specified, returning
     * null if the contact specified does not exist.
     * 
     * @param realm
     *            The user's realm
     * @param username
     *            The user's username
     * @return The contact, or null if the contact does not exist on this
     *         computer
     */
    public synchronized Contact getContact(String realm,
        String username)
    {
        if (!new File(contacts, realm + ":" + username)
            .exists())
            return null;
        return (Contact) readObjectFromFile(new File(
            contacts, realm + ":" + username));
    }
    
    /**
     * adds or updates a contact. If the contact already exists (IE a contact
     * with the same username and realm is present on the file system), the
     * contact's information will be updated. If not, the contact will be
     * created.
     * 
     * @param contact
     */
    
    public synchronized void setContact(Contact contact)
    {
        File contactFile = new File(contacts, contact
            .getRealm()
            + ":" + contact.getUsername());
        if (contactFile.exists())
            contactFile.delete();
        writeObjectToFile(contact, contactFile);
    }
    
    /**
     * Deletes the contact specified, throwing a RuntimeException if the
     * operation failed.
     * 
     * @param realm
     *            the realm of the contact to delete
     * @param username
     *            the username of the contact to delete
     */
    public synchronized void deleteContact(String realm,
        String username)
    {
        if (!new File(contacts, (realm + ":" + username)
            .replace("/", "").replace("\\", "")).delete())
            throw new RuntimeException(
                "The contact could not be deleted.");
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
            throw new RuntimeException(ex);
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
    
    private static volatile int cIdVar = 0;
    
    public static synchronized String createIdentifier()
    {
        return "i" + System.currentTimeMillis() + "z"
            + cIdVar++;
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
     * Lists the workspace information for all workspaces that this user has.
     * 
     * @return
     */
    public synchronized WorkspaceWrapper[] listWorkspaces()
    {
        return listObjectContentsAsArray(workspaces,
            WorkspaceWrapper.class);
    }
    
    public synchronized void addOrUpdateWorkspace(
        WorkspaceWrapper workspace)
    {
        if (!deletedWorkspaces.contains(workspace.getId()))
            writeObjectToFile(workspace, new File(
                workspaces, workspace.getId()));
    }
    
    public synchronized void removeWorkspace(
        WorkspaceWrapper workspace)
    {
        new File(workspaces, workspace.getId()).delete();
        deletedWorkspaces.add(workspace.getId());
    }
    
    public WorkspaceWrapper getWorkspaceById(String id)
    {
        return (WorkspaceWrapper) readObjectFromFile(new File(
            workspaces, id));
    }
    
    public File getWorkspaceDataStore()
    {
        return workspaceDataStore;
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
    
    public String getConfigProperty(String key)
    {
        if (!new File(config, key).exists())
            return null;
        return readFile(new File(config, key));
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
    public String getConfigProperty(String key,
        String defaultValue)
    {
        String p = getConfigProperty(key);
        if (p == null)
        {
            setConfigProperty(key, defaultValue);
            return defaultValue;
        }
        return p;
    }
    
    public void setConfigProperty(String key, String value)
    {
        if (value == null)
            new File(config, key).delete();
        else
            writeFile(value, new File(config, key));
    }
    
    /**
     * different from getConfigProperty only that these properties are system
     * wide, whereas getConfigProperty properties are user-specific.
     * 
     * @param key
     * @return
     */
    public static String getSystemConfigProperty(String key)
    {
        if (!new File(systemConfig, key).exists())
            return null;
        return readFile(new File(systemConfig, key));
    }
    
    public static void setSystemConfigProperty(String key,
        String value)
    {
        if (value == null && key == "autologinuser")
        {
            System.out
                .println("%%%%%%autologinuser set to null");
            Exception e = new Exception();
            StackTraceElement[] st = e.getStackTrace();
            System.out.println("st1 " + st[0]);
            System.out.println("st2" + st[1]);
            System.out.println();
            System.out.println();
        }
        if (value == null)
            new File(systemConfig, key).delete();
        else
            writeFile(value, new File(systemConfig, key));
    }
    
}
