package tests;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import nl.captcha.servlet.DefaultCaptchaIml;

public class Test01
{
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args)
        throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Properties props = new Properties();
        props.setProperty("cap.border", "yes");
        props.setProperty("cap.border.c", "black");
        props.setProperty("cap.char.arr.l", "10");
        DefaultCaptchaIml cap = new DefaultCaptchaIml(props);
        String text = cap.createText();
        System.out.println(text);
        cap.createImage(out, text);
        System.out.println("done");
        Image image = ImageIO
            .read(new ByteArrayInputStream(out
                .toByteArray()));
        JFrame frame = new JFrame();
        frame.getContentPane().add(
            new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.show();
    }
}
