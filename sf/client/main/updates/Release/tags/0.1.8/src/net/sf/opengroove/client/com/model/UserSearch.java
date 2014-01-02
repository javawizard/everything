package net.sf.opengroove.client.com.model;

public class UserSearch
{
    private int total;
    private String[] results;
    
    public int getTotal()
    {
        return total;
    }
    
    public String[] getResults()
    {
        return results;
    }
    
    public void setTotal(int total)
    {
        this.total = total;
    }
    
    public void setResults(String[] results)
    {
        this.results = results;
    }
}
