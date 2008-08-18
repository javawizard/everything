package net.sf.opengroove.client.ui.frames;

import com.jidesoft.swing.JideButton;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.io.File;
import javax.swing.BoxLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.WindowConstants;
import net.sf.opengroove.client.ui.FillContainer;
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
public class LoginFrame extends javax.swing.JFrame
{
    private FillContainer fillContainer;
    private JideButton helpButton;
    private JPanel contents;
    private JLabel iconLabel;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                LoginFrame inst = new LoginFrame();
                inst.getIconLabel().setIcon(
                    new ImageIcon("trayicon48.png"));
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public LoginFrame()
    {
        super();
        initGUI();
    }
    
    private void initGUI()
    {
        try
        {
            BorderLayout thisLayout = new BorderLayout();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            getContentPane().setLayout(thisLayout);
            {
                fillContainer = new FillContainer();
                TableLayout fillContainerLayout = new TableLayout(new double[][] {{12.0, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED}, {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL}});
                fillContainerLayout.setHGap(5);
                fillContainerLayout.setVGap(5);
                getContentPane().add(fillContainer,
                    BorderLayout.CENTER);
                fillContainer
                    .setFillImageName("loginframe");
                fillContainer
                    .setLayout(fillContainerLayout);
                {
                    helpButton = new JideButton();
                    fillContainer.add(helpButton, "3, 0");
                    helpButton.setText("Help");
                    helpButton
                        .setButtonStyle(JideButton.HYPERLINK_STYLE);
                    helpButton.setFont(new java.awt.Font(
                        "Dialog", 0, 12));
                    helpButton
                        .setForeground(new java.awt.Color(
                            0, 0, 255));
                }
                {
                    iconLabel = new JLabel();
                    fillContainer.add(iconLabel, "1, 1");
                }
                {
                    contents = new JPanel();
                    BoxLayout contentsLayout = new BoxLayout(contents, javax.swing.BoxLayout.Y_AXIS);
                    fillContainer.add(contents, "2, 2");
                    contents.setLayout(contentsLayout);
                    contents.setOpaque(false);
                }
            }
            pack();
            this.setSize(335, 199);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public JideButton getHelpButton()
    {
        return helpButton;
    }
    
    public JLabel getIconLabel()
    {
        return iconLabel;
    }
    
}
