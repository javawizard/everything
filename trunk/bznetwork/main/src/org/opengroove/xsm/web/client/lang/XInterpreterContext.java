package org.opengroove.xsm.web.client.lang;

import java.util.HashMap;

public class XInterpreterContext
{
    private XInterpreter interpreter;
    /**
     * This could be a plain java boolean, except that we want to know if an if
     * statement hasn't yet been executed in this context
     */
    private XBoolean lastIfResult = null;
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
    
    /**
     * Sets the specified variable. If <tt>value</tt> is null, the variable is
     * deleted instead of set.
     * 
     * @param var
     *            The name of the variable
     * @param value
     *            The variable's value
     */
    public void setVariable(String var, XData value)
    {
        if (var == null)
            variables.remove(var);
        else
            variables.put(var, value);
    }
    
    public void validateNotNull(XData data)
    {
        if (data == null)
            throw new XException("An input was expected, but none was given");
    }
    
    /**
     * Gets the last if result, throwing an exception if the if result hasn't
     * been set yet
     * 
     * @return
     */
    public XBoolean getLastIfResult()
    {
        if (lastIfResult == null)
            throw new XException("The if command hasn't been run yet");
        return lastIfResult;
    }
    
    public void setLastIfResult(XBoolean lastIfResult)
    {
        this.lastIfResult = lastIfResult;
    }
}
