package net.sf.opengroove.client.plugins;

import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/**
 * This class represents a plugin.
 * 
 * @author Alexander Boyd
 * 
 */
public class Plugin
{
    
    private String id;
    
    private Properties metadata;
    
    private String type;
    
    private URL updateSite;
    
    private int versionIndex;
    
    private Image largeImage;
    
    private Image mediumImage;
    
    private Image smallImage;
    
    private boolean internal;
    
    private File file;
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public Class<E> getImplClass()
    {
        return implClass;
    }
    
    public void setImplClass(Class<E> implClass)
    {
        this.implClass = implClass;
    }
    
    public Properties getMetadata()
    {
        URLClassLoader loader;
        return metadata;
    }
    
    public void setMetadata(Properties metadata)
    {
        this.metadata = metadata;
    }
    
    public E create()
    {
        try
        {
            return implClass.newInstance();
        }
        catch (InstantiationException e)
        {
            // TODO Dec 3, 2007 Auto-generated catch block
            throw new RuntimeException(
                "TODO auto generated on Dec 3, 2007 : "
                    + e.getClass().getName() + " - "
                    + e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            // TODO Dec 3, 2007 Auto-generated catch block
            throw new RuntimeException(
                "TODO auto generated on Dec 3, 2007 : "
                    + e.getClass().getName() + " - "
                    + e.getMessage(), e);
        }
    }
    
    public URL getUpdateSite()
    {
        return updateSite;
    }
    
    public void setUpdateSite(URL updateSite)
    {
        this.updateSite = updateSite;
    }
    
    public int getVersionIndex()
    {
        return versionIndex;
    }
    
    public void setVersionIndex(int versionIndex)
    {
        this.versionIndex = versionIndex;
    }
    
    public Image getLargeImage()
    {
        return largeImage;
    }
    
    public void setLargeImage(Image largeImage)
    {
        this.largeImage = largeImage;
    }
    
    public Image getMediumImage()
    {
        return mediumImage;
    }
    
    public void setMediumImage(Image mediumImage)
    {
        this.mediumImage = mediumImage;
    }
    
    public Image getSmallImage()
    {
        return smallImage;
    }
    
    public void setSmallImage(Image smallImage)
    {
        this.smallImage = smallImage;
    }
    
    boolean isInternal()
    {
        return internal;
    }
    
    void setInternal(boolean internal)
    {
        this.internal = internal;
    }
    
    /**
     * returns the file that was used to load the plugin. this file will end
     * with .jar . if the plugin is an internal plugin, this file will be null.
     * 
     * @return
     */
    File getFile()
    {
        return file;
    }
    
    /**
     * sets the file that this plugin was loaded from. this file should end with
     * .jar
     * 
     * @param file
     */
    void setFile(File file)
    {
        this.file = file;
    }
}
