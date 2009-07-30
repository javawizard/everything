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
    public void onFailure(Throwable caught)
    {
        box.hide();
        BZNetwork.fail(caught);
    }
    
    @Override
    public void onSuccess(T result)
    {
        box.hide();
        run(result);
    }
    
    public abstract void run(T result);
    
}
