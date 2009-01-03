package org.bzflag.jzapi;

import org.bzflag.jzapi.BzfsAPI.TeamType;

/**
 * Provides detailed information about a player. Instances of this class can be
 * obtained by using {@link BzfsAPI#getPlayerRecord(int)}. Any instance of this
 * class obtained from that method must be freed by a call to
 * {@link BzfsAPI#freePlayerRecord(BasePlayerRecord)}. If it is not, memory
 * leaks will occur. In the future, a finalizer will be added that automatically
 * frees a record. Records obtained from other methods (for example, those
 * obtained from BzfsEvents) need not (and must not) be freed.<br/><br/>
 * 
 * Calling methods on an instance of this class after freeing it can, and
 * usually will, cause problems. These can range from simply corrupting memory
 * to crashing bzfs. Don't do it. Similarly, freeing a record that does not need
 * to be freed can result in crashing bzfs or other problems.<br/><br/>
 * 
 * Instances of this class obtained from {@link BzfsAPI#getPlayerRecord(int)}
 * can be used in any thread and at any time (up until the record is freed, of
 * course). Instances obtained from events cannot be used outside of the thread
 * that called the event handler, and cannot be used after completion of the
 * event handler's process method.
 * 
 * @author Alexander Boyd
 * 
 */
public class BasePlayerRecord extends Pointed
{
    
    public native int getVersion();
    
    public native void setVersion(int name);
    
    public native int getPlayerID();
    
    public native void setPlayerID(int name);
    
    public native String getCallsign();
    
    public native TeamType getTeam();
    
    public native void setTeam(TeamType name);
    
    public native String getIpAddress();
    
    public native int getCurrentFlagID();
    
    public native void setCurrentFlagID(int name);
    
    public native String getCurrentFlag();
    
    public native String[] getFlagHistory();
    
    public native float getLastUpdateTime();
    
    public native void setLastUpdateTime(float name);
    
    public native String getClientVersion();
    
    public native boolean getSpawned();
    
    public native void setSpawned(boolean name);
    
    public native boolean getVerified();
    
    public native void setVerified(boolean name);
    
    public native boolean getGlobalUser();
    
    public native void setGlobalUser(boolean name);
    
    public native String getBzID();
    
    public native boolean getAdmin();
    
    public native void setAdmin(boolean name);
    
    public native boolean getOp();
    
    public native void setOp(boolean name);
    
    public native boolean getCanSpawn();
    
    public native void setCanSpawn(boolean name);
    
    public native String[] getGroups();
    
    public native int getLag();
    
    public native void setLag(int name);
    
    public native int getJitter();
    
    public native void setJitter(int name);
    
    public native float getPacketLoss();
    
    public native void setPacketLoss(float name);
    
    public native float getRank();
    
    public native void setRank(float name);
    
    public native int getWins();
    
    public native void setWins(int name);
    
    public native int getLosses();
    
    public native void setLosses(int name);
    
    public native int getTeamKills();
    
    public native void setTeamKills(int name);
    
    public native PlayerUpdateState getCurrentState();
    
    public native PlayerUpdateState getLastKnownState();
}
