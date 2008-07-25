package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ColorConverter
{
    
    /**
     * @param args
     */
    public static void main(String[] args)throws Throwable
    {
        File src = new File(
            "icons/sandbox/presence/user.png");
        BufferedImage image = ImageIO.read(src);
    }
    
    public static void convert(File src, File dest,
        Color srcColor, Color destColor)
    {
        
    }
    
}
