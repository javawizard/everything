package net.sf.opengroove.client;

public class Conventions
{
    public String formatMenuPath(String... path)
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
