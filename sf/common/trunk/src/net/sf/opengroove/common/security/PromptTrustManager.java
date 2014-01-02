package net.sf.opengroove.common.security;

import java.awt.Window;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import net.sf.opengroove.common.ui.CertCheckFrame;
import net.sf.opengroove.common.ui.CertCheckFrame.TrustResult;
import net.sf.opengroove.common.utils.StringUtils;

/**
 * A TrustManager that will prompt the user if the certificate chain is not
 * valid. It first validates the chain itself by checking that all certificates
 * are properly signed and have dates that are curently valid, and then it
 * checks that the either the root certificate is the one specified (IE it has
 * the same distinguished name and public key), and that the certificate chain
 * only has two members, or that the end certificate is a member of the list of
 * end certificates specified. If none of these is true, then the user is
 * prompted with a dialog box asking them if they want to trust the certificate.
 * If they choose not to trust, an exception is thrown. If they choose to trust
 * for one time only, then the trust method doesn't throw an exception (which
 * indicates to the caller that the certificate is trusted). If the user chooses
 * to trust the certificate permanently, the certificate is added to the
 * endCertificates array list specified, and no exception is thrown. In
 * addition, if the certificate is to be trusted always, and if this trust
 * manager has a TrustAlwaysListener installed, it will be notified.<br/><br/>
 * 
 * Currently, a trust manager can only have one TrustAlwaysListener. In the
 * future, the ability to have multiple listeners will be added.<br/><br/>
 * 
 * Currently, this class only supports validating servers. Client validation
 * will always fail with a CertificateException explaining that it isn't
 * supported.<br/><br/>
 * 
 * Note also that this class will only authenticate RSA keys. Support is planned
 * for other types.<br/><br/>
 * 
 * <b>This trust manager does not validate the realm of a particular server.</b>
 * It's important that the certificate be retrieved from the SSLSocket that this
 * is being used with, and that the certificate be checked to make sure that
 * it's common name matches the name of the realm that is being connected to.
 * 
 * @author Alexander Boyd
 * 
 */
public class PromptTrustManager implements X509TrustManager
{
    private Window dialogOwner;
    private X509Certificate rootCertificate;
    private ArrayList<X509Certificate> endCertificates;
    private TrustAlwaysListener listener;
    
    /**
     * Creates a new OpenGrooveTrustManager with the info specified.
     * 
     * @param dialogOwner
     *            The window that should be the parent of the certificate check
     *            dialog if one needs to be opened. The dialog opened on it will
     *            be modal.
     * @param rootCertificate
     *            The root certifiate to check against
     * @param endCertificates
     *            A list of end certificates to check against
     * @param listener
     *            A listener that will be notified if a certificate is not valid
     *            but the user chooses to trust it indefinitely. The certificate
     *            will be passed into this listener, and this listener should
     *            make sure that the next time it constructs an
     *            OpenGrooveTrustManager, the endCertificates list contains this
     *            certificate.
     */
    public PromptTrustManager(Window dialogOwner,
        X509Certificate rootCertificate,
        ArrayList<X509Certificate> endCertificates,
        TrustAlwaysListener listener)
    {
        this.rootCertificate = rootCertificate;
        this.endCertificates = endCertificates;
        this.listener = listener;
    }
    
    public void checkClientTrusted(X509Certificate[] chain,
        String s) throws CertificateException
    {
        // client auth is not supported right now
        throw new CertificateException(
            "OpenGroove PromptTrustManager doesn't support "
                + "client authentication right now");
    }
    
    public void checkServerTrusted(X509Certificate[] chain,
        String s) throws CertificateException
    {
        try
        {
            if (!s.equals("RSA"))
                throw new CertificateException(
                    "Only the RSA algorithm is supported, the one used is "
                        + s);
            if (chain.length != 1 && chain.length != 2)
                throw new CertificateException(
                    "Currently, only chains of length 1 or 2 are allowed.");
            boolean isSignatureChainValid = CertificateUtils
                .checkSignatureChainValid(chain);
            boolean isChainDateValid = CertificateUtils
                .checkChainDateValid(chain);
            boolean isSignedChain = chain.length == 2;
            X509Certificate endCertificate = chain[0];
            X509Certificate rootCertificate = chain[chain.length - 1];
            boolean rootTrusted = false;
            if (isSignedChain)
            {
                boolean isValidRoot = rootCertificate
                    .getSubjectX500Principal().equals(
                        this.rootCertificate
                            .getSubjectX500Principal())
                    && CertificateUtils
                        .checkSignatureChainValid(new X509Certificate[] {
                            endCertificate,
                            this.rootCertificate });
                if (isValidRoot)
                    rootTrusted = true;
            }
            else
            {
                for (X509Certificate cert : endCertificates)
                {
                    if (CertificateUtils.isEncodedEqual(
                        cert, endCertificate))
                    {
                        rootTrusted = true;
                        break;
                    }
                }
            }
            boolean isTrusted = isChainDateValid
                && isSignatureChainValid && rootTrusted;
            if (isTrusted)
                return;
            /*
             * Something's wrong with the certificate, so we'll show a dialog
             * asking the user what to do.
             */
            ArrayList<String> problemStrings = new ArrayList<String>();
            if (!isChainDateValid)
                problemStrings
                    .add("The certificate has expired or is not yet valid.");
            if (!isSignatureChainValid)
                problemStrings
                    .add("The certificate's signature is invalid.");
            if (!rootTrusted)
                problemStrings
                    .add("The certificate has not been signed by "
                        + "the OpenGroove Certificate Authority.");
            String reason = StringUtils
                .delimited(problemStrings
                    .toArray(new String[0]), "\n");
            TrustResult trustResult = CertCheckFrame
                .checkTrust(dialogOwner, endCertificate,
                    reason);
            if (trustResult == TrustResult.DONT_TRUST)
                throw new CertificateException(
                    "The user chose not to trust the certificate.");
            /*
             * The user chose to trust the certificate. Before returning, we'll
             * notify the TrustAlwaysListener if it's not null and the trust
             * result is TRUST_ALWAYS.
             */
            if (trustResult == TrustResult.TRUST_ALWAYS)
            {
                endCertificates.add(endCertificate);
                if (listener != null)
                    listener.trustAlways(endCertificate);
            }
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
    
    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[] { rootCertificate };
    }
    
}
