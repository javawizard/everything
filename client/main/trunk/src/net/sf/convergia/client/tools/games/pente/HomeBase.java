package net.sf.convergia.client.tools.games.pente;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HomeBase.java

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HomeBase extends JPanel
{

	public HomeBase(Color color1)
	{
		color = null;
		beads = null;
		theBead = null;
		capturedBeads = null;
		state = 0;
		listeners = new Vector();
		color = color1;
		setBorder(new EmptyBorder(3, 3, 3, 3));
		buildPanel();
	}

	public void addListener(HomeBaseListener homebaselistener)
	{
		if (!listeners.contains(homebaselistener))
			listeners.add(homebaselistener);
	}

	public void putCapturedBead(Bead bead)
	{
		HomeBaseEvent homebaseevent = HomeBaseEvent.makeCapturedBeadEvent();
		capturedBeads.putBead(bead);
		informListeners(homebaseevent);
	}

	private void informListeners(HomeBaseEvent homebaseevent)
	{
		System.out.println("implement informListeners()");
	}

	public void setEnabled(boolean flag)
	{
		beads.cellAt(0, 0).setFocus(flag);
		System.out.println("setting focus to " + flag);
	}

	public boolean isEnabled()
	{
		return beads.cellAt(0, 0).hasFocus();
	}

	public static void handlePopupTrigger(MouseEvent mouseevent,
			HomeBase homebase)
	{
		if (!Pente.instance.gameStarted && mouseevent.isPopupTrigger())
		{
			System.out.println("opening chooser.");
			Color color1 = JColorChooser.showDialog(
					homebase.beads.beadAt(0, 0), "Select Bead Color",
					homebase.beads.beadAt(0, 0).getColor());
			if (color1 != null)
			{
				homebase.beads.beadAt(0, 0).setColor(color1);
				homebase.color = color1;
			}
		}
	}

	private void buildPanel()
	{
		JPanel jpanel = new JPanel();
		jpanel.setLayout(new GridLayout(1, 3));
		beads = new Grid(1, 1);
		jpanel.add(new JPanel());
		jpanel.add(beads);
		jpanel.add(new JPanel());
		theBead = new Bead(color);
		theBead.addMouseListener(new MouseAdapter()
		{

			public void mousePressed(MouseEvent mouseevent)
			{
				HomeBase.handlePopupTrigger(mouseevent, HomeBase.this);
			}

			public void mouseReleased(MouseEvent mouseevent)
			{
				HomeBase.handlePopupTrigger(mouseevent, HomeBase.this);
			}

			public void mouseClicked(MouseEvent mouseevent)
			{
				HomeBase.handlePopupTrigger(mouseevent, HomeBase.this);
			}

		});
		beads.putBead(theBead, 0, 0);
		capturedBeads = new Grid(5, 2);
		capturedBeads.setDrawGrid(false);
		GridBagLayout gridbaglayout = new GridBagLayout();
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		setLayout(gridbaglayout);
		gridbagconstraints.gridx = 0;
		gridbagconstraints.gridy = 0;
		gridbagconstraints.gridwidth = 1;
		gridbagconstraints.gridheight = 1;
		gridbagconstraints.fill = 1;
		gridbagconstraints.anchor = 10;
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.weighty = 0.20000000000000001D;
		gridbaglayout.setConstraints(beads, gridbagconstraints);
		add(beads);
		gridbagconstraints.gridx = 0;
		gridbagconstraints.gridy = 1;
		gridbagconstraints.gridwidth = 1;
		gridbagconstraints.gridheight = 4;
		gridbagconstraints.fill = 1;
		gridbagconstraints.anchor = 10;
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.weighty = 0.80000000000000004D;
		gridbaglayout.setConstraints(capturedBeads, gridbagconstraints);
		add(capturedBeads);
	}

	public static void main(String args[])
	{
		JFrame jframe = new JFrame("Pente HomeBase Test");
		jframe.setSize(100, 500);
		jframe.addWindowListener(new WindowAdapter()
		{

			public void windowClosing(WindowEvent windowevent)
			{
				System.exit(0);
			}

		});
		HomeBase homebase = new HomeBase(Color.red);
		homebase.setEnabled(true);
		jframe.getContentPane().add(homebase);
		jframe.setVisible(true);
		do
			try
			{
				Thread.currentThread();
				Thread.sleep(3000L);
				homebase.setEnabled(!homebase.isEnabled());
			} catch (Exception exception)
			{
			}
		while (true);
	}

	static JColorChooser chooser = new JColorChooser();

	Color color;

	Grid beads;

	Bead theBead;

	Grid capturedBeads;

	int state;

	Vector listeners;

	public static final int WAITING_FOR_TURN = 0;

	public static final int PICKING_BEAD = 1;

	public static final int PLACING_BEAD = 2;

	public static final int WON_GAME = 3;

}
