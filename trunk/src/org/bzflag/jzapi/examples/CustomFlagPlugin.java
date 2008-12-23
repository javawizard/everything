package org.bzflag.jzapi.examples;

import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.BzfsAPI.FlagQuality;
import org.bzflag.jzapi.BzfsAPI.ShotType;
import org.bzflag.jzapi.internal.SimpleBind;

public class CustomFlagPlugin
{
    public static void load(String args)
    {
        SimpleBind
            .bz_RegisterCustomFlag(
                "CF",
                "Custom flag",
                "This is a custom flag. It gives you shockwave shots.",
                ShotType.shockwave, FlagQuality.good);
    }
    
    public static void unload()
    {
        
    }
}
