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
import net.sf.opengroove.security.Hash;

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
            if (request.getRequestURI().equals("/login"))
            {
                login(request, response, chain);
                return;
            }
            else if (request.getRequestURI().equals(
                "/logout"))
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
        catch (Exception e)
        {
            throw new ServletException(
                "Exception occured during handling in LoginFilter.",
                e);
        }
    }
    
    private void logout(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain)
        throws IOException
    {
        request.getSession().removeAttribute("username");
        request.getSession().invalidate();
        response.sendRedirect("/");
    }
    
    private void login(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain)
        throws SQLException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HashMap map = new HashMap();
        map.put("username", username);
        String hash = Hash.hash(password);
        map.put("password", hash);
        String role = (String) OpenGrooveRealmServer.pdbclient
            .queryForObject("authenticateWebUser", map);
        if (role == null || !role.equals("admin"))// failed authentication
        {
            response
                .sendRedirect("/bypass/login.jsp?errormessage="
                    + URLEncoder
                        .encode("Incorrect username and/or password."));
            return;
        }
        request.getSession().setAttribute("username",
            username);
        request.getSession().setAttribute("role", role);
        response.sendRedirect("/layout");
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
