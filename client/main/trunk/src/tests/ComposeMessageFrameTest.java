package tests;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

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
public class ComposeMessageFrameTest extends javax.swing.JFrame
{
    private JPanel rootPanel;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JPanel messageTopPanel;
    private JPanel recipientsPanel;
    private JPanel jPanel1;
    private JTextArea messageArea;
    private JTextField subjectField;
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
                ComposeMessageFrameTest inst = new ComposeMessageFrameTest();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public ComposeMessageFrameTest()
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
                rootPanel = new JPanel();
                rootPanel.setBorder(new EmptyBorder(5, 5,
                    5, 5));
                TableLayout rootPanelLayout = new TableLayout(
                    new double[][] {
                        { TableLayout.PREFERRED,
                            TableLayout.FILL },
                        { TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.PREFERRED,
                            TableLayout.FILL,
                            TableLayout.FILL,
                            TableLayout.FILL,
                            TableLayout.FILL } });
                rootPanelLayout.setHGap(5);
                rootPanelLayout.setVGap(5);
                getContentPane().add(rootPanel,
                    BorderLayout.CENTER);
                rootPanel.setLayout(rootPanelLayout);
                {
                    jLabel1 = new JLabel();
                    rootPanel.add(jLabel1, "0, 0");
                    jLabel1.setText("To: ");
                }
                {
                    jLabel2 = new JLabel();
                    rootPanel.add(jLabel2, "0, 1");
                    jLabel2.setText("Subject: ");
                }
                {
                    subjectField = new JTextField();
                    rootPanel.add(subjectField, "1, 1");
                }
                {
                    jLabel3 = new JLabel();
                    rootPanel.add(jLabel3, "0, 2");
                    jLabel3.setText("Message: ");
                }
                {
                    messageTopPanel = new JPanel();
                    BoxLayout messageTopPanelLayout = new BoxLayout(
                        messageTopPanel,
                        javax.swing.BoxLayout.X_AXIS);
                    rootPanel.add(messageTopPanel, "1, 2");
                    messageTopPanel
                        .setLayout(messageTopPanelLayout);
                }
                {
                    messageArea = new JTextArea();
                    rootPanel.add(new JScrollPane(
                        messageArea), "0, 3, 1, 3");
                }
                {
                    jPanel1 = new JPanel();
                    BorderLayout jPanel1Layout = new BorderLayout();
                    jPanel1.setLayout(jPanel1Layout);
                    rootPanel.add(jPanel1, "1, 0");
                    {
                        recipientsPanel = new JPanel();
                        FlowLayout recipientsPanelLayout = new FlowLayout();
                        recipientsPanelLayout.setAlignment(FlowLayout.LEFT);
                        jPanel1.add(recipientsPanel, BorderLayout.CENTER);
                        recipientsPanel.setLayout(recipientsPanelLayout);
                    }
                }
            }
            pack();
            this.setSize(495, 510);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
