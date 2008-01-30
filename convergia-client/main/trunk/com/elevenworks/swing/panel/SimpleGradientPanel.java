package com.elevenworks.swing.panel;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JPanel that can draw either a vertical or horizontal gradient.
 * <p/>
 * Copyright (C) 2005 by Jon Lipsky
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software d
 * istributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SimpleGradientPanel extends JPanel
{
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;

	/**
	 * The start color of the gradient
	 */
	private Color startColor = Color.white;

	/**
	 * The end color of the gradient
	 */
	private Color endColor = UIManager.getColor("control");

	/**
	 * The direction of the gradient
	 */
	private int direction = VERTICAL;

	// ------------------------------------------------------------------------------------------------------------------
	//  Constructors and getter/setter methods
	// ------------------------------------------------------------------------------------------------------------------

	/**
	 * Create a default SimpleGradientPanel instance.
	 */
	public SimpleGradientPanel()
	{
		super();
	}

	/**
	 * Create a SimpleGradientPanel with the given start and end colors.
	 *
	 * @param aStart The start color for the gradient.
	 * @param aEnd   The end color for the gradient.
	 */
	public SimpleGradientPanel(Color aStart, Color aEnd)
	{
		super();
		startColor = aStart;
		endColor = aEnd;
	}

	/**
	 * Create a SimpleGradientPanel with the given start and end colors.
	 *
	 * @param aStart 		The start color for the gradient.
	 * @param aEnd   		The end color for the gradient.
	 * @param aDirection The direction of the gradient.
	 */
	public SimpleGradientPanel(Color aStart, Color aEnd, int aDirection)
	{
		super();
		startColor = aStart;
		endColor = aEnd;
		direction = aDirection;
	}

	/**
	 * Get the ending color of the gradient.
	 */
	public Color getEndColor()
	{
		return endColor;
	}

	/**
	 * Set the ending color to use.
	 *
	 * @param aColor The color to use.
	 */
	public void setEndColor(Color aColor)
	{
		Color oldEndColor = endColor;
		endColor = aColor;
		super.firePropertyChange("endColor",oldEndColor,endColor);
		repaint();
	}

	/**
	 * Get the start color of the gradient
	 */
	public Color getStartColor()
	{
		return startColor;
	}

	/**
	 * Set the starting color.
	 *
	 * @param aColor The color to use
	 */
	public void setStartColor(Color aColor)
	{
		Color oldStartColor = endColor;
		startColor = aColor;
		super.firePropertyChange("startColor",oldStartColor,startColor);
		repaint();
	}

	/**
	 * Get the direction (vertical or horizontal) of the gradient.
	 */
	public int getDirection()
	{
		return direction;
	}

	/**
	 * Set the direction
	 *
	 * @param aDirection The direction of the gradient
	 */
	public void setDirection(int aDirection)
	{
		int oldDirection = direction;
		direction = aDirection;
		super.firePropertyChange("direction",oldDirection,aDirection);
		repaint();
	}

	// ------------------------------------------------------------------------------------------------------------------
	//  Custom painting methods
	// ------------------------------------------------------------------------------------------------------------------

	/**
	 * Override the default paintComponent method to paint the gradient in the panel.
	 *
	 * @param g
	 */
	public void paintComponent(Graphics g)
	{
		Dimension dim = getSize();
		Graphics2D g2 = (Graphics2D) g;
		Insets inset = getInsets();
		int vWidth = dim.width - (inset.left + inset.right);
		int vHeight = dim.height - (inset.top + inset.bottom);

		if (direction == HORIZONTAL)
		{
			paintHorizontalGradient(g2, inset.left, inset.top, vWidth, vHeight, dim.width);
		}
		else
		{
			paintVerticalGradient(g2, inset.left, inset.top, vWidth, vHeight, dim.height);
		}
	}

	/**
	 * Paints a vertical gradient background from the start color to the end color.
	 */
	private void paintVerticalGradient(Graphics2D g2, int x, int y, int w, int h, int height)
	{
		g2.setPaint(new GradientPaint(0, y, startColor, 0, height, endColor));
		g2.fillRect(x, y, w, h);
	}

	/**
	 * Paints a horizontal gradient background from the start color to the end color.
	 */
	private void paintHorizontalGradient(Graphics2D g2, int x, int y, int w, int h, int width)
	{
		g2.setPaint(new GradientPaint(x, 0, startColor, width, 0, endColor));
		g2.fillRect(x, y, w, h);
	}
}

