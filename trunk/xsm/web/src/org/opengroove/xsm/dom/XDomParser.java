package org.opengroove.xsm.dom;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XText;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class XDomParser
{
    /**
     * Parses the specified text into an XElement. The text should be a single
     * element, and can be wrapped in an xsm tag if it would otherwise contain
     * multiple elements.
     * 
     * @param text
     *            The text to parse
     * @return The parsed text
     */
    public static XElement parse(String text)
    {
        try
        {
            Document doc =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(new StringReader(text)));
            return nodeToElement((Element) doc.getFirstChild());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Parses a node that is either a document or an element to an XElement.
     * 
     * @param node
     * @return
     */
    private static XElement nodeToElement(Element node)
    {
        XElement element = new XElement();
        element.setTag(node.getTagName().toLowerCase());
        NamedNodeMap attributeList = node.getAttributes();
        for (int i = 0; i < attributeList.getLength(); i++)
        {
            Attr attribute = (Attr) attributeList.item(i);
            element.getAttributes().put(attribute.getName().toLowerCase(),
                attribute.getValue());
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            Node child = children.item(i);
            if (child instanceof Element)
            {
                element.getChildren().add(nodeToElement((Element) child));
            }
            else if (child instanceof Text)
            {
                element.getChildren().add(new XText(((Text) child).getNodeValue()));
            }
        }
        return element;
    }
    
}
