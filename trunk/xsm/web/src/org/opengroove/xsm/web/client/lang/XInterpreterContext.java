package org.opengroove.xsm.web.client.lang;

import java.util.HashMap;

public class XInterpreterContext
{
    private XInterpreter interpreter;
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
}
