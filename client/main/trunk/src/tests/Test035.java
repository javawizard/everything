package tests;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Test035
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        BufferedImage image = new BufferedImage(400, 400,
            BufferedImage.TYPE_INT_ARGB);
        Color transparent = new Color(0, 0, 0, 0);
        int transparentRgb = transparent.getRGB();
        long start = System.currentTimeMillis();
        for (int x = 0; x < image.getWidth(); x++)
        {
            for (int y = 0; y < image.getWidth(); y++)
            {
                if (x < 70 || y < 70)
                    image.setRGB(x, y, transparentRgb);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
