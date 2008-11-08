package net.sf.opengroove.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.sf.opengroove.client.com.ServerSecurityKey;
import net.sf.opengroove.client.storage.TrustedCertificate;

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
     * The security key for this user's realm. If the realm server is listed in
     * the public server directory, then this is the security key obtained from
     * there. If not, the user will be prompted to select a .ogvs file to use as
     * their server's security key.
     */
    public ServerSecurityKey serverKey;
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
    public BigInteger encPub;
    public BigInteger encMod;
    public BigInteger encPrv;
    public BigInteger sigPub;
    public BigInteger sigMod;
    public BigInteger sigPrv;
    /**
     * True if the wizard completed by successfully creating the account (in
     * which case we show a login screen for that account when the wizard is
     * closed), false if it did not
     */
    public boolean finishedWizard;
    protected List<TrustedCertificate> trustedCerts = new ArrayList<TrustedCertificate>();
}
