package net.sf.opengroove.common.security;

import java.math.BigInteger;

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
        String hashUnsplit = hashInt.toString(36);
        hashUnsplit = hashUnsplit.toUpperCase();
        String hash = "";
        for (int i = 0; i < hashUnsplit.length(); i += 4)
        {
            String section = hashUnsplit.substring(i, Math
                .min(i + 4, hashUnsplit.length()));
            if (i != 0)
                hash += "-";
            hash += section;
        }
        return hash;
    }
}
