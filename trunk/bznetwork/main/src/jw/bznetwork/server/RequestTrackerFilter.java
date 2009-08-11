package jw.bznetwork.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestTrackerFilter implements Filter
{
    public static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final long ONE_MONTH = 60 * 60 * 24 * 30 * 1;
    private static ThreadLocal<HttpServletRequest> threadLocalRequest = new ThreadLocal<HttpServletRequest>();
    
    @Override
    public void destroy()
    {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (BZNetworkServer.isAccessLocked())
        {
            res.sendError(res.SC_FORBIDDEN, BZNetworkServer
                    .getAccessLockMessage());
            return;
        }
        String url = req.getRequestURI();
        if (url.contains(".cache.") || url.contains("/images/default/shared/")
                || url.contains("/gwt/standard/images/")
                || url.contains("ext-all.css") || url.contains("standard.css")
                || url.contains("xtheme-gray.css"))
        {
            res.setHeader("Cache-control", "public, max-age=" + ONE_MONTH);
            long cMillis = new Date().getTime();
            cMillis += (ONE_MONTH * 1000);
            Date formatDate = new Date(cMillis);
            res.setHeader("Expires", HTTP_DATE_FORMAT.format(formatDate));
        }
        else
        {
            res.setHeader("Cache-control", "no-cache, must-revalidate");
            res.setHeader("Expires", "Fri, 01 Jan 1990 00:00:00 GMT");
        }
        if (req.getSession(false) != null)
        {
            req.getSession().setAttribute("stat-ip-address", req.getRemoteAddr());
            req.getSession().setAttribute("stat-last-access-time",
                    System.currentTimeMillis());
            req.getSession().setAttribute("stat-user-agent", req.getHeader("User-Agent"));
            req.getSession().setMaxInactiveInterval(60 * 120);
            if (BZNetworkServer.getSessionList().get(req.getSession().getId()) == null)
            {
                /*
                 * This fixes a problem where when tomcat serializes sessions
                 * when it's restarting it causes BZNetworkServer to lose track
                 * of those sessions.
                 */
                BZNetworkServer.getSessionList().put(req.getSession().getId(),
                        req.getSession());
            }
        }
        HttpServletRequest oldReq = threadLocalRequest.get();
        threadLocalRequest.set(req);
        chain.doFilter(request, response);
        threadLocalRequest.set(oldReq);
    }
    
    public static HttpServletRequest getCurrentRequest()
    {
        return threadLocalRequest.get();
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }
    
}
