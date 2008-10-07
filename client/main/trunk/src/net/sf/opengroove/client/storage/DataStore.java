package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.*;

@ProxyBean
public interface DataStore
{
    /**
     * The list of users within this store.
     * 
     * @return
     */
    @Property
    @ListType(LocalUser.class)
    public StoredList<LocalUser> getUsers();
    
    /**
     * Gets a user by their userid.
     * 
     * @param userid
     *            The userid to search for
     * @return The user
     */
    @Search(listProperty = "users", searchProperty = "userid")
    public LocalUser getUser(String userid);
    
    /**
     * Creates a new LocalUser instance. This must then be added to
     * {@link #getUsers()} in order to persist.
     * 
     * @return
     */
    @Constructor
    public LocalUser createUser();
    
    @Property
    @ListType(ConfigProperty.class)
    public StoredList<ConfigProperty> getProperties();
    
    @Constructor
    public ConfigProperty createProperty();
    
    @Search(listProperty = "properties", searchProperty = "name")
    public ConfigProperty getProperty(String name);
    
}
