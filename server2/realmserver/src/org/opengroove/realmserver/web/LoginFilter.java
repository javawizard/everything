package org.opengroove.realmserver.web;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginFilter implements Filter
{
    private Connection db;
    
    public LoginFilter(Connection db)
    {
        this.db = db;
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
        HttpServletRequest request = (HttpServletRequest) sRequest;
        HttpServletResponse response = (HttpServletResponse) sResponse;
        HttpSession session = request.getSession(false);
        if (request.getRequestURI().equals("/login"))
        {
            login(request, response, chain);
        }
        if (request.getRequestURI().startsWith("/bypass/"))
        {
            routeNormally(request, response, chain);
            return;
        }
        else if (session == null)
        {
            routeToLogin(request, response, chain);
            return;
        }
        routeNormally(request, response, chain);
    }
    
    private void login(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain)
    {
        // TODO Auto-generated method stub
        
    }
    
    private void routeToLogin(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException
    {
        request.getRequestDispatcher("/bypass/login.jsp")
            .forward(request, response);
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
