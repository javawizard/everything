package org.bzflag.jzapi;

public class CharArray extends Array
{
    public native char get(int index);
    public native void set(int index, char value);
    public native char[] asArray();
    public native void setFromArray(char[] array);
    public String asString()
    {
        char[] array = asArray();
        StringBuilder b = new StringBuilder();
        for(char c : array)
        {
            if(c != 0)
                b.append(c);
        }
        return b.toString();
    }
}
