package org.json;

/**
 * The JSONException is thrown by the JSON.org classes then things are amiss.
 * 
 * Modified by javawizard to extend RuntimeException instead of Exception as I'm tired of
 * including needless try/catch blocks in every other method I write.
 * 
 * @author JSON.org
 * @author Alexander Boyd (a.k.a. javawizard)
 * @version 2008-09-18
 */
public class JSONException extends RuntimeException
{
    private Throwable cause;
    
    /**
     * Constructs a JSONException with an explanatory message.
     * 
     * @param message
     *            Detail about the reason for the exception.
     */
    public JSONException(String message)
    {
        super(message);
    }
    
    public JSONException(Throwable t)
    {
        super(t.getMessage());
        this.cause = t;
    }
    
    public Throwable getCause()
    {
        return this.cause;
    }
}
