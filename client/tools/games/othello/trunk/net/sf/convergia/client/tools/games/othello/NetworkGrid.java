package net.sf.convergia.client.tools.games.othello;

import java.io.Serializable;

public class NetworkGrid implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3065012379437189926L;

	private String[][] cells = new String[8][8];

	public String get(int row, int col)
	{
		return cells[row][col];
	}
	

	public void set(int row, int col, String cell)
	{
		cells[row][col] = cell;
	}

	/**
	 * copies the contents of this NetworkGrid to the specified grid. the grid
	 * is repainted after this is done.
	 * 
	 * @param grid
	 */
	public void copyTo(Grid grid)
	{
		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				grid.setCell(r, c, get(r, c));
			}
		}
		grid.repaint();
	}

	/**
	 * copies the contents of the specified grid to this NetworkGrid.
	 * 
	 * @param grid
	 */
	public void copyFrom(Grid grid)
	{
		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				set(r,c,grid.getCellOwner(r,c));
			}
		}
	}

}
