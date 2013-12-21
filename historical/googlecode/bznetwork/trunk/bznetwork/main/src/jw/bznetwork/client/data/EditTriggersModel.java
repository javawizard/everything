package jw.bznetwork.client.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import jw.bznetwork.client.data.model.Trigger;

public class EditTriggersModel implements Serializable
{
    private Trigger[] triggers;
    private HashMap<Integer, String> targets = new LinkedHashMap<Integer, String>();
    private HashMap<Integer, String> recipients = new LinkedHashMap<Integer, String>();
    
    public HashMap<Integer, String> getRecipients()
    {
        return recipients;
    }
    
    public Trigger[] getTriggers()
    {
        return triggers;
    }
    
    public void setTriggers(Trigger[] triggers)
    {
        this.triggers = triggers;
    }
    
    public HashMap<Integer, String> getTargets()
    {
        return targets;
    }
}
