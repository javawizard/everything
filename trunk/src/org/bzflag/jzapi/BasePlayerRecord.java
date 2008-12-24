package org.bzflag.jzapi;

import org.bzflag.jzapi.BzfsAPI.TeamType;

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
}
