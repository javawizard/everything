package net.sf.opengroove.common.proxystorage;

/**
 * Methods on an object annotated with ProxyBean can be annotated with this to
 * indicate that the method is a search method. When the method is called,
 * objects in a particular stored list on that object will be searched, and a
 * list of those, or the first match, depending on whether the return type for
 * the method is an array of the object or a single instance of the object, will
 * be returned.
 * 
 * @author Alexander Boyd
 * 
 */
public @interface Search
{
    /**
     * The name of the property on the interface that contains the method
     * annotated with this annotation that is a StoredList, and is the list to
     * be searched.
     * 
     * @return
     */
    public String listProperty();
    
    /**
     * The name of the property that contains the data to be searched. This is a
     * property that should be present on the component type of the stored list
     * to search.
     * 
     * @return
     */
    public String searchProperty();
    
    /**
     * Whether or not the search must be exact. This is treated as if it were
     * true for all types other than String. If it is true, then the value
     * passed into the search method must be exactly equal to the search
     * property in order for the object to be included in the result list. If it
     * is false (it can only be false if the list property is a string), then
     * the list property need only be equal to the search string as determined
     * by the SQL "like" keyword (with asterisks in the search string replaced
     * with the percent sign) for a particular element to be included in the
     * result list.
     * 
     * @return
     */
    public boolean exact() default true;
}
