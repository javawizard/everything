package net.sf.opengroove.common.ui;

import com.jidesoft.swing.MultilineLabel;
import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class CertCheckFrame extends javax.swing.JDialog
{
    private JPanel topLabelPanel;
    private JLabel topLabel;
    private JButton trustButton;
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
    
    public CertCheckFrame(JFrame frame)
    {
        super(frame);
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
                    .createEmptyBorder(20, 10, 10, 10));
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
                TableLayout centerPanelLayout = new TableLayout(new double[][] {{20.0, TableLayout.FILL, TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.FILL}});
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
                    centerPanel.add(certificateSubjectLabel, "1, 2");
                    certificateSubjectLabel.setText("Issued to:");
                }
                {
                    issuedToLabel = new JLabel();
                    centerPanel.add(getIssuedToLabel(), "2, 2");
                    issuedToLabel.setText("UNKNOWN");
                }
                {
                    issuerInternalLabel = new JLabel();
                    centerPanel.add(issuerInternalLabel, "1, 3");
                    issuerInternalLabel.setText("Issued by:");
                }
                {
                    issuerLabel = new JLabel();
                    centerPanel.add(getIssuerLabel(), "2, 3");
                    issuerLabel.setText("UNKNOWN");
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
                                .htmlTipWrap("Trusts this certificate for this connection only. "
                                    + "if OpenGroove needs to reconnect, or you disconnect "
                                    + "from the internet and later reconnect, you will "
                                    + "be prompted again whether to trust this "
                                    + "certificate."));
                        lowerRightPanel.add(trustButton);
                        trustButton.setText("Trust");
                    }
                    {
                        trustAlwaysButton = new JButton();
                        trustAlwaysButton
                            .setToolTipText(ComponentUtils
                                .htmlTipWrap("Trusts this certificate indefinitely. "
                                    + "You will not be prompted again if you "
                                    + "want to trust this certificate."));
                        lowerRightPanel
                            .add(trustAlwaysButton);
                        trustAlwaysButton
                            .setText("Trust Always");
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
                    }
                }
            }
            this.setSize(475, 408);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public JLabel getIssuedToLabel() {
        return issuedToLabel;
    }
    
    public JLabel getIssuerLabel() {
        return issuerLabel;
    }

}
