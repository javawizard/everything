package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a certain property of a proxy bean type is to have a default
 * value other than 0 or false.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Default
{
    /**
     * For int properties, the default int type.
     * 
     * @return
     */
    public int intValue();
    
    /**
     * For long properties, the default long type.
     * 
     * @return
     */
    public long longValue();
    
    /**
     * For double properties, the default double type.
     * 
     * @return
     */
    public double doubleValue();
    
    /**
     * For boolean properties, the default boolean type.
     * 
     * @return
     */
    public boolean booleanValue();
    
    /**
     * For string properties, the default string type.
     * 
     * @return
     */
    public String stringValue();
}
