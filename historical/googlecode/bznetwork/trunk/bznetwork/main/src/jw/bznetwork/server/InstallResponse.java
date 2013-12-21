package jw.bznetwork.server;

public class InstallResponse
{
    /**
     * True to show a continue button, false not to
     */
    private boolean showContinueButton;
    /**
     * A message to show to the user, which can contain html
     */
    private String message;
    public InstallResponse(String continueAddParameter, String message,
            boolean showContinueButton)
    {
        super();
        this.continueAddParameter = continueAddParameter;
        this.message = message;
        this.showContinueButton = showContinueButton;
    }

    /**
     * If showContinueButton is true, the name of a parameter (whose value will
     * be "true") to add to the request upon continuing, to indicate that
     * whatever warning this page represents has already been read
     */
    private String continueAddParameter;
    
    public boolean isShowContinueButton()
    {
        return showContinueButton;
    }
    
    public void setShowContinueButton(boolean showContinueButton)
    {
        this.showContinueButton = showContinueButton;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public String getContinueAddParameter()
    {
        return continueAddParameter;
    }
    
    public void setContinueAddParameter(String continueAddParameter)
    {
        this.continueAddParameter = continueAddParameter;
    }
}
