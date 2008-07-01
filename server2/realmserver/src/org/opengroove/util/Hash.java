package org.opengroove.util;

import java.math.BigInteger;

import DE.knp.MicroCrypt.Sha512;

public class Hash
{
    public static String hash(byte[] bytes)
    {
        Sha512 encoder = new Sha512();
        encoder.append(bytes);
        return hexcode(encoder.finish());
    }
    
    public static String hexcode(byte[] bytes)
    {
        BigInteger i = new BigInteger(bytes);
        return i.toString(16);
    }
    
}
