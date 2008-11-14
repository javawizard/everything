package net.sf.opengroove.realmserver.gwt.core.chart;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class GCanvas extends Widget
{
    private String idPrefix;
    private Element tableElement;
    
    public GCanvas(int width, int height,
        String defaultColor)
    {
        idPrefix = DOM.createUniqueId();
        tableElement = DOM.createTable();
        tableElement.setId(DOM.createUniqueId());
        tableElement.setAttribute("border", "0");
        tableElement.setAttribute("cellspacing", "0");
        tableElement.setAttribute("cellpadding", "0");
        setElement(tableElement);
        for (int r = 0; r < height; r++)
        {
            Element rowElement = DOM.createTR();
            rowElement.setId(DOM.createUniqueId());
            rowElement.setAttribute("style", "height: 1px");
            tableElement.appendChild(rowElement);
            for (int c = 0; c < width; c++)
            {
                Element colElement = DOM.createTD();
                colElement.setId(getId(c, r));
                colElement.setAttribute("style",
                    "width: 1px; height: 1px");
                colElement.setAttribute("bgcolor",
                    defaultColor);
                rowElement.appendChild(colElement);
            }
        }
    }
    
    private String getId(int x, int y)
    {
        return idPrefix + "-" + x + "-" + y;
    }
    
    public void fillRect(String color, int x, int y,
        int width, int height)
    {
        for (int cx = x; cx < x + width; cx++)
        {
            for (int cy = y; cy < y + height; cy++)
            {
                Element cElement = DOM
                    .getElementById(getId(cx, cy));
                if (cElement == null)
                    continue;
                cElement.setAttribute("bgcolor", color);
            }
        }
    }
}
