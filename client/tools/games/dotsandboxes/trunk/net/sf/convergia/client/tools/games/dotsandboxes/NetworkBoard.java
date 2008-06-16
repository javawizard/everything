package net.sf.convergia.client.tools.games.dotsandboxes;

import java.io.Serializable;

public class NetworkBoard implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2888470980829039231L;

	private String[][] boxes;// who owns a box

	private boolean[][] hLines; // which horizontal lines have been placed

	private boolean[][] vLines; // which vertical lines have been placed

	private int rows;

	private int cols;

	public NetworkBoard(int rows, int cols)
	{
		this.rows = rows;
		this.cols = cols;
		this.hLines = new boolean[rows + 1][cols + 1];
		this.vLines = new boolean[rows + 1][cols + 1];
		this.boxes = new String[rows][cols];
	}

	public void getFrom(Board board)
	{
		boxes = board.getBoxes();
		hLines = board.getHLines();
		vLines = board.getVLines();
		rows = board.getRows();
		cols = board.getCols();
	}

	public void sendTo(Board board)
	{
		board.clearAndSetSize(rows, cols);
		board.setBoxes(boxes);
		board.setHLines(hLines);
		board.setVLines(vLines);
	}
}
