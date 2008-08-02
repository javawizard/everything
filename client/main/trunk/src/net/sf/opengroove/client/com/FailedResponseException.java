package net.sf.opengroove.client.com;

public class FailedResponseException extends
    RuntimeException
{
    private String responseCode;
    
    /**
     * Gets the response code associated with this exception.
     */
    public String getResponseCode()
    {
        return responseCode;
    }
    
    public FailedResponseException(String responseCode)
    {
        this.responseCode = responseCode;
    }
    
    public FailedResponseException(String responseCode,
        String message)
    {
        super(message);
        this.responseCode = responseCode;
    }
    
    public FailedResponseException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    public FailedResponseException(String message,
        Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
}
