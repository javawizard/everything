package net.sf.opengroove.common;

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
                    topLabelPanel.add(topLabel, BorderLayout.CENTER);
                    topLabel.setText("Invalid Certificate");
                    topLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    topLabel.setFont(new java.awt.Font("Dialog",1,28));
                }
            }
            {
                centerPanel = new JPanel();
                getContentPane().add(centerPanel, BorderLayout.CENTER);
            }
            {
                lowerPanel = new JPanel();
                BorderLayout lowerPanelLayout = new BorderLayout();
                getContentPane().add(lowerPanel, BorderLayout.SOUTH);
                lowerPanel.setLayout(lowerPanelLayout);
                {
                    lowerRightPanel = new JPanel();
                    BoxLayout lowerRightPanelLayout = new BoxLayout(lowerRightPanel, javax.swing.BoxLayout.X_AXIS);
                    lowerPanel.add(lowerRightPanel, BorderLayout.EAST);
                    lowerRightPanel.setLayout(lowerRightPanelLayout);
                    {
                        trustButton = new JButton();
                        lowerRightPanel.add(trustButton);
                        trustButton.setText("Trust");
                    }
                    {
                        trustAlwaysButton = new JButton();
                        lowerRightPanel.add(trustAlwaysButton);
                        trustAlwaysButton.setText("Trust Always");
                    }
                    {
                        noTrustButton = new JButton();
                        lowerRightPanel.add(noTrustButton);
                        noTrustButton.setText("Don't Trust");
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
    
}
