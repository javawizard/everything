package net.sf.convergia.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This class is used to actually start Convergia on a user's box. developers
 * should use the class Convergia to start, instead of this one, to avoid
 * installing updates from the server. This class installs any updates to
 * Convergia itself that are downloaded, and then calls Convergia.main()
 * 
 * @author Alexander Boyd
 * 
 */
public class Loader
{

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable
	{
		try
		{
			File updatefolder = new File("appdata/systemupdates");
			if (updatefolder.exists())
			{
				File updateJarFile = new File(updatefolder, "updates.jar");
				File versionFile = new File(updatefolder, "version");
				if (updateJarFile.exists() && versionFile.exists())
				{
					JFrame frame = new JFrame("OpenGroove Updating");
					frame.setSize(350, 80);
					frame.setLocationRelativeTo(null);
					frame
							.getContentPane()
							.add(
									new JLabel(
											"OpenGroove is updating and will start in a moment"));
					frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
					frame.show();
					frame.invalidate();
					frame.validate();
					frame.repaint();
					extractUpdates(updateJarFile, new File(".")
							.getAbsoluteFile());
					new File("version").delete();
					versionFile.renameTo(new File("version"));
					updateJarFile.delete();
					frame.dispose();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		doRun(args);
	}

	private static void doRun(String[] s) throws Throwable
	{
		Convergia.updatesEnabled = true;
		Convergia.main(s);
	}

	public static void extractUpdates(File updatejar, File dest)
	{
		try
		{
			System.out.println("loading jar file");
			JarFile file = new JarFile(updatejar);
			System.out.println("about to extract contents");
			byte[] buffer = new byte[4096];
			int amount;
			for (JarEntry entry : Collections.list(file.entries()))
			{
				System.out.println("extracting entry " + entry.getName());
				File targetFile = new File(dest, entry.getName());
				targetFile.getAbsoluteFile().getParentFile().mkdirs();
				if (!entry.isDirectory())
				{
					System.out.println("entry is a file.");
					InputStream stream = file.getInputStream(entry);
					FileOutputStream fos = new FileOutputStream(targetFile);
					while ((amount = stream.read(buffer)) != -1)
					{
						fos.write(buffer, 0, amount);
					}
					fos.flush();
					fos.close();
					stream.close();
				}
				System.out.println("extracted entry successfully.");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		System.out.println("successfully extracted jar file.");
	}
}
