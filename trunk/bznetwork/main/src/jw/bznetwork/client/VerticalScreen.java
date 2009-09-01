package jw.bznetwork.client;

import jw.bznetwork.client.ui.Spacer;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class VerticalScreen implements Screen
{
    protected VerticalPanel widget = new VerticalPanel();
    
    private Spacer spacer = new Spacer("1px", "1px");
    
    private DockPanel wrapper;
    
    public Widget getWidget()
    {
        if (wrapper == null)
        {
            wrapper = new DockPanel();
            wrapper.setWidth("100%");
            wrapper.add(spacer, wrapper.WEST);
            wrapper.add(widget, wrapper.CENTER);
        }
        return wrapper;
    }
    
    protected void setSpacing(String spacing)
    {
        spacer.setWidth(spacing);
    }
    
    /**
     * Does nothing. Subclasses can override this method if they need to get
     * tick information from BZNetwork.
     */
    public void tick(int number)
    {
        
    }
}
