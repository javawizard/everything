package net.sf.opengroove.projects.filleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
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
    private static Hashtable<Class, String> reversePlugins = new Hashtable<Class, String>();
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
        for (Map.Entry<String, Class<? extends FillPlugin>> entry : plugins
            .entrySet())
        {
            reversePlugins.put(entry.getValue(), entry
                .getKey());
        }
        // TODO: replace with an option to create new or load from file
        image = new FillImage();
        image.background = Color.WHITE;
        image.width = 300;
        image.height = 200;
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
        rebuild();
        frame.getContentPane().add(
            new JScrollPane(imageComponent),
            BorderLayout.CENTER);
        frame.setSize(650, 400);
        frame.setLocationRelativeTo(null);
        frame.show();
    }
    
    public static void rebuild()
    {
        buildEditor(controlPanel);
    }
    
    /**
     * Builds the left column editor onto the panel specified.
     * 
     * @param panel
     */
    public static void buildEditor(JPanel panel)
    {
        panel.removeAll();
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
        final JComboBox addType = new JComboBox(plugins
            .keySet().toArray());
        addType.setAlignmentX(0);
        addType.setAlignmentY(0);
        panel.add(addType);
        JButton addRegion = new JButton("Add");
        addRegion.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Region region = new Region();
                region.hide = false;
                try
                {
                    region.plugin = plugins.get(
                        addType.getSelectedItem())
                        .newInstance();
                }
                catch (Exception e1)
                {
                    throw new RuntimeException(e1);
                }
                image.regions.add(region);
                rebuild();
            }
        });
        panel.add(addRegion);
        ButtonGroup pointGroup = new ButtonGroup();
        for (int r = 0; r < image.regions.size(); r++)
        {
            final int regionIndex = r;
            final Region region = image.regions.get(r);
            JPanel regionControls = new JPanel();
            regionControls.setLayout(new BoxLayout(
                regionControls, BoxLayout.X_AXIS));
            regionControls.add(new JLabel("R"
                + (r + 1)
                + "("
                + reversePlugins.get(region.plugin
                    .getClass()) + ")" + ":"));
            JButton regionUp = new JButton(" ↑ ");
            if (r == 0)
                regionUp.setEnabled(false);
            JButton regionDown = new JButton(" ↓ ");
            if ((r + 1) == image.regions.size())
                regionDown.setEnabled(false);
            JButton regionDelete = new JButton(" X ");
            regionUp.setBorder(BorderFactory
                .createLineBorder(Color.GRAY));
            regionDown.setBorder(BorderFactory
                .createLineBorder(Color.GRAY));
            regionDelete.setBorder(BorderFactory
                .createLineBorder(Color.GRAY));
            regionUp.addActionListener(new ActionListener()
            {
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Collections.swap(image.regions,
                        regionIndex, regionIndex - 1);
                    rebuild();
                }
            });
            regionDown
                .addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        Collections.swap(image.regions,
                            regionIndex, regionIndex + 1);
                        rebuild();
                    }
                });
            regionDelete
                .addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        image.regions.remove(regionIndex);
                        rebuild();
                    }
                });
            regionControls.add(regionUp);
            regionControls.add(new JLabel(" "));
            regionControls.add(regionDown);
            regionControls.add(new JLabel(" "));
            regionControls.add(regionDelete);
            regionControls.setAlignmentX(0);
            regionControls.setAlignmentY(0);
            panel.add(regionControls);
            for (int p = 0; p < region.plugin
                .getParameters().length; p++)
            {
                FillParameter parameter = region.plugin
                    .getParameters()[p];
                final int parameterIndex = p;
            }
            JPanel regionPointControls = new JPanel();
            regionPointControls.setLayout(new BoxLayout(
                regionPointControls, BoxLayout.X_AXIS));
            regionPointControls.setAlignmentX(0);
            regionPointControls.setAlignmentY(0);
            regionPointControls.add(new JLabel("Points:"));
            JButton addPointButton = new JButton(" + ");
            addPointButton
                .addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        region.points.add(new Point(0, 0));
                        rebuild();
                    }
                });
            JButton removePointButton = new JButton(" - ");
            removePointButton
                .addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        region.points.remove(region.points
                            .size() - 1);
                        rebuild();
                    }
                });
            removePointButton.setEnabled(region.points
                .size() > 0);
            addPointButton.setBorder(BorderFactory
                .createLineBorder(Color.GRAY));
            removePointButton.setBorder(BorderFactory
                .createLineBorder(Color.GRAY));
            regionPointControls.add(addPointButton);
            regionPointControls.add(new JLabel(" "));
            regionPointControls.add(removePointButton);
            panel.add(regionPointControls);
            for (int p = 0; p < region.points.size(); p++)
            {
                final int pointIndex = p;
                JPanel pointControls = new JPanel();
                pointControls.setLayout(new BoxLayout(
                    pointControls, BoxLayout.X_AXIS));
                pointControls.setAlignmentX(0);
                pointControls.setAlignmentY(0);
                JToggleButton pointButton = new JToggleButton(
                    "Point " + (p + 1));
                pointButton.setBorder(BorderFactory
                    .createLineBorder(Color.GRAY, 1));
                pointGroup.add(pointButton);
                pointControls.add(pointButton);
                pointControls.add(new JLabel(" "));
                JCheckBox bezierCheckbox = new JCheckBox(
                    "bezier");
                pointControls.add(bezierCheckbox);
                panel.add(pointControls);
            }
        }
        panel.invalidate();
        panel.validate();
        panel.repaint();
        frame.invalidate();
        frame.validate();
        frame.repaint();
        panel.invalidate();
        panel.validate();
        panel.repaint();
        imageComponent.repaint();
    }
}
