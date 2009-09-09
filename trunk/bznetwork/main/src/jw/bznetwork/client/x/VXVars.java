package jw.bznetwork.client.x;

import java.util.Collection;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XList;
import jw.bznetwork.client.x.lang.XString;

public class VXVars implements XCommand
{
    
    @Override
    public String getName()
    {
        return "vars";
    }
    
    @Override
    public XData invoke(XInterpreterContext context, XElement element)
    {
        Collection<String> varNames = context.getVariables().keySet();
        XList list = new XList();
        for (String s : varNames)
            list.add(new XString(s));
        return list;
    }
    
}
