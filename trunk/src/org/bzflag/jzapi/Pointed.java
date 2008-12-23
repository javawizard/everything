package org.bzflag.jzapi;

/**
 * Classes that extend Pointed are classes that wrap a C++ class or struct.
 * Pointed adds one field, a private field called pointer, which is of type long
 * and holds the pointer to the wrapped object. In C++, a reinterpret_cast can
 * be used to cast the field value to a pointer of the wrapped data type.
 * 
 * @author Alexander Boyd
 * 
 */
public class Pointed
{
    /**
     * Accessed and written from native functions. This is the pointer to the
     * backing C++ object. The value 0 indicates that the pointer is null.
     */
    private long pointer = 0;
}
