package net.sf.opengroove.client.tools.games.pente;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

import net.sf.opengroove.client.tools.games.multiplayer.MultiplayerSequenceGame;
import net.sf.opengroove.client.tools.games.othello.OthelloPlayerInfo;


public class PenteTool extends MultiplayerSequenceGame
{
	private static final Color[] initialColors = new Color[]
	{ Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW, Color.ORANGE,
			Color.MAGENTA, Color.CYAN, Color.GRAY };

	@Override
	protected Serializable createPlayerMetadata()
	{
		JColorChooser cc = new JColorChooser();
		Color iColor = initialColors[getGameParticipants().length % initialColors.length];
		Color color = null;
		while (color == null)
		{
			color = cc.showDialog(getWrapper().getWorkspace().getFrame(),
					"Choose your bead color",
					iColor);
		}
		OthelloPlayerInfo info = new OthelloPlayerInfo();
		System.out.println("setting color " + color);
		info.setColor(color);
		return info;
	}

	@Override
	protected Serializable getDefaultBoardObject()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JComponent getGameComponent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getMaxPlayers()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getMinPlayers()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void initializeGame()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void myTurn()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected JComponent renderPlayerInfo(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void renderBoard(Serializable board)
	{
		// TODO Auto-generated method stub

	}

}
