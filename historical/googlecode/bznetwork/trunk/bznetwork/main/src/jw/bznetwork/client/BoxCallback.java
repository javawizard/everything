package jw.bznetwork.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class BoxCallback<T> implements AsyncCallback<T>
{
    private PopupPanel box;
    
    public BoxCallback(PopupPanel box)
    {
        this.box = box;
    }
    
    /**
     * Same as {@link #BoxCallback(PopupPanel)}, but creates a new dialog box
     * and shows it.
     */
    public BoxCallback()
    {
        this.box = BZNetwork.showLoadingBox();
    }
    
    @Override
    public final void onFailure(Throwable caught)
    {
        box.hide();
        BZNetwork.fail(caught);
        fail(caught);
    }
    
    @Override
    public final void onSuccess(T result)
    {
        box.hide();
        run(result);
    }
    
    public abstract void run(T result);
    
    /**
     * This can be optionally overriden by the subclass to perform an action on
     * failure. This will be called after BZNetwork.fail() is called. The
     * default implementation does nothing.
     * 
     * @param caught
     */
    public void fail(Throwable caught)
    {
    }
    
}
