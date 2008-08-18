package net.sf.opengroove.projects.filleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.opengroove.client.ui.ColorChooserButton;
import net.sf.opengroove.projects.filleditor.plugins.GradientPlugin;

public class FillEditor
{
    public static class PointSelection
    {
        /**
         * If true, then this point is a parameter, and <code>region</code>
         * and <code>parameter</code> apply. If not, this point is a region
         * boundary point, and <code>region</code> and <code>index</code>
         * apply.
         */
        public boolean isParameter;
        public String parameter;
        public Region region;
        public int index;
    }
    
    private static JFrame frame;
    private static Hashtable<String, Class<? extends FillPlugin>> plugins = new Hashtable<String, Class<? extends FillPlugin>>();
    private static FillImage image;
    private static JTextField widthField;
    private static JTextField heightField;
    private static Point highlightedPoint;
    private PointSelection selectedPoint;
    public static final int BOX_WIDTH = 8;
    public static final int BOX_HEIGHT = 8;
    public static final int HALF_BOX_WIDTH = BOX_WIDTH / 2;
    public static final int HALF_BOX_HEIGHT = BOX_HEIGHT / 2;
    public static final Color BOX_INNER_1 = new Color(100,
        100, 100, 160);
    public static final Color BOX_INNER_2 = new Color(255,
        0, 0, 160);
    private static JComponent imageComponent = new JComponent()
    {
        public void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g;
            if (image != null)
                image.draw(g2, image.width, image.height);
            if (highlightedPoint != null)
            {
                // TODO: change to %2000 to enable pulsing
                g2
                    .setColor((System.currentTimeMillis() % 1000) < 1000 ? BOX_INNER_1
                        : BOX_INNER_2);
                g2.fillRect(highlightedPoint.x
                    - HALF_BOX_WIDTH, highlightedPoint.y
                    - HALF_BOX_HEIGHT, BOX_WIDTH,
                    BOX_HEIGHT);
                g2.setColor(new Color(0, 0, 0, 200));
                g2.drawRect(highlightedPoint.x
                    - HALF_BOX_WIDTH, highlightedPoint.y
                    - HALF_BOX_HEIGHT, BOX_WIDTH,
                    BOX_HEIGHT);
            }
        }
        
        public Dimension getPreferredSize()
        {
            if (image == null)
                return new Dimension(0, 0);
            return new Dimension(image.width, image.height);
        }
    };
    
    private static JPanel leftPanel = new JPanel();
    private static JPanel controlPanel = new JPanel();
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        plugins.put("Dual Gradient", GradientPlugin.class);
        // TODO: replace with an option to create new or load from file
        image = new FillImage();
        image.background = Color.WHITE;
        widthField = new JTextField(5);
        heightField = new JTextField(5);
        widthField.setText("" + image.width);
        heightField.setText("" + image.height);
        ActionListener sizeChangeListener = new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    image.width = Integer
                        .parseInt(widthField.getText());
                    image.height = Integer
                        .parseInt(heightField.getText());
                }
                catch (Exception exception)
                {
                    JOptionPane.showMessageDialog(frame,
                        "Invalid size");
                }
                imageComponent.repaint();
                imageComponent.invalidate();
                imageComponent.validate();
                imageComponent.repaint();
                frame.invalidate();
                frame.validate();
                frame.repaint();
            }
        };
        widthField.addActionListener(sizeChangeListener);
        heightField.addActionListener(sizeChangeListener);
        frame = new JFrame("FillEditor - OpenGroove");
        frame.getContentPane()
            .setLayout(new BorderLayout());
        frame.getContentPane().add(
            new JScrollPane(leftPanel), BorderLayout.WEST);
        leftPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        leftPanel.setLayout(new BorderLayout());
        controlPanel = new JPanel();
        leftPanel.add(controlPanel, BorderLayout.NORTH);
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel,
            BoxLayout.X_AXIS));
        frame.getContentPane().add(lowerPanel,
            BorderLayout.SOUTH);
        buildEditor(controlPanel);
        frame.getContentPane().add(
            new JScrollPane(imageComponent),
            BorderLayout.CENTER);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.show();
    }
    
    /**
     * Builds the left column editor onto the panel specified.
     * 
     * @param panel
     */
    public static void buildEditor(JPanel panel)
    {
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.Y_AXIS));
        JButton saveButton = new JButton("save");
        panel.add(saveButton);
        JPanel innerSize = new JPanel();
        innerSize.setLayout(new BorderLayout());
        innerSize.add(widthField, BorderLayout.WEST);
        innerSize.add(heightField, BorderLayout.EAST);
        panel.add(innerSize);
        final ColorChooserButton backgroundChooser = new ColorChooserButton(
            image.background);
        panel.add(backgroundChooser);
        backgroundChooser
            .addColorChangeListener(new ChangeListener()
            {
                
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    image.background = backgroundChooser
                        .getChooser().getColor();
                    imageComponent.repaint();
                }
            });
        panel.add(new JLabel("Regions:"));
        JComboBox addType = new JComboBox(plugins.keySet()
            .toArray());
        addType.setAlignmentX(0);
        addType.setAlignmentY(0);
        panel.add(addType);
        JButton addRegion = new JButton("Add");
        panel.add(addRegion);
        ButtonGroup pointGroup = new ButtonGroup();
        for (int r = 0; r < image.regions.size(); r++)
        {
            Region region = image.regions.get(r);
            panel.add(new JLabel("R" + (r + 1) + ":"));
        }
    }
    
}
