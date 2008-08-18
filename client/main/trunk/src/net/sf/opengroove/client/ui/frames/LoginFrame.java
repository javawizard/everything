package net.sf.opengroove.client.ui.frames;
import java.awt.BorderLayout;

import javax.swing.WindowConstants;
import net.sf.opengroove.client.ui.FillContainer;
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
public class LoginFrame extends javax.swing.JFrame {
    private FillContainer fillContainer;

    /**
    * Auto-generated main method to display this JFrame
    */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginFrame inst = new LoginFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public LoginFrame() {
        super();
        initGUI();
    }
    
    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            getContentPane().setLayout(thisLayout);
            {
                fillContainer = new FillContainer();
                getContentPane().add(fillContainer, BorderLayout.CENTER);
                fillContainer.setFillImageName("loginframe");
            }
            pack();
            this.setSize(335, 199);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
