package net.sf.opengroove.client.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.interdirected.autoupdate.AutomatedUpdate;
import net.sf.opengroove.client.OpenGroove;

/**
 * This class is used to actually start OpenGroove on a user's box. developers
 * should use the class {@link OpenGroove} to start, instead of this one, to
 * avoid installing updates from the server. This class installs any updates to
 * OpenGroove itself that are downloaded, and then calls OpenGroove.main().<br/><br/>
 * 
 * This class is deprecated because it is in the process of being replaced by
 * Java ANT/SVN Automatic Updater, an auto-updater package from Interdirected.
 * 
 * @author Alexander Boyd
 * @deprecated
 * 
 */
@Deprecated
public class Loader
{
    public static final String REPOSITORY =
        "http://opengroove.svn.sourceforge.net/svnroot/opengroove/client/main/updates";
    
    public static final String DEFAULT_LEVEL = "Release";
    
    /**
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable
    {
        try
        {
            File appdata = new File("appdata");
            File updatesFolder = new File(appdata, "updates");
            File updatesAvailableFile = new File(updatesFolder, "updatesavailable");
            if (updatesAvailableFile.exists())
            {
                System.out.println("updates are available");
                String level = readFile(new File(updatesFolder, "level"));
                if (level == null)
                    level = DEFAULT_LEVEL;
                String modulePath = level + "/tags";
                /*
                 * In the future, we should reset the level to the default if
                 * the level in question doesn't exist, to avoid getting into a
                 * state where clients can't update. We should also reset to the
                 * first level available if the default level doesn't exist.
                 */
                writeFile(" ", new File(updatesFolder, "updateinprogress"));
                AutomatedUpdate.main(new String[] { "-tagmode", "-nofork", "-changelog",
                    "-repositoryurl", REPOSITORY, "-moduleurl", modulePath, "-antlocation",
                    "lib", "-applicationdirectory", ".", "-launchant", "-customgui",
                    "net.sf.opengroove.client.loader.UpdateGUI" });
                /*
                 * Right now, OpenGroove is still run, even if updates fail.
                 * This isn't a good idea, since OpenGroove can be left in a
                 * conflicting state. In the future, something needs to be added
                 * so that AutomatedUpdate can indicate to it's caller why
                 * updates failed. That way, if updates fail before they begin
                 * (which would happen if an internet connection is not
                 * present), then OpenGroove would still start, but if updates
                 * fail after they begin, then OpenGroove would not start until
                 * updates succeed at least once after that.
                 */
                new File(updatesFolder, "updateinprogress").delete();
                updatesAvailableFile.delete();
                writeFile(" ", new File(updatesFolder, "updated"));
            }
            else
            {
                System.out.println("no updates are available");
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        doRun(args);
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
                throw new RuntimeException("the file is " + file.length()
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
            if (e instanceof FileNotFoundException)
                return null;
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
            ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(file);
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
    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
    }
    
    private static void doRun(String[] s) throws Throwable
    {
        OpenGroove.updatesEnabled = true;
        OpenGroove.main(s);
    }
    
}
