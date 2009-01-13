package net.sf.opengroove.projects.filleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractSpinnerModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jidesoft.spinner.PointSpinner;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import net.sf.opengroove.client.storage.Storage;
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
        public FillParameter parameter;
        public Region region;
        public int index;
    }
    
    private static JFrame frame;
    private static JFileChooser filechooser;
    private static Hashtable<String, Class<? extends FillPlugin>> plugins = new Hashtable<String, Class<? extends FillPlugin>>();
    private static Hashtable<Class, String> reversePlugins = new Hashtable<Class, String>();
    private static FillImage image;
    private static JTextField widthField;
    private static JTextField heightField;
    private static Point highlightedPoint;
    private static PointSelection selectedPoint;
    public static final int BOX_WIDTH = 8;
    public static final int BOX_HEIGHT = 8;
    public static final int HALF_BOX_WIDTH = BOX_WIDTH / 2;
    public static final int HALF_BOX_HEIGHT = BOX_HEIGHT / 2;
    public static final Color BOX_INNER_1 = new Color(255,
        0, 0, 160);
    public static final Color BOX_INNER_2 = new Color(255,
        255, 0, 160);
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
    private static PointSpinner pointSpinner;
    private static JSpinner xSpinner;
    private static JSpinner ySpinner;
    
    /**
     * Writes the specified object to the specified file, using the class
     * {@link java.io.ObjectOutputStream}
     * 
     * @param object
     *            The object to write
     * @param file
     *            The file to write the object to
     */
    private static void writeObjectToFile(
        Serializable object, File file)
    {
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(file));
            oos.writeObject(object);
            oos.flush();
            oos.close();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Reads an object from the file specified.
     * 
     * @param file
     *            The file to read an object from
     * @return An object, read from the file specified. Only the first object is
     *         read, so if the file contains multiple objects, they will not be
     *         returned.
     */
    public static Serializable readObjectFromFile(File file)
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file));
            Object object = ois.readObject();
            ois.close();
            return (Serializable) object;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        filechooser = new JFileChooser();
        filechooser.setCurrentDirectory(new File("./backgrounds"));
        filechooser
            .setFileFilter(new FileNameExtensionFilter(
                "FillEditor files", "fdsc"));
        filechooser.setMultiSelectionEnabled(false);
        JFrame testframe = new JFrame(
            "Choose an image - FillEditor - OpenGroove");
        testframe.setLocationRelativeTo(null);
        testframe.show();
        if (filechooser.showOpenDialog(testframe) == JFileChooser.APPROVE_OPTION)
        {
            testframe.dispose();
            image = (FillImage) readObjectFromFile(filechooser
                .getSelectedFile());
        }
        else
        {
            testframe.dispose();
            image = new FillImage();
            image.background = Color.WHITE;
            image.width = 300;
            image.height = 200;
        }
        plugins.put("Dual Gradient", GradientPlugin.class);
        for (Map.Entry<String, Class<? extends FillPlugin>> entry : plugins
            .entrySet())
        {
            reversePlugins.put(entry.getValue(), entry
                .getKey());
        }
        // TODO: replace with an option to create new or load from file
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
        final JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel,
            BoxLayout.X_AXIS));
        final JPanel lowerPanelWrapper = new JPanel(
            new BorderLayout());
        lowerPanelWrapper
            .add(lowerPanel, BorderLayout.WEST);
        frame.getContentPane().add(lowerPanelWrapper,
            BorderLayout.SOUTH);
        xSpinner = new JSpinner(new AbstractSpinnerModel()
        {
            private int value;
            
            @Override
            public Object getNextValue()
            {
                if (value >= image.width)
                    return value;
                return value + 1;
            }
            
            @Override
            public Object getPreviousValue()
            {
                if (value <= 0)
                    return value;
                return value - 1;
            }
            
            @Override
            public Object getValue()
            {
                return value;
            }
            
            @Override
            public void setValue(Object value)
            {
                Point current = getCurrentPosition();
                if (current == null)
                    return;
                if (!(value instanceof Integer))
                    return;
                this.value = (Integer) value;
                if (current != null
                    && current.x != this.value)
                {
                    setCurrentPosition(new Point(
                        this.value, current.y));
                }
                fireStateChanged();
                lowerPanelWrapper.invalidate();
                lowerPanelWrapper.validate();
                lowerPanelWrapper.repaint();
            }
        });
        ySpinner = new JSpinner(new AbstractSpinnerModel()
        {
            private int value;
            
            @Override
            public Object getNextValue()
            {
                if (value >= image.height)
                    return value;
                return value + 1;
            }
            
            @Override
            public Object getPreviousValue()
            {
                if (value <= 0)
                    return value;
                return value - 1;
            }
            
            @Override
            public Object getValue()
            {
                return value;
            }
            
            @Override
            public void setValue(Object value)
            {
                Point current = getCurrentPosition();
                if (current == null)
                    return;
                if (!(value instanceof Integer))
                    return;
                this.value = (Integer) value;
                if (current != null
                    && current.y != this.value)
                {
                    setCurrentPosition(new Point(current.x,
                        this.value));
                }
                fireStateChanged();
                lowerPanelWrapper.invalidate();
                lowerPanelWrapper.validate();
                lowerPanelWrapper.repaint();
            }
        });
        lowerPanel.add(xSpinner);
        lowerPanel.add(ySpinner);
        imageComponent.addMouseListener(new MouseAdapter()
        {
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (selectedPoint != null)
                {
                    Point position = e.getPoint();
                    if (position.x > image.width)
                        position.x = image.width;
                    if (position.y > image.height)
                        position.y = image.height;
                    setCurrentPosition(position);
                }
            }
        });
        rebuild();
        frame.getContentPane().add(
            new JScrollPane(imageComponent),
            BorderLayout.CENTER);
        frame.setSize(650, 400);
        frame.setLocationRelativeTo(null);
        frame
            .setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            
            @Override
            public void windowClosing(WindowEvent e)
            {
                JOptionPane
                    .showMessageDialog(
                        frame,
                        "Closing isn't supported yet. Terminate the program's process to close it.");
            }
        });
        frame.show();
    }
    
    protected static Point getCurrentPosition()
    {
        if (selectedPoint == null)
            return null;
        return (Point) (selectedPoint.isParameter ? (!(selectedPoint.parameter.value instanceof Point) ? new Point(
            0, 0)
            : selectedPoint.parameter.value)
            : selectedPoint.region.points
                .get(selectedPoint.index));
    }
    
    protected static void setCurrentPosition(Point position)
    {
        if (selectedPoint == null)
            return;
        highlightedPoint = position;
        if (selectedPoint.isParameter)
        {
            selectedPoint.parameter.value = position;
        }
        else
        {
            selectedPoint.region.points.set(
                selectedPoint.index, position);
        }
        xSpinner.setValue(position.x);
        ySpinner.setValue(position.y);
        imageComponent.repaint();
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
        selectedPoint = null;
        highlightedPoint = null;
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.Y_AXIS));
        JButton saveButton = new JButton(" save ");
        saveButton.setBorder(BorderFactory
            .createLineBorder(Color.GRAY, 1));
        saveButton.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (filechooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
                {
                    File file = filechooser
                        .getSelectedFile();
                    if (file.getName().indexOf(".") == -1)
                        file = new File(file
                            .getParentFile(), file
                            .getName()
                            + ".fdsc");
                    if (file.exists()
                        && (JOptionPane
                            .showConfirmDialog(
                                frame,
                                "The file "
                                    + file.getName()
                                    + " exists. Overwrite?",
                                null,
                                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION))
                        return;
                    writeObjectToFile(image, file);
                    JOptionPane.showMessageDialog(frame,
                        "Saved.");
                }
            }
        });
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
                        .getColor();
                    imageComponent.repaint();
                }
            });
        panel.add(new JLabel("Regions:"));
        JButton deselectButton = new JButton("de-select");
        final ButtonGroup pointGroup = new ButtonGroup();
        deselectButton
            .addActionListener(new ActionListener()
            {
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    highlightedPoint = null;
                    selectedPoint = null;
                    pointGroup.clearSelection();
                    imageComponent.repaint();
                    highlightedPoint = null;
                    selectedPoint = null;
                }
            });
        deselectButton.setBorder(BorderFactory
            .createLineBorder(Color.GRAY, 1));
        panel.add(deselectButton);
        final JComboBox addType = new JComboBox(plugins
            .keySet().toArray());
        addType.setAlignmentX(0);
        addType.setAlignmentY(0);
        addType.setBorder(BorderFactory.createLineBorder(
            Color.GRAY, 1));
        panel.add(addType);
        JButton addRegion = new JButton(" Add ");
        addRegion.setBorder(BorderFactory.createLineBorder(
            Color.GRAY, 1));
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
            final JCheckBox hideRegion = new JCheckBox(
                "hide");
            final JCheckBox outlineCheckbox = new JCheckBox(
                "outline");
            hideRegion.setSelected(region.hide);
            outlineCheckbox.setSelected(region.outline);
            hideRegion
                .addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        region.hide = hideRegion
                            .isSelected();
                        imageComponent.repaint();
                    }
                });
            outlineCheckbox
                .addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        region.outline = outlineCheckbox
                            .isSelected();
                        imageComponent.repaint();
                    }
                });
            panel.add(hideRegion);
            panel.add(outlineCheckbox);
            for (int p = 0; p < region.plugin
                .getParameters().length; p++)
            {
                final FillParameter parameter = region.plugin
                    .getParameters()[p];
                final int parameterIndex = p;
                if (parameter.type == FillParameter.Type.BOOLEAN)
                {
                    final JCheckBox checkbox = new JCheckBox(
                        parameter.name);
                    checkbox
                        .setToolTipText(parameter.description);
                    checkbox.setSelected(new Boolean(true)
                        .equals(parameter.value));
                    checkbox
                        .addActionListener(new ActionListener()
                        {
                            
                            @Override
                            public void actionPerformed(
                                ActionEvent e)
                            {
                                parameter.value = new Boolean(
                                    checkbox.isSelected());
                                imageComponent.repaint();
                            }
                        });
                    checkbox.setAlignmentX(0);
                    checkbox.setAlignmentY(0);
                    panel.add(checkbox);
                }
                else if (parameter.type == FillParameter.Type.COLOR)
                {
                    Color defaultColor = Color.WHITE;
                    if (parameter.value != null
                        && parameter.value instanceof Color)
                        defaultColor = (Color) parameter.value;
                    final ColorChooserButton chooser = new ColorChooserButton(
                        defaultColor);
                    chooser
                        .addColorChangeListener(new ChangeListener()
                        {
                            
                            @Override
                            public void stateChanged(
                                ChangeEvent e)
                            {
                                parameter.value = chooser
                                    .getColor();
                                imageComponent.repaint();
                            }
                        });
                    JPanel chooserControls = new JPanel();
                    chooserControls
                        .setLayout(new BoxLayout(
                            chooserControls,
                            BoxLayout.X_AXIS));
                    chooserControls.add(new JLabel(
                        parameter.name + " "));
                    chooser
                        .setToolTipText(parameter.description);
                    chooserControls.add(chooser);
                    chooserControls.setAlignmentX(0);
                    panel.add(chooserControls);
                }
                else if (parameter.type == FillParameter.Type.POINT)
                {
                    JToggleButton pointButton = new JToggleButton(
                        " " + parameter.name + " ");
                    pointButton
                        .setToolTipText(parameter.description);
                    pointButton.setBorder(BorderFactory
                        .createLineBorder(Color.GRAY, 1));
                    pointButton
                        .addChangeListener(new ChangeListener()
                        {
                            
                            @Override
                            public void stateChanged(
                                ChangeEvent e)
                            {
                                PointSelection selection = new PointSelection();
                                selection.isParameter = true;
                                selection.parameter = parameter;
                                selection.region = region;
                                selectedPoint = selection;
                                if (parameter.value == null
                                    || !(parameter.value instanceof Point))
                                    parameter.value = new Point(
                                        0, 0);
                                highlightedPoint = (Point) parameter.value;
                                xSpinner
                                    .setValue(highlightedPoint.x);
                                ySpinner
                                    .setValue(highlightedPoint.y);
                                imageComponent.repaint();
                            }
                        });
                    pointGroup.add(pointButton);
                    panel.add(pointButton);
                }
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
                        region.cubicBezier
                            .remove((Integer) region.points
                                .size());
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
                    " Point " + (p + 1) + " ");
                pointButton.setBorder(BorderFactory
                    .createLineBorder(Color.GRAY, 1));
                pointGroup.add(pointButton);
                pointButton
                    .addChangeListener(new ChangeListener()
                    {
                        
                        @Override
                        public void stateChanged(
                            ChangeEvent e)
                        {
                            PointSelection selection = new PointSelection();
                            selection.isParameter = false;
                            selection.region = region;
                            selection.index = pointIndex;
                            selectedPoint = selection;
                            highlightedPoint = region.points
                                .get(pointIndex);
                            xSpinner
                                .setValue(highlightedPoint.x);
                            ySpinner
                                .setValue(highlightedPoint.y);
                            imageComponent.repaint();
                        }
                    });
                pointControls.add(pointButton);
                pointControls.add(new JLabel(" "));
                final JCheckBox bezierCheckbox = new JCheckBox(
                    "bezier");
                bezierCheckbox
                    .setToolTipText("If checked, this point and the next two "
                        + "will be drawn as a bezier curve "
                        + "instead of straight line segments");
                bezierCheckbox
                    .setSelected(region.cubicBezier
                        .contains(p));
                bezierCheckbox
                    .addActionListener(new ActionListener()
                    {
                        
                        @Override
                        public void actionPerformed(
                            ActionEvent e)
                        {
                            if (bezierCheckbox.isSelected()
                                && !region.cubicBezier
                                    .contains(pointIndex))
                                region.cubicBezier
                                    .add((Integer) pointIndex);
                            else
                                region.cubicBezier
                                    .remove(new Integer(
                                        pointIndex));
                            imageComponent.repaint();
                        }
                    });
                pointControls.add(bezierCheckbox);
                panel.add(pointControls);
            }
        }
        panel.invalidate();
        panel.validate();
        panel.repaint();
        frame.getContentPane().invalidate();
        frame.getContentPane().validate();
        frame.getContentPane().repaint();
        panel.invalidate();
        panel.validate();
        panel.repaint();
        imageComponent.repaint();
    }
}
