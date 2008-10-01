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
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import net.sf.opengroove.common.proxystorage.ProxyStorage;

import org.hibernate.Hibernate;
import org.hibernate.ejb.HibernatePersistence;

import tests.test046.TestMessage;
import tests.test046.TestMessageList;

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
    public static void main(String[] args) throws Throwable
    {
        ProxyStorage<TestMessageList> storage = new ProxyStorage<TestMessageList>(
            TestMessageList.class, new File(
                "sandbox/test046/db"));
    }
}
