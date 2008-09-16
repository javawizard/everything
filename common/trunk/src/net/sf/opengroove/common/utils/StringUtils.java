package net.sf.opengroove.common.utils;

public class StringUtils
{
    public boolean isMemberOf(String string, String[] strings)
    {
        for(String test : strings)
        {
            if(test.equals(string))
                return true;
        }
        return false;
    }
    public boolean isMemberOfIgnoreCase(String string, String[] strings)
    {
        for(String test : strings)
        {
            if(test.equalsIgnoreCase(string))
                return true;
        }
        return false;
    }
}
