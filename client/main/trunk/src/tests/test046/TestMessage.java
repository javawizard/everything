package tests.test046;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class TestMessage
{
    @Id
    private String name;
    @Basic(fetch = FetchType.LAZY)
    private String message;
    
    public String getName()
    {
        return name;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
}
