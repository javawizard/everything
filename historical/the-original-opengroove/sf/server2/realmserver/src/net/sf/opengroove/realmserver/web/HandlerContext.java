package net.sf.opengroove.realmserver.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public class HandlerContext
{
    private HttpServletRequest request;
    private String redirect;
    private String message;
    private HashMap<String, Object> model = new HashMap<String, Object>();
    
    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    public String getRedirect()
    {
        return redirect;
    }
    
    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }
    
    public void setRedirect(String redirect)
    {
        this.redirect = redirect;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
