package net.sf.opengroove.realmserver;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.ErrorHandler;

public class DefaultErrorHandler extends ErrorHandler
{

    @Override
    public void handle(String target,
        HttpServletRequest request,
        HttpServletResponse response, int dispatch)
        throws IOException
    {
        response.getWriter().println("<html><body>" +
        		"<b>The page you are looking for could not be found.</b> Try visiting the <a href='/'>home page</a>, or, if you think there is a problem, visit us at <a href='http://www.opengroove.org'>www.opengroove.org</a>.<br/><br/>" +
        				"<small><a href='http://www.opengroove.org'>OpenGroove</a>/<a href='http://www.mortbay.org/jetty-6/'>Jetty</a></small></body></html>");
    }
    
}
