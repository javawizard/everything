package jw.bznetwork.client.x.lang;

import java.util.ArrayList;

public class XException extends RuntimeException
{
    private ArrayList<XStackFrame> programStack = new ArrayList<XStackFrame>();
    
    public ArrayList<XStackFrame> getProgramStack()
    {
        return programStack;
    }
    
    public XException()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public XException(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    public XException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    public XException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
