package net.sf.opengroove.client.ui.frames;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

public class ComposeMessageFrame extends javax.swing.JFrame {

    /**
    * Auto-generated main method to display this JFrame
    */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ComposeMessageFrame inst = new ComposeMessageFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public ComposeMessageFrame() {
        super();
        initGUI();
    }
    
    private void initGUI() {
        try {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            pack();
            setSize(400, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
