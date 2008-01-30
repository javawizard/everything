package tests;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import net.sf.convergia.client.notification.TaskbarNotification;
import net.sf.convergia.client.notification.TaskbarNotificationFrame;


public class Test012
{

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException
	{
		int currentNumber = 1;
		final TaskbarNotificationFrame frame = new TaskbarNotificationFrame();
		JFrame buttonFrame = new JFrame();
		JToggleButton button = new JToggleButton("add notification");
		buttonFrame.getContentPane().add(button);
		buttonFrame.pack();
		buttonFrame.setLocationRelativeTo(null);
		buttonFrame.show();
		while (true)
		{
			while (!button.isSelected())
			{
				Thread.sleep(200);
			}
			button.setSelected(false);
			button.repaint();
			final int number = currentNumber++;
			final JLabel l = new JLabel("<html>This is notification number " + number
					+ "");
			l.setFont(l.getFont().deriveFont(0));
			l.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
			TaskbarNotification notification = new TaskbarNotification()
			{
				private JLabel label = l;

				private int thisNumber = number;

				public void clicked()
				{
					frame.removeNotification(this);
				}

				public Component getComponent()
				{
					return label;
				}

				public boolean isAlert()
				{
					return false;
				}

				public void mouseOut()
				{
					label.setText("<html>This is notification number " + number
							+ "");
				}

				public void mouseOver()
				{
					label.setText("<html><i>This is notification number "
							+ number + "</i>");
				}
			};
			frame.addNotification(notification, true);
		}
	}

}
