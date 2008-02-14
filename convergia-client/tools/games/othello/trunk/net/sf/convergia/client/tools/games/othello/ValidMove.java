package net.sf.convergia.client.tools.games.othello;

/**
 * Conveys a valid move for a user and the number of beads that will be captured
 * as a result.
 * 
 * @author Mark Boyd
 */
public class ValidMove
{
	int row = -1;

	int column = -1;

	int captures = -1;

	ValidMove(int row, int col, int captures)
	{
		this.row = row;
		this.column = col;
		this.captures = captures;
	}

	public int getCaptures()
	{
		return captures;
	}

	public void setCaptures(int captures)
	{
		this.captures = captures;
	}

	public int getColumn()
	{
		return column;
	}

	public void setColumn(int column)
	{
		this.column = column;
	}

	public int getRow()
	{
		return row;
	}

	public void setRow(int row)
	{
		this.row = row;
	}
}
