package jw.bznetwork.client.x.lang;

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
        /*
         * Add an error message in case this isn't within a function and it gets
         * thrown out of the program
         */
        super("Attempted to return a value when not inside a function");
        this.value = returnValue;
    }
    
    public XData getValue()
    {
        return value;
    }
}
