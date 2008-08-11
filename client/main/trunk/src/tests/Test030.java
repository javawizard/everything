package tests;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Test030
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        // This class is for testing out what happens when an XML attribute
        // contains a newline. I'm assuming some sort of entity will be added.
        Document doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().newDocument();
        Element newElement = doc
            .createElement("testelement");
        newElement
            .setAttribute(
                "testattribute",
                "This is line 1.\r\nThis is line 2, started with rn and ending with n.\nThis is line 3.");
        doc.appendChild(newElement);
        Source source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory.newInstance().newTransformer()
            .transform(source, result);
        System.out.println(writer.toString());
    }
}
