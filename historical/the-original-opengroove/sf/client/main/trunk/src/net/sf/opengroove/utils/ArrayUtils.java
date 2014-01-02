package net.sf.opengroove.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that contains a collection of utilities related to arrays.
 * 
 * @author Alexander Boyd
 * 
 */
public class ArrayUtils
{
    public <T> T[] append(T[] array, T object)
    {
        ArrayList<T> list = new ArrayList<T>(Arrays
            .asList(array));
        list.add(object);
        return list.toArray((T[]) Array.newInstance(array
            .getClass().getComponentType(), list.size()));
    }
    
    public <T> T[] insert(T[] array, T object, int index)
    {
        ArrayList<T> list = new ArrayList<T>(Arrays
            .asList(array));
        list.add(index, object);
        return list.toArray((T[]) Array.newInstance(array
            .getClass().getComponentType(), list.size()));
    }
}
