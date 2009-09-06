package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class EmailGroup implements Serializable
{
    private int emailgroupid;
    private String name;
    private String addresses;
    
    public int getEmailgroupid()
    {
        return emailgroupid;
    }
    
    public void setEmailgroupid(int emailgroupid)
    {
        this.emailgroupid = emailgroupid;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getAddresses()
    {
        return addresses;
    }
    
    public void setAddresses(String addresses)
    {
        this.addresses = addresses;
    }
    
    public String[] getAddressList()
    {
        if (addresses.trim().equals(""))
            return new String[0];
        return addresses.split("\\|");
    }
}
