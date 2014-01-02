package org.opengroove.g4.common.object;

/**
 * A pass through object subclass that does nothing except indicate to the
 * client-side message manager that the message should be extracted before being
 * handled. Basically, if an object is wrapped in an ExtractedPassThroughObject
 * and sent in a message, the recipient MessageManager will treat the message as
 * if the wrapped object had been sent itself.
 * 
 * @author Alexander Boyd
 * 
 */
public class ExtractedPassThroughObject extends PassThroughObject
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 8590839516359121639L;
    
}
