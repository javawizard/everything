package tests.test046;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestMessage
{
    @Id
    private String name;
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
