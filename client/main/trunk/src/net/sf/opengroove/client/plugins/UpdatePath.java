package net.sf.opengroove.client.plugins;

/**
 * An UpdatePath is a path to a particular folder within an update site. It
 * contains both the update site that it is for, and the folder within that
 * update site.
 * 
 * @author Alexander Boyd
 * 
 */
public class UpdatePath
{
    /**
     * The url to the update site
     */
    private String updateSite;
    /**
     * The path to the folder. If this is the empty array, then this update path
     * refers directly to the update site.
     */
    private String[] folders;
    /**
     * The name of this path. For folders, this is the last element in the
     * folders array. For update sites, this is the name of the update site
     * (present in the update site's xml file), or the string "Loading..." if
     * the update site's name has not yet been downloaded.
     */
    private String name;
    
    public String getUpdateSite()
    {
        return updateSite;
    }
    
    public String[] getFolders()
    {
        return folders;
    }
    
    public void setUpdateSite(String updateSite)
    {
        this.updateSite = updateSite;
    }
    
    public void setFolders(String[] folders)
    {
        this.folders = folders;
    }
}
