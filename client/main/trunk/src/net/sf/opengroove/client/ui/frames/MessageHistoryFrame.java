package net.sf.opengroove.client.ui.frames;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

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
public class MessageHistoryFrame extends javax.swing.JFrame
{
    private JPanel rootPanel;
    private JPanel lowerPanel;
    private JPanel toolbarPanel;
    private JTextField searchField;
    private JLabel searchHeaderLabel;
    private JScrollPane tableScrollPane;
    private JTable table;
    private JLabel toolbarSpacerLabel;
    private JButton openButton;
    private JPanel contentPanel;
    private JButton okButton;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                MessageHistoryFrame inst = new MessageHistoryFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }
    
    public MessageHistoryFrame()
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
                BorderLayout rootPanelLayout = new BorderLayout();
                rootPanel.setLayout(rootPanelLayout);
                rootPanel.setBorder(new EmptyBorder(10, 10,
                    10, 10));
                getContentPane().add(rootPanel,
                    BorderLayout.CENTER);
                {
                    lowerPanel = new JPanel();
                    BorderLayout lowerPanelLayout = new BorderLayout();
                    rootPanel.add(lowerPanel,
                        BorderLayout.SOUTH);
                    lowerPanel.setLayout(lowerPanelLayout);
                    {
                        okButton = new JButton();
                        lowerPanel.add(okButton,
                            BorderLayout.EAST);
                        okButton.setText("OK");
                    }
                }
                {
                    contentPanel = new JPanel();
                    TableLayout contentPanelLayout = new TableLayout(new double[][] {{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL, 5.0}});
                    contentPanelLayout.setHGap(5);
                    contentPanelLayout.setVGap(5);
                    rootPanel.add(contentPanel,
                        BorderLayout.CENTER);
                    contentPanel
                        .setLayout(contentPanelLayout);
                    {
                        toolbarPanel = new JPanel();
                        BoxLayout toolbarPanelLayout = new BoxLayout(
                            toolbarPanel,
                            javax.swing.BoxLayout.X_AXIS);
                        toolbarPanel
                            .setLayout(toolbarPanelLayout);
                        contentPanel.add(toolbarPanel,
                            "0, 0");
                        {
                            openButton = new JButton();
                            toolbarPanel.add(openButton);
                            openButton.setText("Open");
                        }
                        {
                            toolbarSpacerLabel = new JLabel();
                            toolbarPanel
                                .add(toolbarSpacerLabel);
                            toolbarSpacerLabel
                                .setMaximumSize(new java.awt.Dimension(
                                    1000000, 1000000));
                        }
                        {
                            searchHeaderLabel = new JLabel();
                            toolbarPanel
                                .add(searchHeaderLabel);
                            searchHeaderLabel
                                .setText("Search: ");
                        }
                        {
                            searchField = new JTextField(12);
                            searchField
                                .setMaximumSize(searchField
                                    .getPreferredSize());
                            toolbarPanel.add(searchField);
                        }
                    }
                    {
                        tableScrollPane = new JScrollPane();
                        contentPanel.add(tableScrollPane, "0, 1");
                        {
                            TableModel tableModel = 
                                new DefaultTableModel(
                                    new String[][] { { "One", "Two" }, { "Three", "Four" } },
                                    new String[] { "Column 1", "Column 2" });
                            table = new JTable();
                            tableScrollPane.setViewportView(table);
                            table.setModel(tableModel);
                        }
                    }
                }
            }
            pack();
            this.setSize(687, 606);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
