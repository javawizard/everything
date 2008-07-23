package net.sf.opengroove.realmserver.handlers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import net.sf.opengroove.realmserver.web.Handler;
import net.sf.opengroove.realmserver.web.HandlerContext;

public class Help implements Handler
{
    
    @Override
    public void handle(HandlerContext context)
    {
        File helpFolder = new File(
            "web/pages/help/contents");
        String[] files = helpFolder
            .list(new FilenameFilter()
            {
                
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith(".jsp");
                }
            });
        Map<String, String>[] maps = new HashMap[files.length];
        for (int i = 0; i < maps.length; i++)
        {
            maps[i] = new HashMap<String, String>();
            maps[i].put("title", files[i].replace("_", " ")
                .substring(0,
                    files[i].length() - ".jsp".length()));
            maps[i].put("page", files[i]);
        }
        context.getRequest()
            .setAttribute("helpFiles", maps);
    }
}
