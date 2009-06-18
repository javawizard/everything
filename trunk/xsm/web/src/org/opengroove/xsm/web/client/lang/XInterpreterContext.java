package org.opengroove.xsm.web.client.lang;

import java.util.HashMap;

public class XInterpreterContext
{
    private XInterpreter interpreter;
    private boolean isTopLevel;
    private HashMap<String, XData> variables = new HashMap<String, XData>();
    
    public XInterpreter getInterpreter()
    {
        return interpreter;
    }
    
    public void setInterpreter(XInterpreter interpreter)
    {
        this.interpreter = interpreter;
    }
    
    public HashMap<String, XData> getVariables()
    {
        return variables;
    }
    
    public boolean isTopLevel()
    {
        return isTopLevel;
    }
    
    public void setTopLevel(boolean isTopLevel)
    {
        this.isTopLevel = isTopLevel;
    }
    
    public XInterpreterContext(XInterpreter interpreter, boolean isTopLevel)
    {
        super();
        this.interpreter = interpreter;
        this.isTopLevel = isTopLevel;
    }
    
    /**
     * Executes the specified command in this context, returning its return
     * value.
     * 
     * @param singleElement
     *            The element
     * @return The value that the command returned
     */
    public XData execute(XElement element)
    {
        return interpreter.execute(element, this);
    }
    
    /**
     * Calls the specified command, which must return a string. If it does not
     * (including when it doesn't return or it returns XNull), an exception will
     * be thrown.
     * 
     * @param element
     * @return
     */
    public String executeForString(XElement element)
    {
        return ((XString) execute(element)).getValue();
    }
    
    /**
     * Gets the specified variable, throwing an exception if the variable
     * doesn't exist (but successfully returning XNull if that is the variable's
     * current value)
     * 
     * @param name
     *            The name of the variable
     * @return
     */
    public XData getVariable(String name)
    {
        if (variables.get(name) == null)
            throw new XException("The variable " + name + " doesn't exist");
        return variables.get(name);
    }
}
