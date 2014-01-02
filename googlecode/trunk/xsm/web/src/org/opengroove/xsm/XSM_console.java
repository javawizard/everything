package org.opengroove.xsm;

import org.opengroove.xsm.dom.XDomParser;
import org.opengroove.xsm.web.client.gwt.XWebParser;
import org.opengroove.xsm.web.client.lang.XDisplayDevice;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInputDevice;
import org.opengroove.xsm.web.client.lang.XInterpreter;
import org.opengroove.xsm.web.client.lang.XLimitExceededException;
import org.opengroove.xsm.web.client.lang.XStackFrame;

import com.google.gwt.user.client.Window;

public class XSM_console
{
    public static String programToRun;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        programToRun = "<print><string>Hello</string></print>";
        doInterpreter();
    }
    
    public static void doInterpreter()
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
                XDomParser.parse("<xsm>" + programToRun + "</xsm>");
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
    
    public static void appendOutput(String s)
    {
        System.out.println(s);
    }
    
    public static void appendOutput(String s, boolean b)
    {
        if (b)
            appendOutput(s);
        else
            System.out.print(s);
    }
    
    public static void alertAndAppend(String s)
    {
        appendOutput(s);
    }
    
}
