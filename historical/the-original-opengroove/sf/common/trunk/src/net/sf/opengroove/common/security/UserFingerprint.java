package net.sf.opengroove.common.security;

import java.math.BigInteger;

import net.sf.opengroove.common.utils.MathUtils;

public class UserFingerprint
{
    public static String fingerprint(BigInteger encPub,
        BigInteger encMod, BigInteger sigPub,
        BigInteger sigMod)
    {
        byte[] encPubHash = Hash.hashRaw(encPub
            .toByteArray());
        byte[] encModHash = Hash.hashRaw(encMod
            .toByteArray());
        byte[] sigPubHash = Hash.hashRaw(sigPub
            .toByteArray());
        byte[] sigModHash = Hash.hashRaw(sigMod
            .toByteArray());
        byte[] concat = new byte[encPubHash.length
            + encModHash.length + sigPubHash.length
            + sigModHash.length];
        System.arraycopy(encPubHash, 0, concat, 0,
            encPubHash.length);
        System.arraycopy(encModHash, 0, concat,
            encPubHash.length, encModHash.length);
        System.arraycopy(sigPubHash, 0, concat,
            encPubHash.length + encModHash.length,
            sigPubHash.length);
        System.arraycopy(sigModHash, 0, concat,
            encPubHash.length + encModHash.length
                + sigPubHash.length, sigModHash.length);
        byte[] hashBytes = Hash.hashRaw(concat);
        BigInteger hashInt = new BigInteger(1, hashBytes);
        String hashUnsplit = MathUtils.toString(hashInt,
            MathUtils.RADIX_ALPHA_CLEAR);
        hashUnsplit = hashUnsplit.toUpperCase();
        String hash = "";
        for (int i = 0; i < Math.min(hashUnsplit.length(),
            25); i += 5)
        {
            String section = hashUnsplit.substring(i, Math
                .min(i + 5, hashUnsplit.length()));
            if (i != 0)
                hash += "-";
            hash += section;
        }
        return hash;
    }
    
    public static String fingerprint(byte[] data)
    {
        byte[] hashBytes = Hash.hashRaw(data);
        BigInteger hashInt = new BigInteger(1, hashBytes);
        String hashUnsplit = hashInt.toString(36);
        hashUnsplit = hashUnsplit.toUpperCase();
        String hash = "";
        for (int i = 0; i < Math.min(hashUnsplit.length(),
            30); i += 5)
        {
            String section = hashUnsplit.substring(i, Math
                .min(i + 5, hashUnsplit.length()));
            if (i != 0)
                hash += "-";
            hash += section;
        }
        return hash;
    }
    
    public static String fingerprint(String data)
    {
        return fingerprint(data.getBytes());
    }
    
}
