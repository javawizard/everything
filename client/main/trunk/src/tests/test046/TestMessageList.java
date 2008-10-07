package tests.test046;

import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Required;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface TestMessageList
{
    @Property
    @Required
    @ListType(TestMessage.class)
    public StoredList<TestMessage> getList();
    
    @Search(exact = true, listProperty = "list", searchProperty = "name")
    public TestMessage getMessage(String name);
}
