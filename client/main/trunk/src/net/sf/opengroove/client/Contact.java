package net.sf.opengroove.client;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;

public class Contact implements Serializable
{
    private static final long serialVersionUID = -2910467169227988997L;
    
    private String realm;
    private String username;
    private String realName;
    private String localName;
    private BigInteger rsaEncPub;
    private BigInteger rasEncMod;
    private BigInteger rsaSigPub;
    private BigInteger rsaSigMod;
    private boolean isUserContact;
    private boolean isUserVerified;
}
