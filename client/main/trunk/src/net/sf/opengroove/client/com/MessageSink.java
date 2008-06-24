package net.sf.opengroove.client.com;

public interface MessageSink
{
	public void process(String from, String message);
}
