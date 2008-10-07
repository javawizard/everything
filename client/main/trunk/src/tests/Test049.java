package tests;

import java.io.File;

import net.sf.opengroove.common.proxystorage.ProxyStorage;
import tests.test046.TestMessageList;

public class Test049
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        /*
         * Test for vacuuming the database after Test046 is run
         */
        ProxyStorage<TestMessageList> storage = new ProxyStorage<TestMessageList>(
            TestMessageList.class, new File(
                "sandbox/test046/db"));
        TestMessageList list = storage.getRoot();
        System.out.println(list.getMessage("bogus"));
        System.out.println(list.getMessage("1234"));
    }
    
}
