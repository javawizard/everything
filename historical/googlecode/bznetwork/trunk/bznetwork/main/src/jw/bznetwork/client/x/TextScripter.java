package jw.bznetwork.client.x;

import java.util.Map;

import jw.bznetwork.client.x.gwt.XWebParser;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XDisplayDevice;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreter;
import jw.bznetwork.client.x.lang.XInterpreterContext;

public class TextScripter
{
    private static XInterpreter interpreter = new XInterpreter();
    private static StringBuffer currentBuffer;
    static
    {
        interpreter.configuration.put("limit", "30000");
        interpreter.installDefaultCommands();
        installBZNetworkSet();
    }
    
    /**
     * Executes as XSM script anything between matching [xsm] pairs. Loops and
     * statements can cross boundaries, so, for example,
     * <tt>[xsm]&lt;if&gt;&lt;condition&gt;...&lt;/condition&gt;[xsm]
     * The condition is true[xsm]&lt;/if&gt;[xsm]</tt> would print
     * "The condition is true" if the condition between
     * <tt>&lt;condition&gt;</tt> and <tt>&lt;/condition&gt;</tt> is true.
     * Dynamic output can be generated using the XSM <tt>&lt;print&gt;</tt>
     * command.<br/><br/>
     * 
     * If you need to have the output contain the literal string "[xsm]", the
     * best way to do it would be to do something like
     * <tt>[xsm]&lt;print&gt;[&lt;/print&gt;&lt;print&gt;xsm]&lt;/print&gt;[xsm]</tt>
     * .
     * 
     * @param input
     *            The input string
     * @param commands
     *            Extra commands to load into the interpreter
     * @return The input string after executing all XSM code
     */
    public static String run(String input, Map<String, XData> vars,
            XCommand... commands)
    {
        /*
         * First, we'll install the custom commands.
         */
        for (XCommand command : commands)
        {
            interpreter.install(command);
        }
        /*
         * Now we'll go through and build a command string to parse.
         */
        StringBuffer command = new StringBuffer();
        String[] tokens = input.split("\\[xsm\\]");
        /*
         * Even strings are literal strings, and odd strings are XSM commands.
         */
        for (int i = 0; i < tokens.length; i++)
        {
            if ((i % 2) == 0)
            {
                /*
                 * This is a literal string.
                 */
                command.append("<print newline=\"false\">");
                command.append(escapeXml(tokens[i]));
                command.append("</print>");
            }
            else
            {
                /*
                 * This is an XSM command string.
                 */
                command.append(tokens[i]);
            }
        }
        /*
         * The command string has been built. Now we execute it.
         */
        currentBuffer = new StringBuffer();
        XElement rootElement = XWebParser.parse("<xsm>" + command.toString()
                + "</xsm>");
        command = null;
        XInterpreterContext context = new XInterpreterContext(interpreter, true);
        if (vars != null)
            context.getVariables().putAll(vars);
        interpreter.executeChildren(rootElement, context);
        /*
         * Now we remove the commands we installed.
         */
        for (XCommand c : commands)
        {
            interpreter.remove(c.getName().toLowerCase());
        }
        /*
         * Now we build and return the output.
         */
        String output = currentBuffer.toString();
        currentBuffer = null;
        return output;
    }
    
    private static void installBZNetworkSet()
    {
        interpreter.setDisplay(new XDisplayDevice()
        {
            
            @Override
            public void print(String string, boolean newline)
            {
                if (newline
                        && (currentBuffer.length() == 0 || currentBuffer
                                .charAt(0) != ' '))
                    currentBuffer.append(" ");
                currentBuffer.append(string);
                if (newline)
                    currentBuffer.append(" ");
            }
        });
        
    }
    
    private static String escapeXml(String input)
    {
        return input.replace("&", "&amp;").replace(">", "&gt;").replace("<",
                "&lt;");
    }
    
}
