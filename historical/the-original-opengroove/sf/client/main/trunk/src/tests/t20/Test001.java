package tests.t20;

import java.math.BigInteger;
import java.security.SecureRandom;

import net.sf.opengroove.common.utils.DataUtils;
import net.sf.opengroove.common.utils.MathUtils;
import net.sf.opengroove.common.utils.StringUtils;

public class Test001
{
    private static final String RADIX_ALPHANUMERIC_MIXED =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        BigInteger number = new BigInteger(256, new SecureRandom());
        System.out.println(number.toString());
        System.out.println(number.toString(16));
        System.out.println(MathUtils.toString(number, MathUtils.RADIX_ALPHA_CLEAR));
        System.out.println(number.toString(36));
        System.out.println(MathUtils.toString(number, MathUtils.RADIX_ALPHANUMERIC));
        System.out.println(MathUtils.toString(number, RADIX_ALPHANUMERIC_MIXED));
    }
}
