package net.sf.convergia.client.tools.games.risk;

import java.io.Serializable;

import javax.swing.JComponent;

import net.sf.convergia.client.tools.games.multiplayer.MultiplayerSequenceGame;

public class Risk extends MultiplayerSequenceGame
{

	@Override
	protected Serializable createPlayerMetadata()
	{
		return null;
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
	protected void renderBoard(Serializable board)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected JComponent renderPlayerInfo(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
