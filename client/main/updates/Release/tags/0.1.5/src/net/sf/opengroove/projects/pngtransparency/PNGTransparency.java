package net.sf.opengroove.projects.pngtransparency;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class PNGTransparency
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        JFileChooser fc = new JFileChooser();
        JFrame f = new JFrame(
            "PNG Transparency - OpenGroove");
        f.setLocationRelativeTo(null);
        f.show();
        if (fc.showOpenDialog(f) != JFileChooser.APPROVE_OPTION)
            System.exit(0);
        File src = fc.getSelectedFile();
        if (fc.showSaveDialog(f) != JFileChooser.APPROVE_OPTION)
            System.exit(0);
        File dest = fc.getSelectedFile();
        BufferedImage image = ImageIO.read(src);
        BufferedImage newImage = new BufferedImage(image
            .getWidth(), image.getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++)
        {
            for (int y = 0; y < image.getHeight(); y++)
            {
                Color at = new Color(image.getRGB(x, y));
                int transparent = new Color(255, 0, 0, 0)
                    .getAlpha();
                if (at.getRed() > 250 && at.getGreen() < 5
                    && at.getBlue() < 5)
                    newImage.setRGB(x, y, transparent);
                else
                    newImage.setRGB(x, y, image
                        .getRGB(x, y));
            }
        }
        ImageIO.write(newImage, dest.getName().substring(
            dest.getName().lastIndexOf(".") + 1), dest);
        System.exit(0);
    }
    
}
