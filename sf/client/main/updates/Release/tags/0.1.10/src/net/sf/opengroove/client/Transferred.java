package net.sf.opengroove.client;

/**
 * An annotation that can be used to annotate various classes that store or
 * transfer data. For example, getters on the Contact class tha contain info
 * that would be set by the user (except for the contact's userid) are annotated
 * with this class, with the value being set to the string value of the property
 * name that should be used in the properties format file stored as the value of
 * the contact's user property.
 * 
 * @author Alexander Boyd
 * 
 */
public @interface Transferred
{
    /**
     * A name for a property or variable that will hold the data while it is
     * transferred.
     * 
     * @return
     */
    public String getValue();
}
