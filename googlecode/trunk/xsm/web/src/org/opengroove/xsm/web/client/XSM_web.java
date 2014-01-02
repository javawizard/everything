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
    private static final int MAX_STACK_LENGTH = 25;
    
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
            "&nbsp; &nbsp; <b>Examples: </b><small>Click an example to load it "
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
        mainPanel.add(new HTML("The original idea for an XML-based programming "
            + "language came from Mark Carter's XS "
            + "programming language, <a target='_blank' href='"
            + "http://www.markcarter.me.uk/computing/xs.html'>"
            + "http://www.markcarter.me.uk/computing/xs.html</a>"));
        mainPanel.add(new HTML(
            "XSM is written by Alexander Boyd (aka javawizard2539/jcp). "
                + "Source code for the interpreter is "
                + "<a target='_blank' href='http://opengroove.googlecode.com/svn/"
                + "trunk/xsm/web/src/org/opengroove/xsm/web"
                + "/client/lang/'>here</a>, go up one folder for the web "
                + "viewer source, or <tt>svn checkout</tt> " + "that location."));
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
        if (Window.Location.getParameter("example") != null)
        {
            String url = Window.Location.getParameter("example");
            if (url.equals("w"))
                url = "http://opengroove.googlecode.com/svn/wiki/XSM.wiki";
            String prefix = Window.Location.getParameter("prefix");
            String start = Window.Location.getParameter("start");
            String end = Window.Location.getParameter("end");
            try
            {
                populateCode(url, prefix, start, end);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Window.alert("Could not populate code: " + e.getClass().getName()
                    + ": " + e.getMessage());
            }
        }
    }
    
    private void loadExamplesPanel(VerticalPanel examplesPanel)
    {
        for (final String s : examples.keySet())
        {
            Anchor a = new Anchor(s);
            HorizontalPanel p = new HorizontalPanel();
            p.add(new HTML(" &nbsp; &nbsp; &nbsp; &nbsp; "));
            p.add(a);
            a.addClickListener(new ClickListener()
            {
                
                public void onClick(Widget sender)
                {
                    populateCode("examples/" + examples.get(s), null, null, null);
                }
            });
            examplesPanel.add(p);
        }
    }
    
    /**
     * Populates the code field with code from the specified url.<br/>
     * <br/>
     * 
     * For some help on what all the parameters mean, see
     * http://opengroove.googlecode.com/svn/wiki/XSM.wiki . It has a bunch of
     * URLs that cause this method to be called.
     * 
     * @param url
     *            The url to look for
     * @param prefix
     *            The prefix. If this is not null, this location will be found
     *            in the file, and wherever <tt>start</tt>occurs after that will
     *            be where the code starts.
     * @param start
     *            The start text, which comes after prefix
     * @param end
     *            The end text, which ends the code example
     */
    protected void populateCode(String url, final String prefix, final String start,
        final String end)
    {
        HTTPRequest.asyncGet(url, new ResponseTextHandler()
        {
            
            public void onCompletion(String responseText)
            {
                if (prefix != null && start != null && end != null)
                {
                    int prefixIndex = responseText.indexOf(prefix);
                    int startIndex = responseText.indexOf(start, prefixIndex);
                    int endIndex = responseText.indexOf(end, startIndex);
                    responseText =
                        responseText.substring(startIndex + start.length(), endIndex);
                }
                codeArea.setText(responseText);
            }
        });
    }
    
    private void loadExamples()
    {
        loadExample("Hello world", "hello-world");
        loadExample("99 bottles of pop", "9-bottles");
        loadExample("Print numbers from 1 to 10", "one-to-ten");
        loadExample("Print multiples of 3 up to 15", "three-times");
        loadExample("Test custom function", "test-function");
        loadExample("Simple calculator", "simple-calculator");
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
            interpreter.installDefaultCommands();
            XElement rootElement =
                XWebParser.parse("<xsm>" + codeArea.getText() + "</xsm>");
            if (rootElement.getChildren().size() == 1
                && (rootElement.getChild(0) instanceof XElement))
            {
                XElement ce = (XElement) rootElement.getChild(0);
                if (ce.getTag().equalsIgnoreCase("xsm"))
                    rootElement = ce;
            }
            interpreter.executeChildren(rootElement, null);
        }
        catch (XException e)
        {
            appendOutput("XSM Error: " + e.getMessage());
            int framesPrinted = 0;
            for (XStackFrame frame : e.getProgramStack())
            {
                if (++framesPrinted > MAX_STACK_LENGTH)
                {
                    appendOutput("... stack truncated at " + MAX_STACK_LENGTH
                        + " elements, "
                        + (e.getProgramStack().size() - MAX_STACK_LENGTH) + " more");
                    break;
                }
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
