package jw.bznetwork.client.x;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XNull;
import jw.bznetwork.client.x.lang.XString;

public class VXProvider implements XCommand
{
    
    @Override
    public String getName()
    {
        return "provider";
    }
    
    @Override
    public XData invoke(XInterpreterContext context, XElement element)
    {
        if (BZNetwork.currentUser == null)
            return new XNull();
        return new XString(BZNetwork.currentUser.getProvider());
    }
    
}
