package net.sf.opengroove.common.proxystorage;

class TableColumn
{
    private String name;
    private int type;
    private int size;
    
    public TableColumn()
    {
        super();
    }
    
    public TableColumn(String name, int type, int size)
    {
        super();
        this.name = name;
        this.type = type;
        this.size = size;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getType()
    {
        return type;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public void setSize(int size)
    {
        this.size = size;
    }
}
