package net.sf.opengroove.common.utils;

import net.sf.opengroove.common.proxystorage.ProxyStorage.ToString;

public class StringUtils
{
    public static boolean isMemberOf(String string,
        String[] strings)
    {
        for (String test : strings)
        {
            if (test.equals(string))
                return true;
        }
        return false;
    }
    
    public static boolean isMemberOfIgnoreCase(
        String string, String[] strings)
    {
        for (String test : strings)
        {
            if (test.equalsIgnoreCase(string))
                return true;
        }
        return false;
    }
    
    public static <T> String delimited(T[] items,
        ToString<T> generator, String delimiter)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++)
        {
            if (i != 0)
                sb.append(delimiter);
            sb.append(generator.toString(items[i]));
        }
        return sb.toString();
    }
    
    public interface ToString<S>
    {
        public String toString(S object);
    }
    
}
