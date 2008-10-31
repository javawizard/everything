package tests;

import java.math.BigInteger;
import java.security.SecureRandom;

import net.sf.opengroove.client.ui.TestFrame;
import net.sf.opengroove.common.security.UserFingerprint;
/**
 * A class for testing fingerprints
 * @author Alexander Boyd
 *
 */
public class Test041
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println(UserFingerprint.fingerprint(
            new BigInteger(3072, new SecureRandom()),
            new BigInteger(3072, new SecureRandom()),
            new BigInteger(3072, new SecureRandom()),
            new BigInteger(3072, new SecureRandom())));
    }
    
}
