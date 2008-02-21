package net.sf.convergia.client.toolworkspace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.sf.convergia.client.SubversionFileFilter;


/**
 * 
 * @author Mark Boyd
 * 
 */
public class WorkspaceStorage
{
	private File base;

	private File mTools;

	private File mToolStorage;

	private File mChat;

	public WorkspaceStorage(File file)
	{
		file.mkdirs();
		File tbase = file;
		if (!tbase.exists())
			tbase.mkdirs();
		mTools = iItem(tbase, "mtools");
		mToolStorage = iItem(tbase, "mtoolstorage");
		mChat = iItem(tbase, "mchat");
	}

	private File iItem(File tbase, String itemname)
	{
		File file = new File(tbase, itemname);
		if (!file.exists())
			file.mkdirs();
		return file;
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
			ex.printStackTrace();
			return null;
		}
	}

	static void recursiveDelete(File transmissionFolder)
	{
		if (transmissionFolder.isDirectory())
		{
			for (File file : transmissionFolder.listFiles())// NO SUBVERSION
															// FILTER
			{
				recursiveDelete(file);
			}
		}
		transmissionFolder.delete();
	}

	public void addOrUpdateTool(ToolWrapper tool)
	{
		System.out.println("____ADDING TOOL");
		writeObjectToFile(tool, new File(mTools, tool.getId()));
	}

	public ToolWrapper[] listTools()
	{
		return listObjectContentsAsArray(mTools, ToolWrapper.class);
	}

	public void removeTool(String toolId)
	{
		System.out.println("____REMOVING TOOl");
		new File(mTools, toolId).delete();
	}

	public ToolWrapper getToolById(String id)
	{
		return (ToolWrapper) readObjectFromFile(new File(mTools, id));
	}

	public File getToolDatastore()
	{
		return mToolStorage;
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

	public static final int MAX_CHAT_MESSAGES_LENGTH = 800;

	public void trimChatMessagesToMaxLength()
	{
		File[] chatMessageFiles = sortedChatFiles(mChat
				.listFiles(new SubversionFileFilter()));
		int deletes = 0;
		while (chatMessageFiles.length > MAX_CHAT_MESSAGES_LENGTH)
		{
			deletes++;
			if (deletes > 10000)
			{
				throw new RuntimeException("infinite "
						+ "chat message deleting recursion, check to "
						+ "make sure that chat message files are deletable");
			}
			chatMessageFiles[0].delete();
			chatMessageFiles = sortedChatFiles(mChat
					.listFiles(new SubversionFileFilter()));
		}
	}

	public ChatMessage[] listAllChatMessages()
	{
		return filesToMessages(sortedChatFiles(mChat
				.listFiles(new SubversionFileFilter())));
	}

	public void addChatMessage(ChatMessage message)
	{
		writeFile(message.getMessage(), new File(mChat, message.getId()));
	}

	public ChatMessage[] listChatMessagesForUser(final String username)
	{
		return filesToMessages(sortedChatFiles(mChat.listFiles(new FileFilter()
		{

			public boolean accept(File pathname)
			{
				return pathname.getName().endsWith("-" + username);
			}
		})));
	}

	/**
	 * sorts the list of chat message files passed in chronologically. returns
	 * the same array of files that is passed in.
	 * 
	 * @param files
	 * @return
	 */
	private File[] sortedChatFiles(File[] files)
	{
		Arrays.sort(files, new Comparator<File>()
		{

			public int compare(File o1, File o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		return files;
	}

	private ChatMessage[] filesToMessages(File[] files)
	{
		ChatMessage[] messages = new ChatMessage[files.length];
		for (int i = 0; i < files.length; i++)
		{
			messages[i] = new ChatMessage();
			messages[i].setId(files[i].getName());
			messages[i].setMessage(readFile(files[i]));
		}
		return messages;
	}

}
