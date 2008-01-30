package net.sf.convergia.client.toolworkspace.sync;

import javax.swing.JComponent;

import net.sf.convergia.client.toolworkspace.Tool;

/**
 * NOTE: this class is preferred to com.trivergia.cintouch3.client.toolWorkspace.SynchronizingTool
 * 
 * This class allows implementations to store pieces of data (referred to as blocks) that will be synchronized between computers
 * that use this tool. Blocks have a name, which should be no longer than 512 characters. the main data of a block is stored in 
 * it's properties, which are made up of keys and values.
 * @author Alexander Boyd
 *
 */
public abstract class SynchronizingTool extends Tool
{

	@Override
	public JComponent getComponent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(String from, String message)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userStatusChanged()
	{
		// TODO Auto-generated method stub

	}

}
