package org.opengroove.xsm.web.client.lang;

public interface XCommand
{
    /**
     * Returns the name of the command. This command can be invoked using a tag
     * that has this name.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Invokes this command.
     * 
     * @param context
     *            The context in which the command is to be invoked, which
     *            contains variables and the interpreter itself
     * @param element
     *            The element that represents this function. Attribute values
     *            and arguments can be retrieved from this.
     * @return The return data of this command, or null if this command does not
     *         return. If this command returns an XSM null value, then it should
     *         return a new XNull.
     */
    public XData invoke(XInterpreterContext context, XElement element);
}
