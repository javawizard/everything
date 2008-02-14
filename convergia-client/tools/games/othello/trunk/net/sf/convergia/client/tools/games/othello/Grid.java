package net.sf.convergia.client.tools.games.othello;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JComponent;

/**
 * An othello grid. Cells can be set, and when the mouse is clicked, you can ask
 * the grid what cell was clicked based on the position clicked.
 * 
 * @author Mark Boyd
 * 
 */
public class Grid extends JComponent
{
	public static final int NUM_ROWS = 8;

	public static final int NUM_COLS = NUM_ROWS;

	public int rowHeight;

	public int colWidth;

	private String[][] cells = new String[NUM_COLS][NUM_ROWS];

	private String[][] phantomCells = new String[NUM_COLS][NUM_ROWS];

	private int[][] cellNumbers = new int[NUM_COLS][NUM_ROWS];

	private Color[][] cellColors = new Color[NUM_COLS][NUM_ROWS];

	private Color background = Color.WHITE;

	private SelectionListener listener = null;

	public Panel bubbles;

	public void setCellColor(int row, int col, Color color)
	{
		// System.out.println("setting to color " + color);
		cellColors[col][row] = color;
		repaint();
	}

	public Color getCellColor(int row, int col)
	{
		return cellColors[col][row];
	}

	public void clearCellColors()
	{
		cellColors = new Color[NUM_COLS][NUM_ROWS];
		repaint();
	}

	public void addBubble(int row, int col, String text)
	{
		bubbles.setBackground(new Color(255, 255, 255, 255));
		int gridx = getLocation().x;
		int gridy = getLocation().y;
		int cw = colWidth;
		int rh = rowHeight;
		int posRelativeToGridX = col * cw;
		int posRelativeToGridY = row * rh;
		int width = cw - 10;
		int height = rh - 16;
		TextArea area = new TextArea(text);
		area.setSize(width, height);
		area
				.setLocation(posRelativeToGridX + gridx, posRelativeToGridY
						+ gridy);
		area.setFont(Font.decode("Arial-PLAIN-10"));
		bubbles.add(area);
		bubbles.repaint();
	}

	public void clearBubbles()
	{
		bubbles.removeAll();
		bubbles.repaint();
	}

	private Othello othello;

	public Grid(Othello othello)
	{
		this.othello = othello;
		setFocusable(true);
		setOpaque(true);
		addMouseListener(new MouseListener()
		{

			public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent e)
			{
				requestFocusInWindow();
			}

			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}
		});
		{

		}
		/*
		 * this.addMouseListener( new MouseAdapter() { public void mouseClicked(
		 * MouseEvent me ) { if ( listener != null ) listener.cellSelected(
		 * getCellRow( me.getY() ), getCellColumn( me.getX() ) ); } });
		 */
	}

	public int getCellNumber(int row, int col)
	{
		return cellNumbers[col][row];
	}

	public void setCellNumber(int row, int col, int number)
	{
		cellNumbers[col][row] = number;
		repaint();
	}

	public void clearCellNumbers()
	{
		cellNumbers = new int[NUM_COLS][NUM_ROWS];
		repaint();
	}

	public String getCellOwner(int row, int col)
	{
		return cells[col][row];
	}

	public boolean isValidRow(int row)
	{
		if (row >= 0 && row < NUM_ROWS)
			return true;
		return false;
	}

	public boolean isValidColumn(int col)
	{
		if (col >= 0 && col < NUM_COLS)
			return true;
		return false;
	}

	public boolean isValidCell(int row, int col)
	{
		if (isValidRow(row) && isValidColumn(col))
			return true;
		return false;
	}

	int getCellRow(int y)
	{
		int rowPix = getSize().height / NUM_ROWS;
		return y / rowPix;
	}

	int getCellColumn(int x)
	{
		int colPix = getSize().width / NUM_COLS;
		return x / colPix;
	}

	public void setSelectionListener(SelectionListener listener)
	{
		this.listener = listener;
	}

	public void setCell(int row, int col, String p)
	{
		cells[col][row] = p;
	}

	public void setPhantomCell(int row, int col, String player)
	{
		phantomCells[col][row] = player;
	}

	public int getCellsFor(String p)
	{
		int playerCells = 0;

		for (int row = 0; row < NUM_ROWS; row++)
			for (int col = 0; col < NUM_COLS; col++)
				if (cells[col][row] != null && cells[col][row].equals(p))
					playerCells++;
		System.out.println("cells for " + p + " = " + playerCells);
		return playerCells;
	}

	public int getCellCount()
	{
		int playerCells = 0;

		for (int row = 0; row < NUM_ROWS; row++)
			for (int col = 0; col < NUM_COLS; col++)
				if (cells[col][row] != null)
					playerCells++;
		System.out.println("cell count = " + playerCells);
		return playerCells;
	}

	public void clearPhantomCells()
	{
		for (int r = 0; r < NUM_ROWS; r++)
		{
			for (int c = 0; c < NUM_COLS; c++)
			{
				phantomCells[c][r] = null;
			}
		}
	}

	public String getPhantomCell(int r, int c)
	{
		return phantomCells[c][r];
	}

	public synchronized void paintComponent(Graphics g)
	{
		Dimension size = getSize();
		colWidth = size.width / NUM_COLS;
		rowHeight = size.height / NUM_ROWS;
		if (isOpaque())
		{
			g.setColor(background);
			g.fillRect(0, 0, size.width, size.height);
		}
		g.setColor(Color.black);
		g.drawRect(0, 0, size.width - 1, size.height - 1);
		g.drawRect(2 * colWidth + 2, 2 * rowHeight + 2, 4 * colWidth - 3,
				4 * rowHeight - 3);

		for (int col = 0; col < NUM_COLS; col++)
		{

			if (col != 0)
			{
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(col * colWidth, 0, col * colWidth, size.height);
				g.drawLine(0, col * rowHeight, size.width, col * rowHeight);
				g.setColor(Color.GRAY);
				g.drawLine(col * colWidth + 1, 0, col * colWidth + 1,
						size.height);
				g.drawLine(0, col * rowHeight + 1, size.width, col * rowHeight
						+ 1);
			}

			for (int row = 0; row < NUM_ROWS; row++)
			{
				if (cells[col][row] != null)
				{
					g.setColor(Color.black);
					g.fillOval(col * colWidth + 4, row * rowHeight + 4,
							colWidth - 8, rowHeight - 8);
					g.setColor(othello.getBeadColor(cells[col][row]));
					g.fillOval(col * colWidth + 5, row * rowHeight + 5,
							colWidth - 10, rowHeight - 10);
				}
				if (cellColors[col][row] != null)
				{
					g.setColor(cellColors[col][row]);
					// System.out.println("drawing fill box " + row + "," + col
					// + " " + g.getColor() + " with original " +
					// cellColors[col][row]);
					g.fillRect(col * colWidth + 3, row * rowHeight + 3,
							colWidth - 6, rowHeight - 6);
				}
				if (cellNumbers[col][row] != 0)
				{
					g.setColor(Color.black);
					g.drawString("" + cellNumbers[col][row],
							col * colWidth + 6, (row + 1) * rowHeight - 8);
				}
				if (phantomCells[col][row] != null && cells[col][row] == null)
				// only draw a phantom cell if there isn't a regular cell there
				{
					Color pColor = othello.getBeadColor(phantomCells[col][row]);
					g.setColor(new Color(pColor.getRed(), pColor.getGreen(),
							pColor.getBlue(), 128));
					g.fillOval(col * colWidth + 5, row * rowHeight + 5,
							colWidth - 10, rowHeight - 10);
				}
			}
		}
	}

	public Grid copyOf()
	{
		Grid grid = new Grid(this.othello);
		NetworkGrid ngrid = new NetworkGrid();
		ngrid.copyFrom(this);
		ngrid.copyTo(grid);
		return grid;
	}
}