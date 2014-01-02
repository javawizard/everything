package net.sf.convergia.client.tools.games.dotsandboxes;

public class LineLocation
{
	private int r;

	private int c;

	private boolean horizontal;

	public LineLocation()
	{
	}

	public LineLocation(int r, int c, boolean horizontal)
	{
		super();
		this.r = r;
		this.c = c;
		this.horizontal = horizontal;
	}

	public int getC()
	{
		return c;
	}

	public void setC(int c)
	{
		this.c = c;
	}

	public String toString()
	{
		return "ll," + r + "," + c + "," + horizontal;
	}

	public boolean isHorizontal()
	{
		return horizontal;
	}

	public boolean getHorizontal()
	{
		return horizontal;
	}

	public void setHorizontal(boolean horizontal)
	{
		this.horizontal = horizontal;
	}

	public int getR()
	{
		return r;
	}

	public void setR(int r)
	{
		this.r = r;
	}
}
