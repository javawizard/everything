package org.opengroove.g4.client.ui.frames;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;
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
public class CreateAccountFrame extends javax.swing.JFrame
{
    private JPanel mainPanel;
    private JLabel jLabel1;

    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                CreateAccountFrame inst = new CreateAccountFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public CreateAccountFrame()
    {
        super();
        initGUI();
    }
    
    private void initGUI()
    {
        try
        {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            {
                setSize(400,300);
                mainPanel = new JPanel();
                TableLayout mainPanelLayout = new TableLayout(new double[][] {{6.0, TableLayout.FILL, 6.0, TableLayout.FILL, 6.0}, {6.0, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL}});
                mainPanelLayout.setHGap(5);
                mainPanelLayout.setVGap(5);
                getContentPane().add(mainPanel, BorderLayout.CENTER);
                mainPanel.setLayout(mainPanelLayout);
                {
                    jLabel1 = new JLabel();
                    mainPanel.add(jLabel1, "1, 1, 3, 1");
                    jLabel1.setText("New Account");
                    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
                    jLabel1.setFont(new java.awt.Font("Arial",1,26));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
