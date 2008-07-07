package net.sf.opengroove.realmserver.handlers;

import java.util.ArrayList;

import net.sf.opengroove.realmserver.web.Handler;
import net.sf.opengroove.realmserver.web.HandlerContext;

public class Welcome implements Handler
{
    
    @Override
    public void handle(HandlerContext context)
    {
        ArrayList<String> items = new ArrayList<String>();
        items.add("This is the first item.");
        items.add("This is the second item.");
        if (items.size() > 0)
        {
            context.getRequest().setAttribute(
                "hasNeededItems", new Boolean(true));
            context.getRequest().setAttribute(
                "neededItems", items);
        }
    }
    
}
