package org.opengroove.xsm.web.client.lang;

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
 * attribute or a tag.<br/>
 * <br/>
 * 
 * Normally, all attributes are required, and if neither an attribute nor a tag
 * is present, an exception will be thrown. This can be overridden by specifying
 * an extra array to the constructor, which is a list of booleans representing
 * whether or not a particular attribute is required. A missing attribute
 * evaluates to null, not XNull.<br/>
 * <br/>
 * 
 * Additionally, an XInterpreterContext can be specified, as well as a boolean
 * list. When this is the case, any values that have their respective boolean
 * set to true and that were sourced from an attribute instead of a tag will
 * resolve their value to the value of the variable named by the attribute
 * value. As an example, the for loop does not make use of this, but the
 * condition attribute of the if statement does. In otherwords, if a for loop
 * resolves, say, the initial attribute to an actual attribute, then the initial
 * value will be the actual number contained in the attribute. With the if
 * statement, however,
 * 
 * @author Alexander Boyd
 * 
 */
public class XAttributeMerger
{
    private XData[] resultData;
    private boolean[] fromAttribute;
    private int numFromTag;
}
