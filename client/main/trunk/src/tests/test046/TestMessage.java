package tests.test046;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@ProxyBean
public interface TestMessage
{
    @Property
    public String getName();
    
    public void setName(String name);
    
    @Property
    public String getMessage();
    
    public void setMessage(String message);
}
