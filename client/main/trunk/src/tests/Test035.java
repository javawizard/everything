package tests;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
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
        Shape shape = new Polygon(new int[] {
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400) }, new int[] {
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400),
            (int) (Math.random() * 400) }, 20);
        long start = System.currentTimeMillis();
        for (int x = 0; x < image.getWidth(); x++)
        {
            for (int y = 0; y < image.getWidth(); y++)
            {
                if (shape.contains(x, y))
                    image.setRGB(x, y, transparentRgb);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
