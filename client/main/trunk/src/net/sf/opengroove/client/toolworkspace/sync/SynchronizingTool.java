package net.sf.opengroove.client.toolworkspace.sync;

import javax.swing.JComponent;

import net.sf.opengroove.client.toolworkspace.Tool;

/**
 * <b>This class is deprecated.</b> It is being replaced by the concept of
 * engines (see www.opengroove.org/dev/workspace-synchronization), which
 * will allow all tools to persist large chunks of data, and they can choose any
 * model for that data (relations, hierarchy, chunks, etc.).<br/><br/>
 * 
 * NOTE: this class is preferred over
 * net.sf.opengroove.client.toolworkspace.SynchronizingTool<br/><br/>
 * 
 * This class allows implementations to store pieces of data (referred to as
 * chunks) that will be synchronized between computers that use this tool.
 * Chunks have a name, which should be no longer than 512 characters. the main
 * data of a chunk is stored in it's properties, which are made up of keys and
 * values. the combined size of these keys and value, plus the size of the chunk
 * name, plus 3 bytes for each property, should not exceed 970KB.
 * 
 * Synchronizations of a chunk are guaranteed to be atomic. In other words, If
 * you lock a chunk, modify some properties, and then unlock it, it is
 * guaranteed that all of the properties will be synchronized to each member, at
 * the same time for each member. In other words, you won't have only a few
 * properties get synchronized at a time.
 * 
 * <Br/><br/>A few reccomendations:<br/>
 * <ul>
 * <li>Don't create and then delete chunks excessively. Whenever a chunk is
 * deleted, until all members of this workspace synchronize, a marker (a file of
 * about 20 - 50 bytes) is stored on the local computer. while it doesn't
 * consume much storage space as is, a lot of chunks deleted can start taking up
 * hard disk space. the creator of a workspace is notified if a user has not
 * logged in over the course of one month, so if a user never comes online, the
 * creator will likely remove them from the workspace, thereby making it so that
 * delete markers don't get stuck around.</li>
 * <li>Try to use small chunks, where possible. When changes have occured to a
 * chunk, the whole chunk is sent to all members, not just the part that
 * changed. The smaller a chunk, the faster synchronization will go when the
 * chunk is modified.</li>
 * <li>Try not to lock a chunk and then unlock it without actually modifying
 * any data. Whenever a chunk is unlocked, a synchronization is performed
 * between all members, because this class assumes that the chunk has changed.
 * This can be costly in terms of network usage.</li>
 * </ul>
 * 
 * @author Alexander Boyd
 * 
 */
@Deprecated
public abstract class SynchronizingTool extends Tool
{
    private boolean isShutdown = false;
    
    /**
     * This method is normally used to initialize a tool. SynchronizingTool does
     * it's own initializing, so it overrides this method, and then calls
     * syncInitialize(). If you have any code for this method, put it there.
     */
    @Override
    public final void initialize()
    {
        try
        {
            syncInitialize();
        }
        catch (Exception ex1)
        {
            ex1.printStackTrace();
        }
    }
    
    @Override
    protected void sendMessage(String to, String message)
    {
        super.sendMessage(to, "i|" + message);
    }
    
    protected abstract void syncInitialize();
    
    /**
     * This method is normally used to initialize a tool. SynchronizingTool does
     * it's own initializing, so it overrides this method, and then calls
     * syncReceiveMessage(). If you have code for this method, put it there.
     */
    @Override
    public final void receiveMessage(String from,
        String message)
    {
        if (message.startsWith("sync|"))
            processMessage(from, message.substring("sync|"
                .length()));
        else if (message.startsWith("i|"))
            syncReceiveMessage(from, message.substring("i|"
                .length()));
    }
    
    private void processMessage(String from, String message)
    {
    }
    
    protected abstract void syncReceiveMessage(String from,
        String message);
    
    /**
     * This method is normally used to initialize a tool. SynchronizingTool does
     * it's own initializing, so it overrides this method, and then calls
     * syncInitialize(). If you have any code for this method, put it there.
     */
    @Override
    public final void shutdown()
    {
        this.isShutdown = true;
        try
        {
            syncShutdown();
        }
        catch (Exception ex1)
        {
            ex1.printStackTrace();
        }
    }
    
    protected abstract void syncShutdown();
    
    /**
     * This method is normally used to initialize a tool. SynchronizingTool does
     * it's own initializing, so it overrides this method, and then calls
     * syncInitialize(). If you have code for this method, put it there.
     */
    @Override
    public final void userStatusChanged()
    {
        try
        {
            syncUserStatusChanged();
        }
        catch (Exception ex1)
        {
            ex1.printStackTrace();
        }
    }
    
    protected abstract void syncUserStatusChanged();
    
}
