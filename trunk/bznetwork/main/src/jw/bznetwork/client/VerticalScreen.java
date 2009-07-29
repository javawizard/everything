package jw.bznetwork.client;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class VerticalScreen implements Screen
{
    protected VerticalPanel widget = new VerticalPanel();
    
    public Widget getWidget()
    {
        return widget;
    }
}
