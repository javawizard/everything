package tests;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Hibernate;
import org.hibernate.ejb.HibernatePersistence;

/**
 * A class for testing out simple persistant stuff.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test046
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        HashMap map = new HashMap();
        map.put("hibernate.dialect",
            "org.hibernate.dialect.H2Dialect");
        map.put("hibernate.hbm2ddl.auto", "update");
        map.put("hibernate.connection.driver_class",
            "org.h2.Driver");
        map.put("hibernate.connection.username", "sa");
        map.put("hibernate.connection.password", "");
        map.put("hibernate.connection.url",
            "jdbc:h2:sandbox/test046/db");
        EntityManagerFactory factory = new HibernatePersistence()
            .createEntityManagerFactory("testunit", map);
    }
    
}
