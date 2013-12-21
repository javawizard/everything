package jw.bznetwork.client.x.lang;

import java.util.HashMap;
import java.util.HashSet;

/**
 * A class that assists commands that can accept arguments either as attributes
 * or as tags at the start of the command's element. The for command is a good
 * example of this; var is always an attribute, but initial, final, and step can
 * be dynamic and appear as those tags within the for loop, respectively.<br/>
 * <br/>
 * 
 * The attribute manager takes the element as an argument, along with the
 * attributes that can be specified as both attributes and tags, and the
 * datatype that should be used if an attribute is encountered instead of a tag.
 * It then returns the number of tags that were consumed (so that those tags can
 * be skipped when the content is executed, if the command is a code function),
 * the value of each attribute/tag, and whether the attribute/tag was an
 * attribute or a tag.<br/> <br/>
 * 
 * Normally, all attributes are required, and if neither an attribute nor a tag
 * is present, an exception will be thrown. This can be overridden by specifying
 * an extra array to the constructor, which is a list of booleans representing
 * whether or not a particular attribute is required. A missing attribute
 * evaluates to null, not XNull.<br/> <br/>
 * 
 * Additionally, an XInterpreterContext can be specified, as well as a boolean
 * list. When this is the case, any values that have their respective boolean
 * set to true and that were sourced from an attribute instead of a tag will
 * resolve their value to the value of the variable named by the attribute
 * value. As an example, the for loop does not make use of this, but the
 * condition attribute of the if statement does. In otherwords, if a for loop
 * resolves, say, the initial attribute to an actual attribute, then the initial
 * value will be the actual number contained in the attribute. With the if
 * statement, however, the condition attribute refers to a variable that holds
 * the condition, not a literal value that indicates whether or not to execute
 * the if statement.
 * 
 * @author Alexander Boyd
 * 
 */
public class XAttributeMerger
{
    private XData[] resultData;
    private boolean[] fromAttribute;
    private int numFromTag = 0;
    
    /**
     * Creates a new XAttributeMerger. Only the first 2 parameters and
     * <tt>context</tt> are required; the rest may be null. All boolean arrays
     * that are null default to containing false as many times as is needed for
     * the number of attributes that are to be interpreted.
     * 
     * @param element
     * @param attributes
     * @param asNumbers
     * @param optional
     * @param context
     * @param resolveToVar
     */
    public XAttributeMerger(XElement element, String[] attributes,
            boolean[] asNumbers, boolean[] optional,
            XInterpreterContext context, boolean[] resolveToVar)
    {
        if (asNumbers == null)
            asNumbers = new boolean[attributes.length];
        /*
         * We won't set optional here, since we can just skip the optional check
         * if it's null
         */
        if (resolveToVar == null)
            resolveToVar = new boolean[attributes.length];
        resultData = new XData[attributes.length];
        fromAttribute = new boolean[attributes.length];
        HashSet<String> fromAttributeList = new HashSet<String>();
        HashMap<String, Integer> allowedAttributes = new HashMap<String, Integer>();
        for (int i = 0; i < attributes.length; i++)
        {
            allowedAttributes.put(attributes[i], i);
            String attributeValue = element.getAttribute(attributes[i]);
            if (attributeValue != null)
            {
                /*
                 * This is a requested attribute that is, indeed, present. We'll
                 * load its value, resolve it to a variable if requested, and
                 * stick it in resultData. For simplicity, we're not going to
                 * make sure it's a number if it comes from a variable.
                 */
                fromAttributeList.add(attributes[i]);
                fromAttribute[i] = true;
                if (resolveToVar[i])
                {
                    resultData[i] = context.getVariable(attributeValue);
                }
                else if (asNumbers[i])
                {
                    resultData[i] = XInterpreter.parseNumeric(attributeValue);
                }
                else
                {
                    resultData[i] = new XString(attributeValue);
                }
            }
        }
        /*
         * Now we scan for the first set of tags, making sure that they are in
         * allowedAttributes but not in fromAttributeList. For each one, we
         * interpret the single element in the tag and use its value as data.
         */
        for (XNode child : element.getChildren())
        {
            XElement ce = (XElement) child;
            String tag = ce.getTag();
            if (allowedAttributes.containsKey(tag.toLowerCase())
                    && !fromAttributeList.contains(tag.toLowerCase()))
            {
                /*
                 * This tag is valid. We'll interpret the single element and set
                 * the result as the value.
                 */
                numFromTag += 1;
                XData thisResult = context.execute(ce.getSingleElement());
                resultData[allowedAttributes.get(tag.toLowerCase())] = thisResult;
            }
            else
            {
                /*
                 * This tag is not valid. We've reached the end of the list of
                 * valid tags.
                 */
                break;
            }
        }
        /*
         * Everything's been loaded. Now we check to make sure that required
         * attributes are present.
         */
        if (optional != null)
        {
            for (int i = 0; i < optional.length; i++)
            {
                if (!optional[i] && resultData[i] == null)
                    throw new XException("Required attribute " + attributes[i]
                            + " was not present as a tag or as an attribute");
            }
        }
        /*
         * We're done!
         */
    }
    
    /**
     * Returns the result for this particular attribute.
     * 
     * @param index
     *            The index into the <tt>attributes</tt> array that the
     *            attribute name was when this was constructed
     * @return The data for the specified attribute. If the variable was an
     *         attribute and was not resolved as a variable, then the result
     *         data will be an XString.
     */
    public XData getResult(int index)
    {
        return resultData[index];
    }
    
    public boolean fromAttribute(int index)
    {
        // return fromAttribute(index);
        throw new RuntimeException("Something's wrong with this method...");
    }
    
    /**
     * Returns the number of attributes that took their value from a tag. This
     * can be used to skip over those tags when executing the content of the
     * element, if the element is a code function.
     * 
     * @return
     */
    public int getTagCount()
    {
        return numFromTag;
    }
}
