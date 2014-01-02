package net.sf.opengroove.client.messaging;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * All fields in MessageManager that are stage threads are marked with this
 * annotation. When the MessageManager is started, it starts all of it's threads
 * marked with this annotation.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StageThread
{
    
}
