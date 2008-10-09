package net.sf.opengroove.common.proxystorage;

/**
 * A property can be annotated with this to indicate that another property
 * should be modified, and filled with the current time, when this one changes.
 * 
 * @author Alexander Boyd
 * 
 */
public @interface LastModified
{
    /**
     * The property to change, which must be of type long. When the property
     * annotated with this annotation changes, System.currentTimeMillis() will
     * be inserted into
     * 
     * @return
     */
    public String property();
}
