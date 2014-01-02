package net.sf.opengroove.common.ui;

import com.jidesoft.swing.MultilineLabel;
import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.cert.X509Certificate;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.security.UserFingerprint;

/*
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * This dialog prompts a user whether or not to accept a particular X.509
 * certificate. It is generally used when the certificate is not valid for some
 * reason or other.
 * 
 * @author Alexander Boyd
 */
public class CertCheckFrame extends javax.swing.JDialog
{
    public enum TrustResult
    {
        TRUST, TRUST_ALWAYS, DONT_TRUST
    }
    
    /**
     * Opens up a CertCheckFrame to ask the user if they want to trust the
     * certificate specified.
     * 
     * @param certificate
     *            The certificate to check. This certificate's details will be
     *            shown.
     * @param reasons
     *            The reasons why this certificate is not to be trusted,
     *            separated by newlines. For example, if
     *            {@link X509Certificate#checkValidity()} returns false when
     *            called on the certificate specified, then <code>reasons</code>
     *            could include a message like "This certificate has expired or
     *            is not yet valid".
     * @return A TrustResult, indicating which of the buttons the user pressed
     */
    public static TrustResult checkTrust(Window owner,
        X509Certificate certificate, String reasons)
    {
        CertCheckFrame dialog = new CertCheckFrame(owner);
        dialog.getIssuedToLabel()
            .setText(
                certificate.getSubjectX500Principal()
                    .getName());
        dialog.getIssuerLabel().setText(
            certificate.getIssuerX500Principal().getName());
        dialog.getValidFromLabel().setText(
            certificate.getNotBefore().toString());
        dialog.getValidToLabel().setText(
            certificate.getNotAfter().toString());
        dialog.getCertificateInvalidLabel()
            .setText(reasons);
        dialog.getFingerprintLabel().setText(
            CertificateUtils.fingerprint(certificate));
        dialog.setLocationRelativeTo(owner);
        dialog.show();
        return dialog.result;
    }
    
    private TrustResult result;
    private JPanel topLabelPanel;
    private JLabel topLabel;
    private JButton trustButton;
    private MultilineLabel endInternalLabel;
    private JLabel fingerprintLabel;
    private JLabel certificateInternalFingerprint;
    private JLabel certificateInvalidLabel;
    private MultilineLabel certificateInvalidReasonHeader;
    private JLabel validToLabel;
    private JLabel validFromLabel;
    private JLabel validToInternalLabel;
    private JLabel validFromInternalLabel;
    private JLabel issuerLabel;
    private JLabel issuerInternalLabel;
    private JLabel issuedToLabel;
    private JLabel certificateSubjectLabel;
    private MultilineLabel initialDescriptionLabel;
    private JButton noTrustButton;
    private JButton trustAlwaysButton;
    private JPanel lowerRightPanel;
    private JPanel lowerPanel;
    private JPanel centerPanel;
    
    /**
     * Auto-generated main method to display this JDialog
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame frame = new JFrame();
                CertCheckFrame inst = new CertCheckFrame(
                    frame);
                inst.setVisible(true);
            }
        });
    }
    
    private CertCheckFrame(Window frame)
    {
        super(frame);
        setModal(true);
        initGUI();
    }
    
    private void initGUI()
    {
        try
        {
            {
            }
            BorderLayout thisLayout = new BorderLayout();
            getContentPane().setLayout(thisLayout);
            {
                topLabelPanel = new JPanel();
                BorderLayout topLabelPanelLayout = new BorderLayout();
                getContentPane().add(topLabelPanel,
                    BorderLayout.NORTH);
                topLabelPanel
                    .setLayout(topLabelPanelLayout);
                topLabelPanel.setBorder(BorderFactory
                    .createEmptyBorder(20, 10, 20, 10));
                {
                    topLabel = new JLabel();
                    topLabelPanel.add(topLabel,
                        BorderLayout.CENTER);
                    topLabel.setText("Invalid Certificate");
                    topLabel
                        .setHorizontalAlignment(SwingConstants.CENTER);
                    topLabel.setFont(new java.awt.Font(
                        "Dialog", 1, 28));
                }
            }
            {
                centerPanel = new JPanel();
                TableLayout centerPanelLayout = new TableLayout(
                    new double[][] {
                        { 20.0, 0.35, TableLayout.FILL },
                        { TableLayout.PREFERRED, 7.0,
                            TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.PREFERRED, 7.0,
                            TableLayout.PREFERRED, 7.0,
                            TableLayout.PREFERRED, 7.0,
                            TableLayout.PREFERRED, 7.0,
                            TableLayout.PREFERRED, 7.0,
                            TableLayout.PREFERRED,
                            TableLayout.FILL } });
                centerPanelLayout.setHGap(5);
                centerPanelLayout.setVGap(5);
                centerPanel.setLayout(centerPanelLayout);
                getContentPane().add(centerPanel,
                    BorderLayout.CENTER);
                centerPanel.setBorder(BorderFactory
                    .createEmptyBorder(0, 10, 10, 10));
                {
                    initialDescriptionLabel = new MultilineLabel();
                    centerPanel.add(
                        initialDescriptionLabel,
                        "0, 0, 2, 0");
                    initialDescriptionLabel
                        .setText("The certificate of the server that OpenGroove is connecting to appears to be invalid. Here are the details of the certificate:");
                }
                {
                    certificateSubjectLabel = new JLabel();
                    centerPanel.add(
                        certificateSubjectLabel, "1, 2");
                    certificateSubjectLabel
                        .setText("Issued to:");
                }
                {
                    issuedToLabel = new JLabel();
                    centerPanel.add(issuedToLabel, "2, 2");
                    issuedToLabel.setText("UNKNOWN");
                    issuedToLabel
                        .setFont(new java.awt.Font(
                            "Dialog", 0, 12));
                }
                {
                    issuerInternalLabel = new JLabel();
                    centerPanel.add(issuerInternalLabel,
                        "1, 3");
                    issuerInternalLabel
                        .setText("Issued by:");
                }
                {
                    issuerLabel = new JLabel();
                    centerPanel.add(issuerLabel, "2, 3");
                    issuerLabel.setText("UNKNOWN");
                    issuerLabel.setFont(new java.awt.Font(
                        "Dialog", 0, 12));
                }
                {
                    validFromInternalLabel = new JLabel();
                    centerPanel.add(validFromInternalLabel,
                        "1, 4");
                    validFromInternalLabel
                        .setText("Valid from:");
                }
                {
                    validToInternalLabel = new JLabel();
                    centerPanel.add(validToInternalLabel,
                        "1, 5");
                    validToInternalLabel
                        .setText("Valid until:");
                }
                {
                    validFromLabel = new JLabel();
                    centerPanel.add(getValidFromLabel(),
                        "2, 4");
                    validFromLabel.setText("UNKNOWN");
                    validFromLabel
                        .setFont(new java.awt.Font(
                            "Dialog", 0, 12));
                }
                {
                    validToLabel = new JLabel();
                    centerPanel.add(getValidToLabel(),
                        "2, 5");
                    validToLabel.setText("UNKNOWN");
                    validToLabel.setFont(new java.awt.Font(
                        "Dialog", 0, 12));
                }
                {
                    certificateInvalidReasonHeader = new MultilineLabel();
                    centerPanel.add(
                        certificateInvalidReasonHeader,
                        "0, 7, 2, 7");
                    certificateInvalidReasonHeader
                        .setText("This certificate is invalid because:");
                }
                {
                    certificateInvalidLabel = new JLabel();
                    centerPanel.add(
                        getCertificateInvalidLabel(),
                        "1, 9, 2, 9");
                    certificateInvalidLabel
                        .setText("UNKNOWN");
                    certificateInvalidLabel
                        .setFont(new java.awt.Font(
                            "Dialog", 0, 12));
                }
                {
                    certificateInternalFingerprint = new JLabel();
                    centerPanel.add(
                        certificateInternalFingerprint,
                        "0, 11, 2, 11");
                    certificateInternalFingerprint
                        .setText("The certificate's fingerprint is:");
                }
                {
                    fingerprintLabel = new JLabel();
                    centerPanel.add(getFingerprintLabel(),
                        "1, 13, 2, 13");
                    fingerprintLabel.setText("UNKNOWN");
                    fingerprintLabel
                        .setFont(new java.awt.Font(
                            "Dialog", 0, 12));
                }
                {
                    endInternalLabel = new MultilineLabel();
                    centerPanel.add(endInternalLabel,
                        "0, 15, 2, 15");
                    endInternalLabel
                        .setText("Do you want to trust this certificate? You should only trust it if you are sure that it is authentic. If you're not sure, contact the owner of your realm server and ask them for their server's fingerprint, and make sure that it matches the fingerprint displayed here. If this doesn't help, contact us at support@opengroove.org and we will help you.");
                }
            }
            {
                lowerPanel = new JPanel();
                BorderLayout lowerPanelLayout = new BorderLayout();
                getContentPane().add(lowerPanel,
                    BorderLayout.SOUTH);
                lowerPanel.setLayout(lowerPanelLayout);
                {
                    lowerRightPanel = new JPanel();
                    BoxLayout lowerRightPanelLayout = new BoxLayout(
                        lowerRightPanel,
                        javax.swing.BoxLayout.X_AXIS);
                    lowerPanel.add(lowerRightPanel,
                        BorderLayout.EAST);
                    lowerRightPanel
                        .setLayout(lowerRightPanelLayout);
                    {
                        trustButton = new JButton();
                        trustButton
                            .setToolTipText(ComponentUtils
                                .htmlTipWrap("Trust this certificate for this connection only. "
                                    + "if OpenGroove needs to reconnect, or if you disconnect "
                                    + "from the internet and later reconnect, you will "
                                    + "be prompted again whether to trust this "
                                    + "certificate."));
                        lowerRightPanel.add(trustButton);
                        trustButton.setText("Trust");
                        trustButton
                            .addActionListener(new ActionListener()
                            {
                                
                                public void actionPerformed(
                                    ActionEvent e)
                                {
                                    result = TrustResult.TRUST;
                                    dispose();
                                }
                            });
                    }
                    {
                        trustAlwaysButton = new JButton();
                        trustAlwaysButton
                            .setToolTipText(ComponentUtils
                                .htmlTipWrap("Trust this certificate indefinitely. "
                                    + "You will not be prompted again if you "
                                    + "want to trust this certificate. TODO:"
                                    + " add note that the user can remove via OG/"
                                    + "Options/Certificates tab"));
                        lowerRightPanel
                            .add(trustAlwaysButton);
                        trustAlwaysButton
                            .setText("Trust Always");
                        trustAlwaysButton
                            .addActionListener(new ActionListener()
                            {
                                
                                public void actionPerformed(
                                    ActionEvent e)
                                {
                                    result = TrustResult.TRUST_ALWAYS;
                                    dispose();
                                }
                            });
                    }
                    {
                        noTrustButton = new JButton();
                        noTrustButton
                            .setToolTipText(ComponentUtils
                                .htmlTipWrap("Don't trust this certificate. You will still "
                                    + "be prompted the next time OpenGroove attempts "
                                    + "to connect if this certificate is not trusted."));
                        lowerRightPanel.add(noTrustButton);
                        noTrustButton
                            .setText("Don't Trust");
                        noTrustButton
                            .addActionListener(new ActionListener()
                            {
                                
                                public void actionPerformed(
                                    ActionEvent e)
                                {
                                    result = TrustResult.DONT_TRUST;
                                    dispose();
                                }
                            });
                    }
                }
            }
            this.setSize(475, 609);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public JLabel getIssuedToLabel()
    {
        return issuedToLabel;
    }
    
    public JLabel getIssuerLabel()
    {
        return issuerLabel;
    }
    
    public JLabel getValidFromLabel()
    {
        return validFromLabel;
    }
    
    public JLabel getValidToLabel()
    {
        return validToLabel;
    }
    
    public JLabel getCertificateInvalidLabel()
    {
        return certificateInvalidLabel;
    }
    
    public JLabel getFingerprintLabel()
    {
        return fingerprintLabel;
    }
    
}
