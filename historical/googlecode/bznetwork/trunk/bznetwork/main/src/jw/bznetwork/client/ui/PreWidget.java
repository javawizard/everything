package jw.bznetwork.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class PreWidget extends Widget implements HasText
{
    private PreElement element;
    
    public PreWidget(String text)
    {
        element = Document.get().createPreElement();
        setElement(element);
        element.setInnerText(text);
    }
    
    @Override
    public String getText()
    {
        return element.getInnerText();
    }
    
    @Override
    public void setText(String text)
    {
        element.setInnerText(text);
    }
    
}
