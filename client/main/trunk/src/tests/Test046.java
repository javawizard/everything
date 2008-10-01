package tests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.Hibernate;
import org.hibernate.ejb.HibernatePersistence;

import tests.test046.TestMessage;

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
            .createContainerEntityManagerFactory(
                new PersistenceUnitInfo()
                {
                    
                    @Override
                    public void addTransformer(
                        ClassTransformer transformer)
                    {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public boolean excludeUnlistedClasses()
                    {
                        // TODO Auto-generated method stub
                        return false;
                    }
                    
                    @Override
                    public ClassLoader getClassLoader()
                    {
                        // TODO Auto-generated method stub
                        return super.getClass()
                            .getClassLoader();
                    }
                    
                    @Override
                    public List<URL> getJarFileUrls()
                    {
                        return new ArrayList<URL>();
                    }
                    
                    @Override
                    public DataSource getJtaDataSource()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public List<String> getManagedClassNames()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public List<String> getMappingFileNames()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public ClassLoader getNewTempClassLoader()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public DataSource getNonJtaDataSource()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getPersistenceProviderClassName()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getPersistenceUnitName()
                    {
                        // TODO Auto-generated method stub
                        return "default";
                    }
                    
                    @Override
                    public URL getPersistenceUnitRootUrl()
                    {
                        try
                        {
                            return new File("classes")
                                .toURI().toURL();
                        }
                        catch (MalformedURLException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return null;
                    }
                    
                    @Override
                    public Properties getProperties()
                    {
                        // TODO Auto-generated method stub
                        return new Properties();
                    }
                    
                    @Override
                    public PersistenceUnitTransactionType getTransactionType()
                    {
                        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
                    }
                }, map);
        System.out.println("creating entitymanager");
        EntityManager manager = factory
            .createEntityManager();
        System.out.println("created.");
        TestMessage message = new TestMessage();
        message.setName("testname");
        message.setMessage("This is a test message.");
        EntityTransaction transaction = manager
            .getTransaction();
        transaction.begin();
        manager.persist(message);
        manager.flush();
        transaction.commit();
    }
    
}
