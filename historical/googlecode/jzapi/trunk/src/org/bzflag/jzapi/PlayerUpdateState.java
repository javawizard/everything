package org.bzflag.jzapi;

import org.bzflag.jzapi.BzfsAPI.PlayerStatus;

public class PlayerUpdateState extends Pointed
{
    public native PlayerStatus getStatus();
    
    public native void setStatus(PlayerStatus name);
    
    public native boolean getFalling();
    
    public native void setFalling(boolean name);
    
    public native boolean getCrossingWall();
    
    public native void setCrossingWall(boolean name);
    
    public native boolean getInPhantomZone();
    
    public native void setInPhantomZone(boolean name);
    
    public native float[] getPos();
    
    public native void setPos(float[] name);
    
    public native float[] getVelocity();
    
    public native void setVelocity(float[] name);
    
    public native float getRotation();
    
    public native void setRotation(float name);
    
    public native float getAngVel();
    
    public native void setAngVel(float name);
    
    public native int getPhydrv();
    
    public native void setPhydrv(int name);
}
