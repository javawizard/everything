package jw.bznetwork.client.ui;

import com.google.gwt.user.client.ui.Label;

public class Spacer extends Label
{
    public Spacer(String width, String height)
    {
        super("&nbsp;");
        setWidth(width);
        setHeight(height);
    }
}
