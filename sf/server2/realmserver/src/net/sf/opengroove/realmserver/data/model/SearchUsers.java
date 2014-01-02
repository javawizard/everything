package net.sf.opengroove.realmserver.data.model;

public class SearchUsers
{
    private int offset;
    private int limit;
    private String search;
    private String[] keys;
    private boolean searchkeys;
    
    public String[] getKeys()
    {
        return keys;
    }
    
    public void setKeys(String[] keys)
    {
        this.keys = keys;
    }
    
    public int getOffset()
    {
        return offset;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public String getSearch()
    {
        return search;
    }
    
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setSearch(String search)
    {
        this.search = search;
    }
    
    public boolean isSearchkeys()
    {
        return searchkeys;
    }
    
    public void setSearchkeys(boolean searchkeys)
    {
        this.searchkeys = searchkeys;
    }
}
