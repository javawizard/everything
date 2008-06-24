package tests;

import javax.swing.JFrame;

import net.sf.opengroove.utils.ItemChooser;

public class Test019
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.show();
		System.out
				.println("the user chose "
						+ ItemChooser
								.showItemChooser(
										frame,
										"Choose an item from the items below.",

										new String[]
										{ "c1", "c2", "c3" },
										new String[]
										{
												"<html><b>Choice 1</b><br/>This is some more information<br/>on the first choice.",
												"<html><b>Choice 2</b><br/>This is some more information<br/>on the second choice.",
												"<html><b>Choice 3</b><br/>This is some more information<br/>on the third choice." },
										true));
	}

}
