package jw.bznetwork.client.ui;

import com.google.gwt.user.client.ui.Label;

public class Header3 extends Label
{
    public Header3(String text, boolean centered)
    {
        super(text);
        addStyleName("bznetwork-Header3");
        if (centered)
            addStyleName("bznetwork-centered");
    }
    
    public Header3(String text)
    {
        this(text, false);
    }
}
