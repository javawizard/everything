package org.opengroove.g4.common.protocol.doc;

import java.lang.annotation.Documented;

/**
 * A marker annotation on packet subclasses that serves no other purpose than to
 * indicate to javadoc readers that the annotated packet type may be sent from
 * the server to the client.
 * 
 * @author Alexander Boyd
 * 
 */
@Documented
public @interface ServerToClient
{
    
}
