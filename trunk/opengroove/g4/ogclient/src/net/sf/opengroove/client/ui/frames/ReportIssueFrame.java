package net.sf.opengroove.client.ui.frames;
import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ReportIssueFrame extends javax.swing.JFrame {
    private JPanel jPanel1;

    /**
    * Auto-generated main method to display this JFrame
    */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ReportIssueFrame inst = new ReportIssueFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public ReportIssueFrame() {
        super();
        initGUI();
    }
    
    private void initGUI() {
        try {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            {
                jPanel1 = new JPanel();
                TableLayout jPanel1Layout = new TableLayout(new double[][] {{6.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 6.0}, {6.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL}});
                jPanel1Layout.setHGap(5);
                jPanel1Layout.setVGap(5);
                getContentPane().add(jPanel1, BorderLayout.CENTER);
                jPanel1.setLayout(jPanel1Layout);
            }
            pack();
            setSize(400, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
