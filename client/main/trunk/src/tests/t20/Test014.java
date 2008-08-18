package tests.t20;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class Test014
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setSize(400, 300);
		f.setLocationRelativeTo(null);
		Container p = f.getContentPane();
		JPanel p2 = new JPanel();
		p2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		p2.add(createPlayerComponent("testplayer1", false));
		p2.add(createPlayerComponent("testplayer2", true));
		p2.add(createPlayerComponent("testplayer3", false));
		p2.add(createPlayerComponent("testplayer4", false));
		p.add(p2);
		f.show();
		f.invalidate();
	}

	private static JComponent createPlayerComponent(String name,
			boolean isCurrent)
	{
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setAlignmentX(0);
		p.add(new JLabel(name), BorderLayout.CENTER);
		if (isCurrent)
		{
			Border outBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border middleBorder = BorderFactory.createEtchedBorder(
					EtchedBorder.RAISED, new Color(255, 50,50).brighter(), new Color(
							255, 50,50).darker());
			Border inBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border middleOutBorder = BorderFactory.createCompoundBorder(
					outBorder, middleBorder);
			Border border = BorderFactory.createCompoundBorder(middleOutBorder,
					inBorder);
			p.setBorder(border);
		} else
		{
			p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		}
		return p;
	}

}
