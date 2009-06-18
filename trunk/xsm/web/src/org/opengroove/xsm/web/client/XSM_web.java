package org.opengroove.xsm.web.client;

import java.util.HashMap;

import org.opengroove.xsm.web.client.gwt.XWebParser;
import org.opengroove.xsm.web.client.lang.XDisplayDevice;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInputDevice;
import org.opengroove.xsm.web.client.lang.XInterpreter;
import org.opengroove.xsm.web.client.lang.XLimitExceededException;
import org.opengroove.xsm.web.client.lang.XStackFrame;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
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
    
    private static HashMap<String, String> examples = new HashMap<String, String>();
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        loadExamples();
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
        HorizontalPanel codePanel = new HorizontalPanel();
        codePanel.add(codeArea);
        VerticalPanel examplesPanel = new VerticalPanel();
        examplesPanel.add(new HTML("&nbsp;"));
        examplesPanel.add(new HTML(
            "<b>Examples: </b><small>Click an example to load it "
                + "into the text area at left</small>"));
        loadExamplesPanel(examplesPanel);
        codePanel.add(examplesPanel);
        mainPanel.add(codePanel);
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
                appendOutput("", true);
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
    
    private void loadExamplesPanel(VerticalPanel examplesPanel)
    {
        for (final String s : examples.keySet())
        {
            Anchor a = new Anchor(" &nbsp; &nbsp; " + s, true);
            a.addClickListener(new ClickListener()
            {
                
                public void onClick(Widget sender)
                {
                    HTTPRequest.asyncGet("examples/" + examples.get(s),
                        new ResponseTextHandler()
                        {
                            
                            public void onCompletion(String responseText)
                            {
                                codeArea.setText(responseText);
                            }
                        });
                }
            });
            examplesPanel.add(a);
        }
    }
    
    private void loadExamples()
    {
        loadExample("Hello world", "hello-world");
        loadExample("Print numbers from 1 to 10", "one-to-ten.xsm");
        loadExample("Print multiples of 3 up to 15", "three-times.xsm");
    }
    
    private void loadExample(String string, String string2)
    {
        examples.put(string, string2 + ".xsm");
    }
    
    protected void doInterpreter()
    {
        XInterpreter interpreter = new XInterpreter();
        try
        {
            interpreter.installDefaultCommands();
            interpreter.setDisplay(new XDisplayDevice()
            {
                
                public void print(String string, boolean newline)
                {
                    appendOutput(string, newline);
                }
            });
            interpreter.setInput(new XInputDevice()
            {
                
                public String prompt(String message)
                {
                    if (message == null)
                        message = "XSM Program Prompt";
                    return Window.prompt(message, "");
                }
            });
            XElement rootElement =
                XWebParser.parse("<xsm>" + codeArea.getText() + "</xsm>");
            interpreter.executeChildren(rootElement, null);
        }
        catch (XException e)
        {
            appendOutput("XSM Error: " + e.getMessage());
            for (XStackFrame frame : e.getProgramStack())
            {
                appendOutput("    in " + frame.getCommand());
            }
            if (e instanceof XLimitExceededException)
            {
                alertAndAppend("Your program went above the maximum instruction limit. Since \n"
                    + "the browser freezes while the program is running, the interpreter \n"
                    + "limits the number of instructions per program to around 1000. \n\n"
                    + "If you'd like to raise the limit, add this code to the beginning of \n"
                    + "your program (you can change the value to any suitable limit): \n\n"
                    + "<config name=\"limit\" value=\"5000\"/>");
            }
            else
            {
                alertAndAppend("Your program encountered an error. The stack trace "
                    + "has been printed to the output.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            appendOutput("An unexpected error occured");
            appendOutput("Class: " + e.getClass().getName());
            appendOutput("Message: " + e.getMessage());
            alertAndAppend("An internal error occured. This means that the program's xml \n"
                + "is not valid xml, or this is an internal error. Send an \n"
                + "email to javawiza"
                + "rd@triv"
                + "ergia.c"
                + "om about the problem, and I'll fix it.");
            /*
             * The email is split across multiple strings to obfuscate it.
             * Hopefully GWT's compiler will preserve that.
             */
        }
    }
    
    private void alertAndAppend(String string)
    {
        appendOutput(string);
        Window.alert(string);
    }
    
    public void appendOutput(String string)
    {
        appendOutput(string, true);
    }
    
    protected void appendOutput(String string, boolean newline)
    {
        outputArea.setText(outputArea.getText() + string + (newline ? "\n" : ""));
    }
}
