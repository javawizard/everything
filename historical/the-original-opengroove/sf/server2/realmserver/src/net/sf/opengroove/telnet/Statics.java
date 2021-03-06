package net.sf.opengroove.telnet;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Contains one method, {@link #run()}, which should be called before
 * OpenGroove starts, or before any classes that use OpenGroove start.
 * Currently, it just activates the license for the Jidesoft component
 * libraries.
 * 
 * @author Alexander Boyd
 * 
 */
public class Statics
{
    public static void run()
    {
        com.jidesoft.utils.Lm.verifyLicense(
            "Trivergia Technologies, LLC", "OpenGroove",
            "pfMGeSDFibz9ga5TsECdMuheO6AV0Jw2");
        Security.addProvider(new BouncyCastleProvider());        
    }
}
