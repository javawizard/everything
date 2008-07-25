package net.sf.opengroove.client.com;

import java.math.BigInteger;
import java.net.Socket;

public class Communicator
{
    private BigInteger serverRsaPublic;
    private BigInteger serverRsaMod;
    private String serverHost;
    private int serverPort;
    private Socket socket;
    
    public Communicator(String serverHost, int serverPort,
        BigInteger serverRsaPublic, BigInteger serverRsaMod)
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serverRsaPublic = serverRsaPublic;
        this.serverRsaMod = serverRsaMod;
    }
    
    private void performHandshake()
    {
        
    }
}
