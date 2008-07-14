package net.sf.opengroove.realmserver.data.model;

public class StoredMessage
{
    // id sender recipient fromcomputer tocomputer varchar
    // maxchunks maxchunksize lifecycle lifecycleprogress lifecycletotal int
    // needslifecycleupdate finalized approved boolean
    // metadata varchar
    private String id;
    private String sender;
    private String recipient;
    private String fromcomputer;
    private String tocomputer;
    private int maxchunks;
    private int maxchunksize;
    private int lifecycle;
    private int lifecycleprogress;
    private int lifecycletotal;
    private boolean needslifecycleupdate;
    private boolean finalized;
    private boolean approved;
    private String metadata;
    
    public String getId()
    {
        return id;
    }
    
    public String getSender()
    {
        return sender;
    }
    
    public String getRecipient()
    {
        return recipient;
    }
    
    public String getFromcomputer()
    {
        return fromcomputer;
    }
    
    public String getTocomputer()
    {
        return tocomputer;
    }
    
    public int getMaxchunks()
    {
        return maxchunks;
    }
    
    public int getMaxchunksize()
    {
        return maxchunksize;
    }
    
    public int getLifecycle()
    {
        return lifecycle;
    }
    
    public int getLifecycleprogress()
    {
        return lifecycleprogress;
    }
    
    public int getLifecycletotal()
    {
        return lifecycletotal;
    }
    
    public boolean isNeedslifecycleupdate()
    {
        return needslifecycleupdate;
    }
    
    public boolean isFinalized()
    {
        return finalized;
    }
    
    public boolean isApproved()
    {
        return approved;
    }
    
    public String getMetadata()
    {
        return metadata;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setSender(String sender)
    {
        this.sender = sender;
    }
    
    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }
    
    public void setFromcomputer(String fromcomputer)
    {
        this.fromcomputer = fromcomputer;
    }
    
    public void setTocomputer(String tocomputer)
    {
        this.tocomputer = tocomputer;
    }
    
    public void setMaxchunks(int maxchunks)
    {
        this.maxchunks = maxchunks;
    }
    
    public void setMaxchunksize(int maxchunksize)
    {
        this.maxchunksize = maxchunksize;
    }
    
    public void setLifecycle(int lifecycle)
    {
        this.lifecycle = lifecycle;
    }
    
    public void setLifecycleprogress(int lifecycleprogress)
    {
        this.lifecycleprogress = lifecycleprogress;
    }
    
    public void setLifecycletotal(int lifecycletotal)
    {
        this.lifecycletotal = lifecycletotal;
    }
    
    public void setNeedslifecycleupdate(
        boolean needslifecycleupdate)
    {
        this.needslifecycleupdate = needslifecycleupdate;
    }
    
    public void setFinalized(boolean finalized)
    {
        this.finalized = finalized;
    }
    
    public void setApproved(boolean approved)
    {
        this.approved = approved;
    }
    
    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }
}
