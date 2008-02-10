package net.sf.convergia.client;

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

import net.sf.convergia.client.workspace.WorkspaceWrapper;

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

	public static void initStorage(File file)
	{
		if (base != null)
			throw new RuntimeException("Storage is already initialized");
		base = file;
		auth = new File(base, "auth");
		if (!auth.exists())
			auth.mkdirs();
		systemConfig = new File(base, "systemconfig");
		if (!systemConfig.exists())
			systemConfig.mkdirs();
	}

	public static void setCurrentUser(String username)
	{
		File tbase = new File(new File(base, "userspecific"), username);
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
	}
	
	public static File getFeatureStorage()
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

	private static File config;

	private static File workspaceDataStore;

	private static File workspaces;

	private static File mInCache;

	private static File mOutCache;

	private static File mArchive;

	private static File mAttachments;

	private static File contacts;

	private static File featureStorage;

	public static String[] getUsers()
	{
		return auth.list(new SubversionFilenameFilter());
	}

	public static void storeUser(String username, String password)
	{
		File userFile = new File(auth, username);
		userFile.mkdirs();
		try
		{
			writeFile(MD4MessageDigester.hexDigest(password.getBytes("ASCII")),
					new File(userFile, "password"));
		} catch (UnsupportedEncodingException e)
		{
			userFile.delete();
			// TODO Oct 22, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Oct 22, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	/**
	 * reads the file specified in to a string. the file must not be larger than
	 * 5 MB.
	 * 
	 * @param file.
	 * @return
	 */
	static String readFile(File file)
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
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	static void writeFile(String string, File file)
	{
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(string
					.getBytes("UTF-8"));
			FileOutputStream fos = new FileOutputStream(file);
			copy(bais, fos);
			bais.close();
			fos.flush();
			fos.close();
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

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

	public static boolean checkPassword(String user, String pass)
	{
		String realEncPassword = readFile(new File(new File(auth, user),
				"password"));
		String encPassword;
		try
		{
			encPassword = MD4MessageDigester.hexDigest(pass.getBytes("ASCII"));
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return realEncPassword.equalsIgnoreCase(encPassword);
	}

	public static Contact[] getAllContacts()
	{
		File[] contactEntries = contacts.listFiles();
		Contact[] contactArray = new Contact[contactEntries.length];
		for (int i = 0; i < contactEntries.length; i++)
		{
			contactArray[i] = (Contact) readObjectFromFile(contactEntries[i]);
		}
		return contactArray;
	}

	public static Contact getContact(String username)
	{
		if (!new File(contacts, username).exists())
			return null;
		return (Contact) readObjectFromFile(new File(contacts, username));
	}

	/**
	 * adds or updates a contact.
	 * 
	 * @param contact
	 */

	public static void setContact(Contact contact)
	{
		File contactFile = new File(contacts, contact.getUsername());
		if (contactFile.exists())
			contactFile.delete();
		writeObjectToFile(contact, contactFile);
	}

	public static void deleteContact(String username)
	{
		if (!new File(contacts, username.replace("/", "").replace("\\", ""))
				.delete())
			System.err.println("contact could not be deleted.");
	}

	private static void writeObjectToFile(Serializable object, File file)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(object);
			oos.flush();
			oos.close();
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private static Serializable readObjectFromFile(File file)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			Object object = ois.readObject();
			ois.close();
			return (Serializable) object;
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	static void recursiveDelete(File transmissionFolder)
	{
		if (transmissionFolder.isDirectory())
		{
			for (File file : transmissionFolder.listFiles())
			{
				recursiveDelete(file);
			}
		}
		transmissionFolder.delete();
	}

	private static int cIdVar = 0;

	public static synchronized String createIdentifier()
	{
		return "i" + System.currentTimeMillis() + "z" + cIdVar++;
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
	public static <T> HashMap<String, T> listObjectContents(File folder,
			Class<T> c)
	{
		HashMap<String, T> map = new HashMap<String, T>();
		for (File file : folder.listFiles(new SubversionFileFilter()))
		{
			if (file.isFile())
				map.put(file.getName(), (T) readObjectFromFile(file));
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
	public static <T> T[] listObjectContentsAsArray(File folder, Class<T> c)
	{
		return listObjectContents(folder, c).values().toArray(
				(T[]) Array.newInstance(c, 0));
	}

	public static WorkspaceWrapper[] listWorkspaces()
	{
		return listObjectContentsAsArray(workspaces, WorkspaceWrapper.class);
	}

	public static synchronized void addOrUpdateWorkspace(
			WorkspaceWrapper workspace)
	{
		if (!deletedWorkspaces.contains(workspace.getId()))
			writeObjectToFile(workspace,
					new File(workspaces, workspace.getId()));
	}

	public static synchronized void removeWorkspace(WorkspaceWrapper workspace)
	{
		new File(workspaces, workspace.getId()).delete();
		deletedWorkspaces.add(workspace.getId());
	}

	public static WorkspaceWrapper getWorkspaceById(String id)
	{
		return (WorkspaceWrapper) readObjectFromFile(new File(workspaces, id));
	}

	public static File getWorkspaceDataStore()
	{
		return workspaceDataStore;
	}

	public static String getConfigProperty(String key)
	{
		if (!new File(config, key).exists())
			return null;
		return readFile(new File(config, key));
	}

	public static void setConfigProperty(String key, String value)
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

	public static void setSystemConfigProperty(String key, String value)
	{
		if (value == null && key == "autologinuser")
		{
			System.out.println("%%%%%%autologinuser set to null");
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
