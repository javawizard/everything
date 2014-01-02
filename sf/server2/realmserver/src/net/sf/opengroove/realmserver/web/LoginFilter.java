package net.sf.opengroove.realmserver.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.opengroove.realmserver.OpenGrooveRealmServer;
import net.sf.opengroove.common.security.Hash;

public class LoginFilter implements Filter
{
    
    public LoginFilter()
    {
    }
    
    @Override
    public void destroy()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void doFilter(ServletRequest sRequest,
        ServletResponse sResponse, FilterChain chain)
        throws IOException, ServletException
    {
        try
        {
            HttpServletRequest request = (HttpServletRequest) sRequest;
            HttpServletResponse response = (HttpServletResponse) sResponse;
            HttpSession session = request.getSession(false);
            if (request.getRequestURI().equals("")
                || request.getRequestURI().equals("/"))
            {
                response
                    .sendRedirect("/bypass/gwt/net.sf.opengroove.realmserver.gwt.AdminInterface/");
                return;
            }
            else if (request.getRequestURI().startsWith(
                "/bypass/"))
            {
                routeNormally(request, response, chain);
                return;
            }
            else if (session == null
                || session.getAttribute("username") == null)
            {
                routeToLogin(request, response, chain);
                return;
            }
            routeNormally(request, response, chain);
        }
        catch (Exception e)
        {
            throw new ServletException(
                "Exception occured during handling in LoginFilter.",
                e);
        }
    }
    
    private void routeToLogin(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException
    {
        if (request.getRequestURI().equals("/authlink"))
        {
            response.sendError(403);
        }
        else
        {
            response.sendRedirect("/");
        }
    }
    
    private void routeNormally(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        chain.doFilter(request, response);
    }
    
    @Override
    public void init(FilterConfig arg0)
        throws ServletException
    {
        // TODO Auto-generated method stub
        
    }
    
}
