package tests;

import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

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
        e
            .setAttribute("testname",
                "line 1, ending with rn\r\nline 2, ending with n\nline 3");
        document.addContent(e);
        XMLOutputter generator = new XMLOutputter();
        StringWriter sw = new StringWriter();
        generator.output(document, sw);
        System.out.println(sw.toString());
    }
}
