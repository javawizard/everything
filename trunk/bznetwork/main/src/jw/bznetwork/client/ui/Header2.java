package jw.bznetwork.client.ui;

import com.google.gwt.user.client.ui.Label;

public class Header2 extends Label
{
    public Header2(String text, boolean centered)
    {
        super(text);
        addStyleName("bznetwork-Header2");
        if(centered)
            addStyleName("bznetwork-centered");
    }
    
    public Header2(String text)
    {
        this(text,false);
    }
}
