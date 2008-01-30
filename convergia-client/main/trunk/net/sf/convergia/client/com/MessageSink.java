package net.sf.convergia.client.com;

public interface MessageSink
{
	public void process(String from, String message);
}
