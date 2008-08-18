package tests.t20;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import com.elevenworks.swing.panel.SimpleGradientPanel;

public class Test002
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// a simple test for painting a panel with a gradient.
		JFrame frame = new JFrame();
		SimpleGradientPanel p = new SimpleGradientPanel(
				new Color(180, 200, 255), new Color(245, 249, 255),
				SimpleGradientPanel.VERTICAL);
		frame.getContentPane().add(new JScrollPane(p));
		Border emptyBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setBorder(emptyBorder);
		p.setLayout(new BorderLayout());
		p2.setOpaque(false);
		p.add(p2);
		for (int i = 1; i < 21; i++)
		{
			JLabel label = new JLabel("label " + i);
			label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			p2.add(label);
		}
		frame.show();
		frame.setSize(300, 500);
		frame.setLocationRelativeTo(null);
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}
}
