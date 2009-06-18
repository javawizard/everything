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
}
