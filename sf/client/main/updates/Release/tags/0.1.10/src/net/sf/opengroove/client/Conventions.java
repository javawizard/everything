package net.sf.opengroove.client;

/**
 * A class containing methods for generating string content that organizes
 * different types of data according to OpenGroove's conventions.
 * 
 * @author Alexander Boyd
 * 
 */
public class Conventions
{
    /**
     *  Formats a menu path. The arguments are a bunch of strings, each one
     * representing a progressive item within a menu. The result is the
     * formatted version of the paths entered. Currently, it returns the items
     * specified, but with a &rarr; character inbetween each one. For example, calling:<br/><br/>
     * 
     * formatMenuPath("View", "Zoom", "Normal")<br/><br/>
     * 
     * would produce:<br/><br/>
     * 
     * View &rarr; Zoom &rarr; Normal
     * 
     * @param path
     * @return
     */
    public static String formatMenuPath(String... path)
    {
        if (path.length == 0)
            return "";
        String result = path[0];
        for (int i = 1; i < path.length; i++)
        {
            result += " " + Symbol.RIGHT + " " + path[i];
        }
        return result;
    }
}
