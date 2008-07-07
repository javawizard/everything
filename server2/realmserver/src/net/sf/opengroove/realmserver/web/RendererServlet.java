package net.sf.opengroove.realmserver.web;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RendererServlet extends HttpServlet
{
    /**
     * map of handlers by url component
     */
    private Map<String, Handler> handlers = new LinkedHashMap<String, Handler>();
    /**
     * map of page paths (within web/pages) by url component
     */
    private Map<String, String> pages = new LinkedHashMap<String, String>();
    /**
     * map of parent tab names by page url
     */
    private Map<String, String> parentTabs = new LinkedHashMap<String, String>();
    /**
     * map of tab url components by tab name
     */
    private Map<String, String> tabs = new LinkedHashMap<String, String>();
    /**
     * the page url of the default page, or null if none was specified
     */
    private String defaultTab = null;
    
    public RendererServlet(String descriptor)
        throws IOException
    {
        String[] components = descriptor
            .split("\\[next\\]");
        for (String component : components)
        {
            component = component.trim();
            if (component.equals(""))
                continue;
            Properties props = new Properties();
            props.load(new StringReader(component));
            String name = props.getProperty("name");
            String defaultPage = props
                .getProperty("default");
            tabs.put(name, defaultPage);
            for (String prop : (Set<? extends String>) props
                .keySet())
            {
                if (!prop.endsWith(".page"))
                    continue;
                String page = props.getProperty(prop);
                String url = prop.substring(0, prop
                    .length() - 5);
                if (props.getProperty(url + ".default") != null)
                {
                    defaultTab = url;
                }
                String handlerClass = props.getProperty(url
                    + ".handler");
                // System.out.println("adding page with url "
                // + url + " and page " + page);
                pages.put(url, page);
                parentTabs.put(url, name);
                if (handlerClass != null)
                {
                    try
                    {
                        handlers.put(url, (Handler) Class
                            .forName(handlerClass)
                            .newInstance());
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(
                            "The handler for page " + url
                                + " couldn't be loaded.", e);
                    }
                }
            }
        }
    }
    
    @Override
    protected void service(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException
    {
        try
        {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            String url = request.getPathInfo();
            if (url == null)
                url = "/";
            if (url.length() > 0)
                url = url.substring(1);
            url = url.trim();
            // System.err.println("url is " + url);
            if (url.trim().equals("") && defaultTab != null)
            {
                response.sendRedirect(request
                    .getServletPath()
                    + "/" + defaultTab);
                return;
            }
            String page = pages.get(url);
            if (page == null)
            {
                response.sendError(response.SC_NOT_FOUND);
                return;
            }
            Handler handler = handlers.get(url);
            HandlerContext context = new HandlerContext();
            context.setRequest(request);
            if (handler != null)
                handler.handle(context);
            if (context.getRedirect() != null)
            {
                response.sendRedirect(request
                    .getServletPath()
                    + "/" + context.getRedirect());
                return;
            }
            HashMap<String, String> actions = new HashMap<String, String>();
            for (String pageurl : pages.keySet())
            {
                actions.put(pageurl, request
                    .getServletPath()
                    + "/" + pageurl);
            }
            request.setAttribute("actions", actions);
            // System.out.println("tabcount: " + tabs.size());
            request.setAttribute("tabs", tabs);
            request.setAttribute("pageName", parentTabs
                .get(url));
            request.setAttribute("selectedTab", parentTabs
                .get(url));
            request.setAttribute("rendererPath", request
                .getServletPath());
            if (context.getMessage() != null)
                request.setAttribute("alertMessage",
                    context.getMessage());
            request.setAttribute("page", pages.get(url));
            request.getRequestDispatcher("/renderer.jsp")
                .forward(request, response);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }
    
}
