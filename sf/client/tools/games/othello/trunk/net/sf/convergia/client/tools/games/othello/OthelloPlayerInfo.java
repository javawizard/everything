package net.sf.convergia.client.tools.games.othello;

import java.awt.Color;
import java.io.Serializable;

public class OthelloPlayerInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7185958203237148330L;

	private Color color;

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

}
