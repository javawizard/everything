package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class TargetEventPair implements Serializable
{
    private int target;
    private String event;
    
    public int getTarget()
    {
        return target;
    }
    
    public void setTarget(int target)
    {
        this.target = target;
    }
    
    public String getEvent()
    {
        return event;
    }
    
    public void setEvent(String event)
    {
        this.event = event;
    }
}
