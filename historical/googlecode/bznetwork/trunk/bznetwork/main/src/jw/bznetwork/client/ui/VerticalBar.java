package jw.bznetwork.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class VerticalBar extends HTML
{
    public VerticalBar()
    {
        super("&nbsp;|&nbsp;");
        setHeight("100%");
        addStyleName("bznetwork-VerticalBar");
    }
}
