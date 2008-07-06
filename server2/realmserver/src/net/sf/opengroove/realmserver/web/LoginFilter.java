package net.sf.opengroove.realmserver.web;

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
        HttpServletRequest request = (HttpServletRequest) sRequest;
        HttpServletResponse response = (HttpServletResponse) sResponse;
        HttpSession session = request.getSession(false);
        if (request.getRequestURI().equals("/login"))
        {
            login(request, response, chain);
            return;
        }
        else if (request.getRequestURI().equals("/logout"))
        {
            logout(request, response, chain);
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
    
    private void logout(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain) throws IOException
    {
        request.getSession().removeAttribute("username");
        response.sendRedirect("/");
    }
    
    private void login(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain)
    {
        
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
