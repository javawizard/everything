package net.sf.opengroove.client.ui.frames;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

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
public class AddingAttachmentDialog extends
    javax.swing.JDialog
{
    private JPanel rootPanel;
    private JProgressBar progress;
    private JLabel mainLabel;
    
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
                AddingAttachmentDialog inst = new AddingAttachmentDialog(
                    frame);
                inst.setVisible(true);
            }
        });
    }
    
    public AddingAttachmentDialog(JFrame frame)
    {
        super(frame);
        initGUI();
        setLocationRelativeTo(frame);
    }
    
    private void initGUI()
    {
        try
        {
            {
                this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            }
            {
                rootPanel = new JPanel();
                TableLayout rootPanelLayout = new TableLayout(
                    new double[][] {
                        { 20.0, TableLayout.FILL, 20.0 },
                        { 20.0, TableLayout.PREFERRED,
                            10.0, 25.0, TableLayout.FILL,
                            20.0 } });
                rootPanelLayout.setHGap(5);
                rootPanelLayout.setVGap(5);
                getContentPane().add(rootPanel,
                    BorderLayout.CENTER);
                rootPanel.setLayout(rootPanelLayout);
                rootPanel
                    .setPreferredSize(new java.awt.Dimension(
                        387, 167));
                {
                    mainLabel = new JLabel();
                    rootPanel.add(mainLabel, "1, 1");
                    mainLabel
                        .setText("Adding attachments...");
                    mainLabel
                        .setHorizontalAlignment(SwingConstants.CENTER);
                    mainLabel.setFont(new java.awt.Font("Dialog",1,24));
                }
                {
                    progress = new JProgressBar();
                    rootPanel.add(getProgress(), "1, 3");
                    progress.setString("Scanning...");
                    progress.setStringPainted(true);
                }
            }
            this.setSize(421, 200);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public JProgressBar getProgress()
    {
        return progress;
    }
    
}
