package net.sf.opengroove.client.ui.frames;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

public class SearchForUsersFrame extends javax.swing.JFrame {

    /**
    * Auto-generated main method to display this JFrame
    */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SearchForUsersFrame inst = new SearchForUsersFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public SearchForUsersFrame() {
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
