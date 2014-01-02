package net.sf.opengroove.client.oldplugins;

/**
 * This class makes available to a plugin the strings in it's current language
 * pack.
 * 
 * @author Alexander Boyd
 * 
 */
public class LanguageContext
{
    /**
     * Gets the string with the specified key.
     * 
     * @param key
     *            The key
     * @return the string that corresponds to the key specified
     */
    public String getKey(String key)
    {
        return getKey(key, new String[0]);
    }
    
    /**
     * Gets the string with the specified key, injecting the parameters
     * specified. For every value n such that n >= 0 and n < parameters.length,
     * the string {n} will be replaced in the value of the string with
     * parameters[n]. For example, if the value of the key "testkey" is:<br/><br/>
     * 
     * Hello, {0}! Welcome to {1}.<br/><br/>
     * 
     * and the method was called like this:<br/><br/>
     * 
     * getKey("testkey","Alexander Boyd","a really crazy plugin")<br/><br/>
     * 
     * The string:<br/><br/>
     * 
     * Hello, Alexander Boyd! Welcome to a really crazy plugin.<br/><br/>
     * 
     * would be returned.
     */
    public String getKey(String key, String... parameters)
    {
        return null;
    }
    
    /**
     * Returns all of the keys currently known.
     * 
     * @return
     */
    public String[] listKeys()
    {
        return null;
    }
}
