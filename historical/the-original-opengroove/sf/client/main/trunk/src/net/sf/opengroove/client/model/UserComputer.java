package net.sf.opengroove.client.model;

public class UserComputer
{
    private String userid;
    private String computer;
    
    public UserComputer()
    {
        super();
    }
    
    public UserComputer(String userid, String computer)
    {
        super();
        this.userid = userid;
        this.computer = computer;
    }
    
    public String getUserid()
    {
        return userid;
    }
    
    public String getComputer()
    {
        return computer;
    }
    
    public void setUserid(String userid)
    {
        this.userid = userid;
    }
    
    public void setComputer(String computer)
    {
        this.computer = computer;
    }
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((computer == null) ? 0 : computer.hashCode());
        result = prime * result + ((userid == null) ? 0 : userid.hashCode());
        return result;
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final UserComputer other = (UserComputer) obj;
        if (computer == null)
        {
            if (other.computer != null)
                return false;
        }
        else if (!computer.equals(other.computer))
            return false;
        if (userid == null)
        {
            if (other.userid != null)
                return false;
        }
        else if (!userid.equals(other.userid))
            return false;
        return true;
    }
}
