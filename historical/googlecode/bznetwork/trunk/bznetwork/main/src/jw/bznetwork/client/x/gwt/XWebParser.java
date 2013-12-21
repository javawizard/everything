package jw.bznetwork.client.x.gwt;

import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XText;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

/**
 * A class that can parse a string into an XElement using GWT's DOM library.
 * 
 * @author Alexander Boyd
 * 
 */
public class XWebParser
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
        Document doc = XMLParser.parse(text);
        return nodeToElement((Element) doc.getFirstChild());
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
                Text text = (Text) child;
                if (!text.getNodeValue().trim().equals(""))
                    element.getChildren().add(new XText(child.getNodeValue().trim()));
            }
        }
        return element;
    }
}
