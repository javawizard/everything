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
    public String userid;
    public String password;
    public String computer;
}
