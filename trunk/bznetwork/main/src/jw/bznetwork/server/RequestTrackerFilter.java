package jw.bznetwork.server;

import java.io.IOException;

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
        if (BZNetworkServer.isAccessLocked())
        {
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendError(res.SC_FORBIDDEN, BZNetworkServer
                    .getAccessLockMessage());
            return;
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
