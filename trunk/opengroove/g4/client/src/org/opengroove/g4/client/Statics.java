package org.opengroove.g4.client;

/**
 * Contains one method, {@link #run()}, which should be called before OpenGroove
 * starts, or before any classes that use OpenGroove start. Currently, it just
 * activates the license for the Jidesoft component libraries.
 * 
 * @author Alexander Boyd
 * 
 */
public class Statics
{
    private static boolean wasRun = false;
    
    public static synchronized void run()
    {
        if (wasRun)
            return;
        wasRun = true;
        com.jidesoft.utils.Lm.verifyLicense("Trivergia Technologies, LLC",
            "OpenGroove", "pfMGeSDFibz9ga5TsECdMuheO6AV0Jw2");
    }
}
