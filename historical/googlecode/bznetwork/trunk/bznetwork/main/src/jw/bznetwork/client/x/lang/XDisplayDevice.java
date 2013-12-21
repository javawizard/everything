package jw.bznetwork.client.x.lang;

public interface XDisplayDevice
{
    /**
     * Prints a string to this display device.
     * 
     * @param string
     *            The string to print
     * @param newline
     *            True if this display device should append an end-of-line
     *            character after writing this, false if it should not
     */
    public void print(String string, boolean newline);
}
