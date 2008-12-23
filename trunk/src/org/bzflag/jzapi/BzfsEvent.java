package org.bzflag.jzapi;

import org.bzflag.jzapi.BzfsAPI.EventType;

/**
 * An event that is passed to a particular listener. Events are only valid
 * during the listener's method call. Thereafter, the effects of calling a
 * method on an event are undefined, ranging from an exception to crashing bzfs.
 * 
 * @author Alexander Boyd
 * 
 */
public class BzfsEvent extends Pointed
{    
    public native EventType getEventType();
    
    public native double getEventTime();
}
