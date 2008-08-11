package tests;

import org.jdom.Document;
import org.jdom.Element;

public class Test030
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        // This class is for testing out what happens when an XML attribute
        // contains a newline. I'm assuming some sort of entity will be added.
        Document document = new Document();
        Element e = new Element("testelement");
    }
}
