package net.sf.opengroove.client;

/**
 * This class contains variables related to the new account wizard. An instance
 * of it is created whenever the user chooses to show the new account wizard,
 * and it persists for the life of the wizard. Ideally, all of the fields on
 * this class would be replaced by non-final local variables within the method
 * in the OpenGroove class that shows the new account wizard, but since java
 * doesn't allow non-final local variables to be accessed from within anonymous
 * inner classes, this will have to suffice.
 * 
 * @author Alexander Boyd
 * 
 */
public class NewAccountWizardVars
{
    /**
     * The userid that the user has chosen to use
     */
    public String userid;
    /**
     * The password that the user has entered, in plain text
     */
    public String password;
    /**
     * True if a connection has been established to this user's realm to see if
     * the username and password are correct, with the result that they are,
     * false otherwise
     */
    public boolean useridValidated = false;
    /**
     * The name of the computer that this user has chosen to create
     */
    public String computer;
    /**
     * True if a connection has been established to this user's realm to ensure
     * that the computer name specified does not already exist, with the result
     * that it does not, false otherwise
     */
    public boolean computerValidated = false;
}
