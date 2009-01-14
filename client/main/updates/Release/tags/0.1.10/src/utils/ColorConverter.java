package utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorConverter
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        File src = new File("icons/users/user-normal.png");
        final BufferedImage image = ImageIO.read(src);
        final JFrame frame = new JFrame();
        frame.getContentPane()
            .setLayout(new BorderLayout());
        final JSlider slider = new JSlider(0, 360);
        slider.setValue(0);
        slider.setMajorTickSpacing(60);
        slider.setMinorTickSpacing(15);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        Hashtable lTable = new Hashtable<Integer, JLabel>();
        lTable.put(0, new JLabel("Red"));
        lTable.put(30, new JLabel("Orange"));
        lTable.put(60, new JLabel("Yellow"));
        lTable.put(120, new JLabel("Green"));
        lTable.put(180, new JLabel("Cyan"));
        lTable.put(240, new JLabel("Blue"));
        lTable.put(300, new JLabel("Purple"));
        lTable.put(360, new JLabel("Red"));
        slider.setLabelTable(lTable);
        final JSlider satSlider = new JSlider(0, 360);
        satSlider.setValue(360);
        final JSlider lumSlider = new JSlider(0, 360);
        lumSlider.setValue(360);
        final JLabel imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon(setHue(image,
            (slider.getValue() * 1f) / 360, (satSlider
                .getValue() * 1f) / 360, (lumSlider
                .getValue() * 1f) / 360)));
        frame.getContentPane().add(imageLabel,
            BorderLayout.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(slider, BorderLayout.SOUTH);
        panel.add(satSlider, BorderLayout.CENTER);
        panel.add(lumSlider, BorderLayout.NORTH);
        frame.getContentPane().add(panel,
            BorderLayout.SOUTH);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String filename = JOptionPane
                    .showInputDialog(frame,
                        "Choose a name (don't include .png)");
                if (filename == null)
                    return;
                File file = new File("icons/users/"
                    + filename + ".png");
                try
                {
                    ImageIO.write(setHue(image, (slider
                        .getValue() * 1f) / 360, (satSlider
                        .getValue() * 1f) / 360, (lumSlider
                        .getValue() * 1f) / 360), "PNG",
                        file);
                }
                catch (IOException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        frame.getContentPane().add(saveButton,
            BorderLayout.NORTH);
        ChangeListener listener = new ChangeListener()
        {
            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                imageLabel.setIcon(new ImageIcon(setHue(
                    image, (slider.getValue() * 1f) / 360,
                    (satSlider.getValue() * 1f) / 360,
                    (lumSlider.getValue() * 1f) / 360)));
            }
        };
        slider.addChangeListener(listener);
        satSlider.addChangeListener(listener);
        lumSlider.addChangeListener(listener);
        frame.show();
    }
    
    public static BufferedImage setHue(BufferedImage image,
        float hue, float satMul, float lumMul)
    {
        BufferedImage result = new BufferedImage(image
            .getWidth(), image.getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < result.getWidth(); x++)
        {
            for (int y = 0; y < result.getHeight(); y++)
            {
                Color srcColor = new Color(image.getRGB(x,
                    y), true);
                float[] hsb = Color.RGBtoHSB(srcColor
                    .getRed(), srcColor.getGreen(),
                    srcColor.getBlue(), null);
                Color destColor = Color.getHSBColor(hue,
                    hsb[1], hsb[2] * lumMul);
                destColor = new Color(destColor.getRed(),
                    destColor.getGreen(), destColor
                        .getBlue(), srcColor.getAlpha());
                int avg = (destColor.getRed()
                    + destColor.getGreen() + destColor
                    .getBlue()) / 3;
                int rdif = (int) ((destColor.getRed() - avg) * (1 - satMul));
                int gdif = (int) ((destColor.getGreen() - avg) * (1 - satMul));
                int bdif = (int) ((destColor.getBlue() - avg) * (1 - satMul));
                destColor = new Color(destColor.getRed()
                    - rdif, destColor.getGreen() - gdif,
                    destColor.getBlue() - bdif, destColor
                        .getAlpha());
                result.setRGB(x, y, destColor.getRGB());
            }
        }
        return result;
    }
}
