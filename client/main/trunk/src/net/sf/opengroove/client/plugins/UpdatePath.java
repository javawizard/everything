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
