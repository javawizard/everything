package org.opengroove.g4.server.commands.types;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to annotate a command that can be used when a user has authenticated as
 * a user only, not as a computer.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserCommand
{
    
}
