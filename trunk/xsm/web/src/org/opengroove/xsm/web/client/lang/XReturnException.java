package org.opengroove.xsm.web.client.lang;

/**
 * An exception that the return tag throws. It indicates that the function is to
 * return. This probably isn't the best way to handle returns, but it works, and
 * has the advantage that attempting to return when not in a custom function
 * throws an exception, like it should.
 * 
 * @author Alexander Boyd
 * 
 */
public class XReturnException extends XException
{
    private XData value;
    
    public XReturnException(XData returnValue)
    {
        this.value = returnValue;
    }
    
    public XData getValue()
    {
        return value;
    }
}
