package org.opengroove.xsm.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class XSM_web implements EntryPoint
{
    public static TextArea codeArea;
    
    public static TextArea outputArea;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        RootPanel root = RootPanel.get();
        HTMLPanel wrapper =
            new HTMLPanel("<table width='100%' height='100%' border='0' "
                + "cellspacing='0' cellpadding='0'>"
                + "<tr><td colspan='3'>&nbsp;</td></tr>"
                + "<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td valign='top'>"
                + "<div id='xsm_embedded'/></td>"
                + "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>"
                + "<tr><td>&nbsp;</td></tr></table>");
        root.add(wrapper);
        VerticalPanel mainPanel = new VerticalPanel();
        wrapper.add(mainPanel, "xsm_embedded");
        mainPanel.setWidth("100%");
        mainPanel.add(new HTML("Type an XSM program here, then click &quot;"
            + "Run program&quot;. The program's output will then be shown. You "
            + "can view the XSM language reference "
            + "<a target='_blank' href='http://wiki.opengroove.org/XSM'>here</a>."));
        codeArea = new TextArea();
        codeArea.setCharacterWidth(100);
        codeArea.setVisibleLines(15);
        mainPanel.add(codeArea);
        outputArea = new TextArea();
        outputArea.setCharacterWidth(100);
        outputArea.setVisibleLines(10);
        Button runButton = new Button("Run program");
        Button clearOutputButton = new Button("Clear output");
        HorizontalPanel runPanel = new HorizontalPanel();
        runPanel.add(runButton);
        runPanel.add(clearOutputButton);
        mainPanel.add(runPanel);
        mainPanel.add(new HTML("<b>Output:</b>"));
        mainPanel.add(outputArea);
        runButton.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                outputArea.setText("");
                doInterpreter();
                appendOutput("Done.", true);
            }
        });
        clearOutputButton.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                outputArea.setText("");
            }
        });
    }
    
    protected void doInterpreter()
    {
        // TODO Auto-generated method stub
        
    }
    
    protected void appendOutput(String string, boolean b)
    {
        // TODO Auto-generated method stub
        
    }
}
