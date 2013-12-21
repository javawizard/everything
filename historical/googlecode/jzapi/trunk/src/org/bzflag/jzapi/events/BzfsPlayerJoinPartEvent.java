package org.bzflag.jzapi.events;

import org.bzflag.jzapi.BzfsEvent;

public class BzfsPlayerJoinPartEvent extends BzfsEvent
{
    public native int getPlayerId();
    
    public native void setPlayerId(int playerId);
}
