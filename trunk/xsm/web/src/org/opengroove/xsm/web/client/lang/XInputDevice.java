package org.opengroove.xsm.web.client.lang;

public interface XInputDevice
{
    /**
     * Prompts the user for some text, given the message, and returns the text
     * that the user entered.
     * 
     * @param message
     *            The message to prompt the user with
     * @return The message the user entered
     */
    public String prompt(String message);
}
