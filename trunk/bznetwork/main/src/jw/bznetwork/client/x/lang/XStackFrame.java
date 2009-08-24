package jw.bznetwork.client.x.lang;

public class XStackFrame
{
    private String command;
    
    public String getCommand()
    {
        return command;
    }
    
    public void setCommand(String command)
    {
        this.command = command;
    }
    
    public XStackFrame(String command)
    {
        super();
        this.command = command;
    }
}
